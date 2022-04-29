package net.accelf.wakefromassistant.controllers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import net.accelf.wakefromassistant.assistant.FulfillmentRequest
import net.accelf.wakefromassistant.assistant.FulfillmentResponse
import net.accelf.wakefromassistant.assistant.FulfillmentResponse.QueryPayload.Device.Status.ERROR
import net.accelf.wakefromassistant.assistant.FulfillmentResponse.QueryPayload.Device.Status.SUCCESS
import net.accelf.wakefromassistant.assistant.QueryIntent
import net.accelf.wakefromassistant.assistant.SyncIntent
import net.accelf.wakefromassistant.helpers.isReachable
import net.accelf.wakefromassistant.models.DeviceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import net.accelf.wakefromassistant.assistant.FulfillmentResponse.QueryPayload.Device as QueryDevice
import net.accelf.wakefromassistant.assistant.FulfillmentResponse.SyncPayload.Device as SyncDevice

@RestController
class FulfillmentController {

    @Autowired
    lateinit var deviceRepository: DeviceRepository

    @RequestMapping("/fulfillment", method = [RequestMethod.POST])
    @Secured
    fun fulfillment(
        @AuthenticationPrincipal userName: String,
        @RequestBody request: FulfillmentRequest,
    ): FulfillmentResponse {
        val intent = request.inputs.first()
        return FulfillmentResponse(
            requestId = request.requestId,
            payload = when (intent) {
                is SyncIntent -> FulfillmentResponse.SyncPayload(
                    agentUserId = userName,
                    devices = deviceRepository.findAll().map { model ->
                        SyncDevice(
                            id = model.id.toString(),
                            type = SyncDevice.Type.SWITCH,
                            traits = listOf(SyncDevice.Trait.ON_OFF),
                            name = SyncDevice.Name(
                                name = model.deviceName,
                            ),
                            willReportState = false,
                        )
                    },
                )
                is QueryIntent -> FulfillmentResponse.QueryPayload(
                    devices = intent.payload.devices.map { it.id.toLong() }
                        .let { deviceRepository.findAllById(it) }
                        .map { it to CoroutineScope(Dispatchers.IO).async { it.ipAddress.isReachable() } }
                        .associate { (model, deferred) ->
                            runBlocking {
                                model.id.toString() to deferred.await().fold(
                                    { on ->
                                        QueryDevice(
                                            online = true,
                                            status = SUCCESS,
                                            on = on,
                                        )
                                    },
                                    {
                                        QueryDevice(
                                            online = false,
                                            status = ERROR,
                                            on = false,
                                        )
                                    },
                                )
                            }
                        }
                )
            }
        )
    }
}
