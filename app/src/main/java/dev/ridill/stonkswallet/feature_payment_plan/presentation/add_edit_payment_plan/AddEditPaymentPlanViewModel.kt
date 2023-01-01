package dev.ridill.stonkswallet.feature_payment_plan.presentation.add_edit_payment_plan

import androidx.lifecycle.*
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.domain.model.UiText
import dev.ridill.stonkswallet.core.util.Constants
import dev.ridill.stonkswallet.core.util.toDoubleOrZero
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentCategory
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentPlan
import dev.ridill.stonkswallet.feature_payment_plan.domain.payment_reminder.PaymentPlanReminderManager
import dev.ridill.stonkswallet.feature_payment_plan.domain.repository.AddEditPaymentPlanRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditPaymentPlanViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repo: AddEditPaymentPlanRepository,
    private val paymentPlanReminderManager: PaymentPlanReminderManager
) : ViewModel(), AddEditPaymentPlanActions {

    private val planId = -1L

    //        AddEditPaymentPlanScreenSpec.getPaymentPlanIdFromSavedStateHandle(savedStateHandle)
    val isEditMode = false
//        AddEditPaymentPlanScreenSpec.isEditMode(planId)

    private val paymentPlan = savedStateHandle.getLiveData<PaymentPlan>(KEY_BILL_LIVE_DATA)
    val name = paymentPlan.map { it.name }
    val amount = paymentPlan.map { it.amount }
    val repeatPeriod = paymentPlan.map { it.repeatMonthsPeriod }
    private val category = paymentPlan.map { it.category }.distinctUntilChanged()
    private val dueDateMillis = paymentPlan.map { it.dueDateFormatted }.distinctUntilChanged()

    private val showCategorySelection = savedStateHandle.getLiveData("showCategorySelection", false)

    private val showDeletionConfirmation =
        savedStateHandle.getLiveData("showDeletionConfirmation", false)

    private val showDatePicker = savedStateHandle.getStateFlow(KEY_SHOW_DATE_PICKER, false)

    val state = combineTuple(
        showCategorySelection.asFlow(),
        showDeletionConfirmation.asFlow(),
        category.asFlow(),
        dueDateMillis.asFlow(),
        showDatePicker
    ).map { (
                showCategorySelection,
                showDeletionConfirmation,
                category,
                payByDate,
                showDatePicker
            ) ->
        AddEditPaymentPlanScreenState(
            showCategorySelection = showCategorySelection,
            showDeletionConfirmation = showDeletionConfirmation,
            dueDate = payByDate,
            category = category,
            showDatePicker = showDatePicker
        )
    }.asLiveData()

    init {
        if (!savedStateHandle.contains(KEY_BILL_LIVE_DATA)) {
            if (planId != null && isEditMode) viewModelScope.launch {
                paymentPlan.value = repo.getPlan(planId)
            } else {
                paymentPlan.value = PaymentPlan.DEFAULT
            }
        }
    }

    private val eventsChannel = Channel<AddEditPaymentPlanEvent>()
    val events get() = eventsChannel.receiveAsFlow()

    override fun onNameChange(value: String) {
        if (value.length > Constants.BILL_DESCRIPTION_MAX_LENGTH) return
        paymentPlan.value = paymentPlan.value?.copy(
            name = value
        )
    }

    override fun onAmountChange(value: String) {
        paymentPlan.value = paymentPlan.value?.copy(
            amount = value
        )
    }

    override fun onCategoryClick() {
        showCategorySelection.value = true
    }

    override fun onCategorySelectionDismiss() {
        showCategorySelection.value = false
    }

    override fun onCategorySelect(category: PaymentCategory) {
        paymentPlan.value = paymentPlan.value?.copy(
            category = category
        )
        showCategorySelection.value = false
    }

    override fun onDatePickerClick() {
        savedStateHandle[KEY_SHOW_DATE_PICKER] = true
    }

    override fun onDatePickerDismiss() {
        savedStateHandle[KEY_SHOW_DATE_PICKER] = false
    }

    override fun onDueDateChange(dateMillis: Long) {
        paymentPlan.value = paymentPlan.value?.copy(
            nextDueMillis = dateMillis
        )
    }

    override fun onRepeatMonthsPeriodChange(months: String) {
        paymentPlan.value = paymentPlan.value?.copy(
            repeatMonthsPeriod = months.toIntOrNull()
        )
    }

    override fun onSave() {
        val bill = paymentPlan.value ?: return
        viewModelScope.launch {
            val description = bill.name.trim()
            if (description.isEmpty()) {
                eventsChannel.send(AddEditPaymentPlanEvent.ShowErrorMessage(UiText.StringResource(R.string.error_invalid_description)))
                return@launch
            }
            val amount = bill.amount.trim().toDoubleOrZero()
            if (amount <= 0.0) {
                eventsChannel.send(AddEditPaymentPlanEvent.ShowErrorMessage(UiText.StringResource(R.string.error_invalid_amount)))
                return@launch
            }
            repo.savePlan(bill)
            paymentPlanReminderManager.scheduleReminder()
            eventsChannel.send(
                if (isEditMode) AddEditPaymentPlanEvent.PaymentPlanUpdated
                else AddEditPaymentPlanEvent.PaymentPlanAdded
            )
        }
    }

    override fun onDeleteClick() {
        showDeletionConfirmation.value = true
    }

    override fun onDeleteDismiss() {
        showDeletionConfirmation.value = false
    }

    override fun onDeleteConfirm() {
        planId?.let {
            viewModelScope.launch {
                repo.delete(it)
                if (!repo.doAnyPlansExist()) paymentPlanReminderManager.stopReminder()
                showDeletionConfirmation.value = false
                eventsChannel.send(AddEditPaymentPlanEvent.PaymentPlanDeleted)
            }
        }
    }

    sealed class AddEditPaymentPlanEvent {
        data class ShowErrorMessage(val message: UiText) : AddEditPaymentPlanEvent()
        object PaymentPlanAdded : AddEditPaymentPlanEvent()
        object PaymentPlanUpdated : AddEditPaymentPlanEvent()
        object PaymentPlanDeleted : AddEditPaymentPlanEvent()
    }
}

private const val KEY_BILL_LIVE_DATA = "KEY_BILL_LIVE_DATA"
private const val KEY_SHOW_DATE_PICKER = "KEY_SHOW_DATE_PICKER"

const val ADD_EDIT_BILL_RESULT = "ADD_EDIT_BILL_RESULT"
const val RESULT_BILL_ADDED = "RESULT_BILL_ADDED"
const val RESULT_BILL_UPDATED = "RESULT_BILL_UPDATED"
const val RESULT_BILL_DELETED = "RESULT_BILL_DELETED"
