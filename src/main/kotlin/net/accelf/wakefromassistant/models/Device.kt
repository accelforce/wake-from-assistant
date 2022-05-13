package net.accelf.wakefromassistant.models

import net.accelf.wakefromassistant.helpers.toStringMacAddress
import org.hibernate.Hibernate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.InetSocketAddress
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Device(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    val deviceName: String,
    val macAddress: Long,
    val ipAddress: Inet4Address,
    val broadcastAddress: Inet4Address? = null,
) {
    val macAddressString: String
        get() = macAddress.toStringMacAddress()

    constructor() : this(null, "", 0x000000000000, Inet4Address.getLocalHost() as Inet4Address, null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Device

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , deviceName = $deviceName , macAddress = $macAddressString , ipAddress = $ipAddress , broadcastAddress = $broadcastAddress )"
    }

    fun wake(): Result<Unit> = runCatching {
        val packet = buildList {
            repeat(6) { add(0xff.toByte()) }
            repeat(16) { addAll(macAddress.toULong().toByteList()) }
        }.toByteArray()
        val address = InetSocketAddress(ipAddress, 9)

        DatagramPacket(packet, packet.size, address)
            .let { DatagramSocket().send(it) }
    }

    private fun ULong.toByteList() =
        (0..5).map { n -> (this shr ((5 - n) * 8) and 255u).toByte() }
}

interface DeviceRepository : CrudRepository<Device, Long> {
    fun findAll(pageable: Pageable): Page<Device>
}
