package net.accelf.wakefromassistant.assistant

data class FulfillmentRequest(
    val requestId: String,
    val inputs: List<Intent>,
)
