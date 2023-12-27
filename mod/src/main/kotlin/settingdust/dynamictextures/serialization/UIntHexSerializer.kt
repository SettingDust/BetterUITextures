package settingdust.dynamictextures.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalStdlibApi::class)
object UIntHexSerializer : KSerializer<UInt> {
    override val descriptor = PrimitiveSerialDescriptor("UIntHex", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder) = decoder.decodeString().hexToUInt()

    override fun serialize(encoder: Encoder, value: UInt) {
        encoder.encodeString(value.toHexString())
    }
}
