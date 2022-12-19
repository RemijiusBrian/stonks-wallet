package dev.ridill.stonkswallet.feature_expenses.presentation.add_edit_expense

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.*
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.domain.model.UiText
import dev.ridill.stonkswallet.core.notification.NotificationHelper
import dev.ridill.stonkswallet.core.ui.navigation.screen_specs.AddEditExpenseScreenSpec
import dev.ridill.stonkswallet.core.util.Constants
import dev.ridill.stonkswallet.core.util.toDoubleOrZero
import dev.ridill.stonkswallet.feature_expenses.domain.model.Expense
import dev.ridill.stonkswallet.feature_expenses.domain.model.Tag
import dev.ridill.stonkswallet.feature_expenses.domain.repository.AddEditExpenseRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val notificationHelper: NotificationHelper<Expense>,
    private val repo: AddEditExpenseRepository
) : ViewModel(), AddEditExpenseActions {

    private val expenseId =
        AddEditExpenseScreenSpec.getExpenseIdFromSavedStateHandle(savedStateHandle)
    val editMode = AddEditExpenseScreenSpec.isEditMode(expenseId)

    private val expense = savedStateHandle.getStateFlow<Expense>(KEY_EXPENSE, Expense.DEFAULT)
    val amountInput = savedStateHandle.getLiveData(KEY_AMOUNT_INPUT, "")
    val noteInput = savedStateHandle.getLiveData(KEY_NOTE_INPUT, "")

    private val tagsList = repo.getTags()
    private val selectedTag = savedStateHandle.getStateFlow<Tag?>(KEY_SELECTED_TAG, null)

    private val savable = combineTuple(
        expense,
        amountInput.asFlow(),
        noteInput.asFlow(),
        selectedTag
    ).map { (
                expense,
                amountInput,
                noteInput,
                selectedTag
            ) ->
        expense.amount.toString() != amountInput ||
                expense.note != noteInput ||
                selectedTag?.name != expense.tag?.name
    }.distinctUntilChanged()

    private val showTagInput = savedStateHandle.getStateFlow(KEY_SHOW_TAG_INPUT, false)

    private val showDeleteConfirmation =
        savedStateHandle.getStateFlow(KEY_SHOW_DELETE_CONFIRMATION, false)

    private val showDiscardChangesMessage =
        savedStateHandle.getStateFlow(KEY_SHOW_DISCARD_CHANGES_MESSAGE, false)

    val state = combineTuple(
        tagsList,
        selectedTag,
        savable,
        showDeleteConfirmation,
        showTagInput,
        showDiscardChangesMessage
    ).map { (
                tagsList,
                selectedTag,
                savable,
                showDeleteConfirmation,
                showTagInput,
                showDiscardChangesMessage
            ) ->
        AddEditExpenseState(
            tagsList = tagsList,
            selectedTag = selectedTag,
            savable = savable,
            showDeleteConfirmation = showDeleteConfirmation,
            showTagInput = showTagInput,
            showDiscardChangesMessage = showDiscardChangesMessage
        )
    }.asLiveData()

    init {
        onInit()
    }

    private fun onInit() = viewModelScope.launch {
        if (expenseId != null && editMode) {
            repo.getExpense(expenseId)?.let {
                savedStateHandle[KEY_EXPENSE] = it
                amountInput.value = it.amount.toString()
                noteInput.value = it.note
                savedStateHandle[KEY_SELECTED_TAG] = it.tag
                notificationHelper.dismissNotification(it.id.toInt())
            }
        } else {
            savedStateHandle[KEY_EXPENSE] = Expense.DEFAULT
        }
    }

    private val eventsChannel = Channel<AddEditExpenseEvent>()
    val events get() = eventsChannel.receiveAsFlow()

    override fun onAmountChange(value: String) {
        amountInput.value = value
    }

    override fun onNoteChange(value: String) {
        if (value.length > Constants.EXPENSE_NOTE_MAX_LENGTH) return
        noteInput.value = value
    }

    override fun onTagSelect(tag: Tag) {
        savedStateHandle[KEY_SELECTED_TAG] = tag.takeIf { it.name != selectedTag.value?.name }
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
                    AddEditExpenseEvent.ShowUiMessage(
                        UiText.StringResource(R.string.error_invalid_tag_name),
                        true
                    )
                )
                return@launch
            }
            val createdTag = repo.createTag(name, color)
            savedStateHandle[KEY_SELECTED_TAG] = createdTag
            savedStateHandle[KEY_SHOW_TAG_INPUT] = false
            eventsChannel.send(AddEditExpenseEvent.ShowUiMessage(UiText.StringResource(R.string.tag_created)))
        }
    }

    override fun onDeleteClick() {
        savedStateHandle[KEY_SHOW_DELETE_CONFIRMATION] = true
    }

    override fun onDeleteDismiss() {
        savedStateHandle[KEY_SHOW_DELETE_CONFIRMATION] = false
    }

    override fun onDeleteConfirm() {
        viewModelScope.launch {
            val expense = expense.value
            repo.deleteExpense(expense.id, expense.paymentPlanId)
            savedStateHandle[KEY_SHOW_DELETE_CONFIRMATION] = false
            eventsChannel.send(AddEditExpenseEvent.ExpenseDeleted)
        }
    }

    override fun onSave() {
        viewModelScope.launch {
            val amount = amountInput.value.orEmpty().toDoubleOrZero()
            if (amount <= 0.0) {
                eventsChannel.send(
                    AddEditExpenseEvent.ShowUiMessage(
                        UiText.StringResource(R.string.error_invalid_amount),
                        true
                    )
                )
                return@launch
            }
            val note = noteInput.value?.trim().orEmpty().ifEmpty {
                eventsChannel.send(
                    AddEditExpenseEvent.ShowUiMessage(
                        UiText.StringResource(R.string.error_invalid_note),
                        true
                    )
                )
                return@launch
            }
            repo.saveExpense(
                id = expense.value.id,
                note = note,
                amount = amount,
                tag = selectedTag.value?.name.takeIf { it?.isNotEmpty() == true },
                dateMillis = expense.value.dateMillis
            )
            eventsChannel.send(
                if (editMode) AddEditExpenseEvent.ExpenseUpdated
                else AddEditExpenseEvent.ExpenseCreated
            )
        }
    }

    override fun onTryNavigateUp() {
        if (state.value?.savable == true) {
            savedStateHandle[KEY_SHOW_DISCARD_CHANGES_MESSAGE] = true
            return
        }
        viewModelScope.launch {
            eventsChannel.send(AddEditExpenseEvent.NavigateUp)
        }
    }

    override fun onDiscardChangedDismissed() {
        savedStateHandle[KEY_SHOW_DISCARD_CHANGES_MESSAGE] = false
    }

    override fun onDiscardChangedConfirmed() {
        viewModelScope.launch {
            savedStateHandle[KEY_SHOW_DISCARD_CHANGES_MESSAGE] = false
            eventsChannel.send(AddEditExpenseEvent.NavigateUp)
        }
    }

    sealed class AddEditExpenseEvent {
        object ExpenseCreated : AddEditExpenseEvent()
        object ExpenseUpdated : AddEditExpenseEvent()
        object ExpenseDeleted : AddEditExpenseEvent()
        data class ShowUiMessage(val message: UiText, val error: Boolean = false) :
            AddEditExpenseEvent()

        object NavigateUp : AddEditExpenseEvent()
    }
}

private const val KEY_EXPENSE = "KEY_EXPENSE"
private const val KEY_AMOUNT_INPUT = "KEY_AMOUNT_INPUT"
private const val KEY_NOTE_INPUT = "KEY_NOTE_INPUT"
private const val KEY_SELECTED_TAG = "KEY_SELECTED_TAG"
private const val KEY_SHOW_DELETE_CONFIRMATION = "KEY_SHOW_DELETE_CONFIRMATION"
private const val KEY_SHOW_TAG_INPUT = "KEY_SHOW_TAG_INPUT"
private const val KEY_SHOW_DISCARD_CHANGES_MESSAGE = "KEY_SHOW_DISCARD_CHANGED_MESSAGE"

const val ADD_EDIT_EXPENSE_RESULT = "ADD_EDIT_EXPENSE_RESULT"
const val RESULT_EXPENSE_ADDED = "RESULT_EXPENSE_ADDED"
const val RESULT_EXPENSE_UPDATED = "RESULT_EXPENSE_UPDATED"
const val RESULT_EXPENSE_DELETED = "RESULT_EXPENSE_DELETED"