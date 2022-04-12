package net.accelf.wakefromassistant.controllers

import net.accelf.wakefromassistant.assistant.FulfillmentRequest
import net.accelf.wakefromassistant.assistant.FulfillmentResponse
import net.accelf.wakefromassistant.assistant.FulfillmentResponse.Payload.Device
import net.accelf.wakefromassistant.assistant.Intent
import net.accelf.wakefromassistant.models.DeviceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class FulfillmentController {

    @Autowired
    lateinit var deviceRepository: DeviceRepository

    @RequestMapping("/fulfillment", method = [RequestMethod.POST])
    @Secured
    fun fulfillment(@AuthenticationPrincipal userName: String, @RequestBody request: FulfillmentRequest): FulfillmentResponse {
        val devices = deviceRepository.findAll()
            .map { model ->
                Device(
                    id = model.id.toString(),
                    type = Device.Type.SWITCH,
                    traits = listOf(Device.Trait.ON_OFF),
                    name = Device.Name(
                        name = model.deviceName,
                    ),
                    willReportState = false,
                )
            }

        return when (request.inputs.first().intent) {
            Intent.Type.SYNC -> FulfillmentResponse(
                requestId = request.requestId,
                payload = FulfillmentResponse.Payload(
                    agentUserId = userName,
                    devices = devices,
                ),
            )
        }
    }
}
