package dev.ridill.stonkswallet.feature_payment_plan.data.repository

import dev.ridill.stonkswallet.feature_payment_plan.data.mapper.toEntity
import dev.ridill.stonkswallet.feature_payment_plan.data.mapper.toPaymentPlan
import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentPlan
import dev.ridill.stonkswallet.feature_payment_plan.domain.repository.AddEditPaymentPlanRepository
import dev.ridill.stonkswallet.feature_payment_plan.domain.repository.PaymentPlanRepository

class AddEditPaymentPlanRepositoryImpl(
    private val billsRepo: PaymentPlanRepository
) : AddEditPaymentPlanRepository {

    override suspend fun getPlan(id: Long): PaymentPlan? =
        billsRepo.getPlanById(id)?.toPaymentPlan()

    override suspend fun savePlan(paymentPlan: PaymentPlan) =
        billsRepo.cachePaymentPlan(paymentPlan.toEntity())

    override suspend fun delete(id: Long) =
        billsRepo.deletePaymentPlan(id)

    override suspend fun doAnyPlansExist(): Boolean =
        billsRepo.getTotalPaymentPlanCount() > 0
}