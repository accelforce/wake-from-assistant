package net.accelf.wakefromassistant.assistant

data class FulfillmentResponse(
    val requestId: String,
    val payload: Payload,
) {
    data class Payload(
        val agentUserId: String,
        val devices: List<Device>,
    ) {
        data class Device(
            val id: String,
            val type: Type,
            val traits: List<Trait>,
            val name: Name,
            val willReportState: Boolean,
        ) {
            enum class Type

            enum class Trait

            data class Name(
                val name: String,
            )
        }
    }
}
