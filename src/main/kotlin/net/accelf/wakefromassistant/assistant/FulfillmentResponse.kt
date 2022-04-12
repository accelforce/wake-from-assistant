package net.accelf.wakefromassistant.assistant

import com.fasterxml.jackson.annotation.JsonProperty

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
            enum class Type {
                @JsonProperty("action.devices.types.SWITCH") SWITCH,
                ;
            }

            enum class Trait {
                @JsonProperty("action.devices.traits.OnOff") ON_OFF,
                ;
            }

            data class Name(
                val name: String,
            )
        }
    }
}
