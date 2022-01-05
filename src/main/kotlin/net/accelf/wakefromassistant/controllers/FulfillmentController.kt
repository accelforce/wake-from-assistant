package net.accelf.wakefromassistant.controllers

import net.accelf.wakefromassistant.assistant.FulfillmentRequest
import net.accelf.wakefromassistant.assistant.FulfillmentResponse
import net.accelf.wakefromassistant.assistant.Intent
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class FulfillmentController {

    @RequestMapping("/fulfillment", method = [RequestMethod.POST])
    @Secured
    fun fulfillment(@AuthenticationPrincipal userName: String, @RequestBody request: FulfillmentRequest): FulfillmentResponse {
        return when (request.inputs.first().intent) {
            Intent.Type.SYNC -> FulfillmentResponse(
                requestId = request.requestId,
                payload = FulfillmentResponse.Payload(
                    agentUserId = userName,
                    devices = listOf(),
                ),
            )
        }
    }
}
