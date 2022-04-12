package net.accelf.wakefromassistant.converters

import java.net.Inet4Address
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Suppress("unused")
@Converter(autoApply = true)
class Inet4AddressConverter : AttributeConverter<Inet4Address, Int> {
    override fun convertToDatabaseColumn(attribute: Inet4Address?): Int? =
        attribute?.address?.mapIndexed { n, byte -> byte.toUInt() and 255u shl ((3 - n) * 8) }?.sum()?.toInt()

    override fun convertToEntityAttribute(dbData: Int?): Inet4Address? =
        dbData?.toUInt()?.toByteArray()?.let { Inet4Address.getByAddress(it) as Inet4Address }

    private fun UInt.toByteArray() =
        (0..3).map { n -> (this shr ((3 - n) * 8) and 255u).toByte() }.toByteArray()
}
