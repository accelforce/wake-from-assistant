package net.accelf.wakefromassistant.assistant

import com.fasterxml.jackson.annotation.JsonProperty

data class FulfillmentResponse(
    val requestId: String,
    val payload: Payload,
) {
    sealed class Payload

    data class SyncPayload(
        val agentUserId: String,
        val devices: List<Device>,
    ): Payload() {
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

    data class QueryPayload(
        val devices: Map<String, Device>,
    ): Payload() {
        data class Device(
            val online: Boolean,
            val status: Status,
            val on: Boolean,
        ) {
            enum class Status {
                SUCCESS,
                ERROR,
                ;
            }
        }
    }

    data class ExecutePayload(
        val commands: List<Command>,
    ): Payload() {
        data class Command(
            val ids: List<String>,
            val status: Status,
            val errorCode: ErrorCode? = null,
        ) {
            enum class Status {
                SUCCESS,
                ERROR,
                ;
            }

            enum class ErrorCode {
                @JsonProperty("deviceNotFound") NOT_FOUND,
                @JsonProperty("functionNotSupported") NOT_SUPPORTED,
                @JsonProperty("hardError") HARD_ERROR,
                ;
            }
        }
    }
}
