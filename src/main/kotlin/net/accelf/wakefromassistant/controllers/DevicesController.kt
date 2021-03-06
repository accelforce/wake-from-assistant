package net.accelf.wakefromassistant.controllers

import net.accelf.wakefromassistant.controllers.DevicesController.Device.Companion.toDevice
import net.accelf.wakefromassistant.helpers.toLongMacAddress
import net.accelf.wakefromassistant.models.DeviceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.net.Inet4Address
import net.accelf.wakefromassistant.models.Device as DeviceModel

@Controller
class DevicesController {

    @Autowired
    lateinit var deviceRepository: DeviceRepository

    data class Device(
        val id: Long? = null,
        val deviceName: String = "",
        val macAddress: String = "",
        val ipAddress: String = "",
    ) {
        fun toModel() = DeviceModel(
            id = id,
            deviceName = deviceName,
            macAddress = macAddress.toLongMacAddress()!!,
            ipAddress = Inet4Address.getByName(ipAddress) as Inet4Address,
        )

        companion object {
            fun DeviceModel.toDevice() = Device(
                id = id,
                deviceName = deviceName,
                macAddress = macAddressString,
                ipAddress = ipAddress.hostAddress,
            )
        }
    }

    @GetMapping("/devices")
    @Secured
    fun index(model: Model, pageable: Pageable): String {
        val devices = deviceRepository.findAll(pageable)
        model.addAttribute("devices", devices.map { it.toDevice() })
        return "devices/index"
    }

    @GetMapping("/devices/new")
    @Secured
    fun new(model: Model): String {
        model.addAttribute("device", Device())
        return "devices/new"
    }

    @PostMapping("/devices/new")
    @Secured
    fun create(model: Model, @ModelAttribute device: Device): String {
        val deviceModel = deviceRepository.save(device.toModel())
        return "redirect:/devices/${deviceModel.id}"
    }

    @GetMapping("/devices/{id}")
    @Secured
    fun show(model: Model, @PathVariable id: Long): String {
        val device = deviceRepository.findByIdOrNull(id) ?: error("not found")
        model.addAttribute("device", device.toDevice())
        return "devices/show"
    }

    @PostMapping("/devices/{id}")
    @Secured
    fun save(model: Model, @PathVariable id: Long, @ModelAttribute device: Device): String {
        val deviceModel = deviceRepository.save(device.copy(id = id).toModel())
        return "redirect:/devices/${deviceModel.id}"
    }
}
