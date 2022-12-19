package dev.ridill.stonkswallet.feature_payment_plan.domain.model

import androidx.annotation.DrawableRes
import dev.ridill.stonkswallet.R

enum class PaymentCategory(
    @DrawableRes val icon: Int,
    val label: String
) {
    ELECTRICITY(R.drawable.ic_electricity, "Electricity"),
    WATER(R.drawable.ic_water_drops, "Water"),
    BROADBAND(R.drawable.ic_wifi_cloud, "Broadband"),
    DIGITAL_TV(R.drawable.ic_tv, "Digital TV"),
    SUBSCRIPTION(R.drawable.ic_subscription, "Subscription"),
    MISC(R.drawable.ic_box, "Miscellaneous")
}