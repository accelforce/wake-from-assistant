package net.accelf.wakefromassistant.controllers

import net.accelf.wakefromassistant.assistant.FulfillmentResponse
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class FulfillmentController {

    @RequestMapping("/fulfillment", method = [RequestMethod.POST])
    @Secured
    fun fulfillment(): FulfillmentResponse {
        return FulfillmentResponse("test")
    }
}
