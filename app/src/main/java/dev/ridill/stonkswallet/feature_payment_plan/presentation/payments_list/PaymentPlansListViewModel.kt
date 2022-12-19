package dev.ridill.stonkswallet.feature_payment_plan.presentation.payments_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.stonkswallet.R
import dev.ridill.stonkswallet.core.domain.model.UiText
import dev.ridill.stonkswallet.core.util.DateUtil
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentPlanExpense
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentStatus
import dev.ridill.stonkswallet.feature_payment_plan.domain.repository.PaymentPlanListRepository
import dev.ridill.stonkswallet.feature_payment_plan.presentation.add_edit_payment_plan.RESULT_BILL_ADDED
import dev.ridill.stonkswallet.feature_payment_plan.presentation.add_edit_payment_plan.RESULT_BILL_DELETED
import dev.ridill.stonkswallet.feature_payment_plan.presentation.add_edit_payment_plan.RESULT_BILL_UPDATED
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentPlansListViewModel @Inject constructor(
    private val repo: PaymentPlanListRepository
) : ViewModel(), PaymentPlansListActions {

    private val currentDateMillis = DateUtil.currentEpochMillis()
    private val paymentPlans = repo.getPlansByCategory()
    private val payments = repo.getPaymentsByStatus(currentDateMillis)

    private val eventsChannel = Channel<PaymentPlansListEvent>()
    val events get() = eventsChannel.receiveAsFlow()

    val state = combineTuple(
        paymentPlans,
        payments
    ).map { (
                bills,
                billPayments
            ) ->
        PaymentsListScreenState(
            billsList = bills,
            paymentsPlanExpense = billPayments
        )
    }.asLiveData()

    override fun onMarkAsPaidClick(payment: PaymentPlanExpense) {
        if (payment.status == PaymentStatus.PAID) return
        viewModelScope.launch {
            repo.markAsPaid(payment)
            eventsChannel.send(PaymentPlansListEvent.ShowUiMessage(UiText.StringResource(R.string.payment_plan_marked_as_paid_message)))
        }
    }

    fun onAddBillResult(result: String) {
        val message = when (result) {
            RESULT_BILL_ADDED -> R.string.message_payment_plan_added
            RESULT_BILL_UPDATED -> R.string.message_payment_plan_updated
            RESULT_BILL_DELETED -> R.string.message_payment_plan_deleted
            else -> return
        }
        viewModelScope.launch {
            eventsChannel.send(PaymentPlansListEvent.ShowUiMessage(UiText.StringResource(message)))
        }
    }

    override fun onPlanClick(id: Long) {
        viewModelScope.launch {
            eventsChannel.send(PaymentPlansListEvent.NavigateToAddEditPaymentPlanScreen(id))
        }
    }

    sealed class PaymentPlansListEvent {
        data class ShowUiMessage(val message: UiText) : PaymentPlansListEvent()
        data class NavigateToAddEditPaymentPlanScreen(val id: Long) : PaymentPlansListEvent()
    }
}