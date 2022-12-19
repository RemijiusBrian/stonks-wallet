package dev.ridill.stonkswallet.feature_payment_plan.domain.repository

import dev.ridill.stonkswallet.feature_payment_plan.domain.model.PaymentPlan

interface AddEditPaymentPlanRepository {

    suspend fun getPlan(id: Long): PaymentPlan?

    suspend fun savePlan(paymentPlan: PaymentPlan)

    suspend fun delete(id: Long)

    suspend fun doAnyPlansExist(): Boolean
}