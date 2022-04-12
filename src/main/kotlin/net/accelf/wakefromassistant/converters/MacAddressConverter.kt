package net.accelf.wakefromassistant.converters

import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class MacAddressConverter : AttributeConverter<String, Long> {
    override fun convertToDatabaseColumn(attribute: String?): Long? =
        attribute?.filterNot { it in "-:" }
            ?.takeIf { it.length == 12 }
            ?.toLong(16)

    override fun convertToEntityAttribute(dbData: Long?): String? =
        dbData?.toString(16)?.padStart(12, '0')
            ?.chunked(2)?.joinToString(":")
}
