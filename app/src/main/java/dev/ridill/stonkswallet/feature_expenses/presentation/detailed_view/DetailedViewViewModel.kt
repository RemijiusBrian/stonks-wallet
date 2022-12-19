package dev.ridill.stonkswallet.feature_expenses.presentation.detailed_view

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.domain.model.UiText
import dev.ridill.stonkswallet.core.ui.util.TextUtil
import dev.ridill.stonkswallet.core.util.DateUtil
import dev.ridill.stonkswallet.feature_expenses.domain.repository.DetailedViewRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Month
import javax.inject.Inject

@HiltViewModel
class DetailedViewViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repo: DetailedViewRepository
) : ViewModel(), DetailedViewActions {

    private val yearsList = repo.getYearsList()
    private val selectedYear = savedStateHandle.getStateFlow(
        KEY_SELECTED_YEAR, DateUtil.currentDateTime().year.toString()
    )
    private val selectedMonth = savedStateHandle.getStateFlow(
        KEY_SELECTED_MONTH, DateUtil.currentDate().monthValue
    )
    private val selectedDate = combineTuple(
        selectedYear,
        selectedMonth
    ).map { (year, month) ->
        val paddedMonth = month.toString().padStart(2, '0')
        "$paddedMonth-$year"
    }

    private val totalExpenditure = selectedDate.flatMapLatest { date ->
        repo.getTotalExpenditure(date)
    }

    private val tagOverviews = combineTuple(
        totalExpenditure,
        selectedDate
    ).flatMapLatest { (expenditure, date) ->
        repo.getTagOverviews(expenditure, date)
    }
    private val selectedTag = savedStateHandle.getStateFlow<String?>(KEY_SELECTED_TAG, null)

    private val expenses = combineTuple(
        selectedTag,
        selectedDate
    ).flatMapLatest { (tag, date) ->
        repo.getExpenses(tag, date)
    }

    private val showTagDeletionConfirmation =
        savedStateHandle.getStateFlow(KEY_SHOW_TAG_DELETE_CONFIRMATION, false)

    private val multiSelectionModeActive =
        savedStateHandle.getStateFlow(KEY_MULTI_SELECTION_ACTIVE, false)
    private val selectedExpenseIds = savedStateHandle.getStateFlow<List<Long>>(
        KEY_SELECTED_EXPENSE_IDS, emptyList()
    )
    private val expenseSelectionState = combineTuple(
        expenses,
        selectedExpenseIds
    ).map { (expenses, selectedIds) ->
        val expenseIds = expenses.map { it.id }
        when {
            expenseIds.all { it in selectedIds } -> ToggleableState.On
            expenseIds.none { it in selectedIds } -> ToggleableState.Off
            else -> ToggleableState.Indeterminate
        }
    }

    private val showTagInput = savedStateHandle.getStateFlow(KEY_SHOW_TAG_INPUT, false)

    private val showExpenseDeletionConfirmation =
        savedStateHandle.getStateFlow(KEY_SHOW_EXPENSE_DELETE_CONFIRMATION, false)

    val state = combineTuple(
        tagOverviews,
        selectedTag,
        totalExpenditure,
        showTagInput,
        yearsList,
        selectedYear,
        selectedMonth,
        expenses,
        showTagDeletionConfirmation,
        multiSelectionModeActive,
        selectedExpenseIds,
        expenseSelectionState,
        showExpenseDeletionConfirmation
    ).map { (
                tagOverviews,
                selectedTag,
                totalExpenditure,
                showTagInput,
                yearsList,
                selectedYear,
                selectedMonth,
                expenses,
                showTagDeletionConfirmation,
                multiSelectionModeActive,
                selectedExpenseIds,
                expenseSelectionState,
                showExpenseDeleteConfirmation
            ) ->
        DetailedViewState(
            tagOverviews = tagOverviews,
            selectedTag = selectedTag,
            totalExpenditure = TextUtil.formatAmountWithCurrency(totalExpenditure),
            showTagInput = showTagInput,
            yearsList = yearsList,
            selectedYear = selectedYear,
            selectedMonth = selectedMonth,
            expenses = expenses,
            showTagDeletionConfirmation = showTagDeletionConfirmation,
            multiSelectionModeActive = multiSelectionModeActive,
            selectedExpenseIds = selectedExpenseIds,
            expenseSelectionState = expenseSelectionState,
            showExpenseDeleteConfirmation = showExpenseDeleteConfirmation
        )
    }.asLiveData()

    init {
        collectExpenseYears()
        collectTagOverviews()
    }

    private val eventsChannel = Channel<DetailedViewEvent>()
    val events get() = eventsChannel.receiveAsFlow()

    private fun collectExpenseYears() = viewModelScope.launch {
        repo.getYearsList().collectLatest { years ->
            savedStateHandle[KEY_SELECTED_YEAR] =
                selectedYear.value.ifEmpty { years.firstOrNull()?.toString() }.orEmpty()
        }
    }

    private fun collectTagOverviews() = viewModelScope.launch {
        combineTuple(
            totalExpenditure,
            selectedDate
        ).flatMapLatest { (expenditure, date) ->
            repo.getTagOverviews(expenditure, date)
        }.collectLatest { overviews ->
            val tags = overviews.map { it.tag }
            savedStateHandle[KEY_SELECTED_TAG] = selectedTag.value.takeIf { it in tags }
        }
    }

    private suspend fun assignTagToExpenses(tag: String?) {
        val selectedExpenses = state.value?.selectedExpenseIds ?: return
        repo.tagExpenses(tag, selectedExpenses)
        disableMultiSelectionMode()
    }

    override fun onTagSelect(tag: String) {
        if (multiSelectionModeActive.value) viewModelScope.launch {
            assignTagToExpenses(tag)
            eventsChannel.send(
                DetailedViewEvent.ShowUiMessage(
                    UiText.StringResource(R.string.expenses_tagged_as, tag)
                )
            )
        } else {
            savedStateHandle[KEY_SELECTED_TAG] = tag.takeIf { it != selectedTag.value }
        }
    }

    private var deletionTag: String? = null
    override fun onTagDelete(tag: String) {
        deletionTag = tag
        savedStateHandle[KEY_SHOW_TAG_DELETE_CONFIRMATION] = true
    }

    override fun onTagDeleteDismiss() {
        deletionTag = null
        savedStateHandle[KEY_SHOW_TAG_DELETE_CONFIRMATION] = false
    }

    override fun onTagDeleteConfirm() {
        deletionTag?.let {
            viewModelScope.launch {
                repo.deleteTag(it)
                savedStateHandle[KEY_SHOW_TAG_DELETE_CONFIRMATION] = false
                eventsChannel.send(DetailedViewEvent.ShowUiMessage(UiText.StringResource(R.string.tag_deleted)))
            }
        }
    }

    override fun onNewTagClick() {
        savedStateHandle[KEY_SHOW_TAG_INPUT] = true
    }

    override fun onNewTagDismiss() {
        savedStateHandle[KEY_SHOW_TAG_INPUT] = false
    }

    override fun onNewTagConfirm(name: String, color: Color) {
        viewModelScope.launch {
            if (name.isEmpty()) {
                eventsChannel.send(
                    DetailedViewEvent.ShowUiMessage(
                        UiText.StringResource(R.string.error_invalid_tag_name), true
                    )
                )
                return@launch
            }
            repo.createTag(name, color)
            savedStateHandle[KEY_SHOW_TAG_INPUT] = false
            eventsChannel.send(DetailedViewEvent.ShowUiMessage(UiText.StringResource(R.string.tag_created)))
        }
    }

    override fun onYearSelect(year: String) {
        savedStateHandle[KEY_SELECTED_YEAR] = year
    }

    override fun onMonthSelect(month: Month) {
        savedStateHandle[KEY_SELECTED_MONTH] = month.value
    }

    override fun onExpenseLongClick(id: Long) {
        if (!multiSelectionModeActive.value) {
            savedStateHandle[KEY_SELECTED_EXPENSE_IDS] = listOf(id)
            savedStateHandle[KEY_SELECTED_TAG] = null
            savedStateHandle[KEY_MULTI_SELECTION_ACTIVE] = true
        } else {
            disableMultiSelectionMode()
        }
    }

    private fun disableMultiSelectionMode() {
        savedStateHandle[KEY_SELECTED_EXPENSE_IDS] = emptyList<Long>()
        savedStateHandle[KEY_MULTI_SELECTION_ACTIVE] = false
    }

    override fun onExpenseSelectionToggle(id: Long) {
        addOrRemoveIdFromSelectedList(id)
        if (selectedExpenseIds.value.isEmpty()) disableMultiSelectionMode()
    }

    private fun addOrRemoveIdFromSelectedList(id: Long) {
        if (id in selectedExpenseIds.value) {
            savedStateHandle[KEY_SELECTED_EXPENSE_IDS] = selectedExpenseIds.value - id
        } else {
            savedStateHandle[KEY_SELECTED_EXPENSE_IDS] = selectedExpenseIds.value + id
        }
    }

    override fun onDismissMultiSelectionMode() {
        disableMultiSelectionMode()
    }

    override fun onSelectionStateChange(currentState: ToggleableState) {
        when (currentState) {
            ToggleableState.On -> {
                savedStateHandle[KEY_SELECTED_EXPENSE_IDS] = emptyList<Long>()
            }
            ToggleableState.Off -> {
                state.value?.expenses?.firstOrNull()?.id?.let {
                    savedStateHandle[KEY_SELECTED_EXPENSE_IDS] = listOf(it)
                }
            }
            ToggleableState.Indeterminate -> {
                state.value?.expenses?.let { expenses ->
                    savedStateHandle[KEY_SELECTED_EXPENSE_IDS] = expenses.map { it.id }
                }
            }
        }
    }

    override fun onDeleteExpensesClick() {
        savedStateHandle[KEY_SHOW_EXPENSE_DELETE_CONFIRMATION] = true
    }

    override fun onDeleteExpensesDismissed() {
        savedStateHandle[KEY_SHOW_EXPENSE_DELETE_CONFIRMATION] = false
    }

    override fun onDeleteExpensesConfirmed() {
        state.value?.selectedExpenseIds?.let {
            viewModelScope.launch {
                repo.deleteExpenses(it)
                savedStateHandle[KEY_SHOW_EXPENSE_DELETE_CONFIRMATION] = false
                disableMultiSelectionMode()
                eventsChannel.send(DetailedViewEvent.ShowUiMessage(UiText.StringResource(R.string.selected_expenses_deleted)))
            }
        }
    }

    override fun onUntagExpensesClick() {
        viewModelScope.launch {
            assignTagToExpenses(null)
            eventsChannel.send(DetailedViewEvent.ShowUiMessage(UiText.StringResource(R.string.expenses_untagged)))
        }
    }

    sealed class DetailedViewEvent {
        data class ShowUiMessage(val message: UiText, val isError: Boolean = false) :
            DetailedViewEvent()
    }
}

private const val KEY_SELECTED_TAG = "KEY_SELECTED_TAG"
private const val KEY_SELECTED_YEAR = "KEY_SELECTED_YEAR"
private const val KEY_SELECTED_MONTH = "KEY_SELECTED_MONTH"
private const val KEY_SHOW_TAG_DELETE_CONFIRMATION = "KEY_SHOW_TAG_DELETE_CONFIRMATION"
private const val KEY_SHOW_EXPENSE_DELETE_CONFIRMATION = "KEY_SHOW_EXPENSE_DELETE_CONFIRMATION"
private const val KEY_MULTI_SELECTION_ACTIVE = "KEY_MULTI_SELECTION_ACTIVE"
private const val KEY_SELECTED_EXPENSE_IDS = "KEY_SELECTED_EXPENSE_IDS"
private const val KEY_SHOW_TAG_INPUT = "KEY_SHOW_TAG_INPUT"