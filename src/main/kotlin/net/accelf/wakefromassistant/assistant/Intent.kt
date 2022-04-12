package net.accelf.wakefromassistant.assistant

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DatabindContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase

@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "intent")
@JsonTypeIdResolver(Intent.IdResolver::class)
sealed class Intent {
    class IdResolver : TypeIdResolverBase() {

        private lateinit var superType: JavaType

        override fun init(bt: JavaType) {
            superType = bt
        }

        // Never called
        override fun idFromValue(value: Any?): String? = null
        override fun idFromValueAndType(value: Any?, suggestedType: Class<*>?): String? = null

        override fun typeFromId(context: DatabindContext, id: String): JavaType =
            when (id) {
                "action.devices.SYNC" -> SyncIntent::class.java
                "action.devices.QUERY" -> QueryIntent::class.java
                else -> null
            }
                .let { context.constructSpecializedType(superType, it) }

        override fun getMechanism(): JsonTypeInfo.Id = JsonTypeInfo.Id.CUSTOM
    }
}

object SyncIntent : Intent()

data class QueryIntent(
    val payload: Payload,
) : Intent() {
    data class Payload(
        val devices: List<Device>,
    ) {
        data class Device(
            val id: String,
        )
    }
}
