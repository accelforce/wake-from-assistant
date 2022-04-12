package net.accelf.wakefromassistant.models

import net.accelf.wakefromassistant.converters.MacAddressConverter
import org.hibernate.Hibernate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import java.net.Inet4Address
import javax.persistence.*

@Entity
data class Device(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    val deviceName: String,
    @Convert(converter = MacAddressConverter::class) val macAddress: String,
    val ipAddress: Inet4Address,
    val broadcastAddress: Inet4Address? = null,
) {
    constructor() : this(null, "", "00:00:00:00:00:00", Inet4Address.getLocalHost() as Inet4Address, null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Device

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , deviceName = $deviceName , macAddress = $macAddress , ipAddress = $ipAddress , broadcastAddress = $broadcastAddress )"
    }
}

interface DeviceRepository: CrudRepository<Device, Long> {
    fun findAll(pageable: Pageable): Page<Device>
}
