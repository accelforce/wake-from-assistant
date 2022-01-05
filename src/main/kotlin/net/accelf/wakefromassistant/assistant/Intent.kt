package net.accelf.wakefromassistant.assistant

import com.fasterxml.jackson.annotation.JsonProperty

data class Intent(
    val intent: Type,
) {
    enum class Type {
        @JsonProperty("action.devices.SYNC") SYNC,
        ;
    }
}
