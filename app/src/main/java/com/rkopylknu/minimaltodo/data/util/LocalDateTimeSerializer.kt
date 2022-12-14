package com.rkopylknu.minimaltodo.data.util

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class LocalDateTimeSerializer : KSerializer<LocalDateTime?> {

    override val descriptor =
        PrimitiveSerialDescriptor("LocalDateTime?", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime?) {
        encoder.encodeString(value?.toString() ?: "")
    }

    override fun deserialize(decoder: Decoder): LocalDateTime? {
        val raw = decoder.decodeString()
        return if (raw.isNotEmpty()) {
            LocalDateTime.parse(raw)
        } else null
    }
}