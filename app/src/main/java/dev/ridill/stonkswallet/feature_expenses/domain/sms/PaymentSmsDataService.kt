package dev.ridill.stonkswallet.feature_expenses.domain.sms

import javax.inject.Inject

class PaymentSmsDataService @Inject constructor() {

    fun isMerchantSms(address: String?): Boolean =
        address?.matches(MERCHANT_SENDER_PATTERN.toRegex()) ?: false

    fun isSmsForMonetaryDebit(body: String): Boolean =
        body.contains(DEBIT_PATTERN.toRegex())

    fun extractAmount(body: String): String? =
        AMOUNT_PATTERN.toRegex().find(body)?.groups?.get(1)?.value

    fun extractMerchantName(content: String): String =
        MERCHANT_PATTERN.toRegex().find(content)?.value.orEmpty()
            .ifEmpty { "at Merchant" }.trim()
}

private const val MERCHANT_SENDER_PATTERN = "[a-zA-Z0-9]{2}-[a-zA-Z0-9]{6}"
private const val DEBIT_PATTERN = "(?i)spent|debited"
private const val AMOUNT_PATTERN =
    "(?i)(?:RS|INR|MRP)\\.?\\s?(\\d+(:?,\\d+)?(,\\d+)?(\\.\\d{1,2})?)"
private const val MERCHANT_PATTERN =
    "(?i)(?:\\sat\\s|in\\*)([A-Za-z0-9]*\\s?-?\\s?[A-Za-z0-9]*\\s?-?\\.?)"