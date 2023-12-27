package settingdust.betteruitextures.client

import com.mojang.serialization.Codec
import java.util.function.Function
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.quiltmc.qkl.library.serialization.internal.CodecSerializerAdapter
import org.quiltmc.qkl.library.serialization.internal.decoder.DynamicDecoder
import org.quiltmc.qkl.library.serialization.internal.encoder.DynamicEncoder

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
open class DispatchedCodecSerializer<Out : Any, Type>(
    val targetCodec: Codec<Type>,
    val type: Function<Out, Type>,
    val codec: Function<Type, Codec<Out>>,
    private val serialName: String
) : KSerializer<Out> {
    override val descriptor =
        buildSerialDescriptor(serialName, PolymorphicKind.OPEN) {
            element("type", String.serializer().descriptor)
            element(
                "value",
                buildSerialDescriptor(
                    "DispatchedCodecSerializer<$serialName>",
                    SerialKind.CONTEXTUAL
                )
            )
        }

    override fun deserialize(decoder: Decoder): Out {
        if (decoder !is DynamicDecoder<*>) {
            throw UnsupportedOperationException(
                "Codec serializers can only be used in Dynamic serialization"
            )
        }

        val compositeDecoder = decoder.beginStructure(descriptor)

        var currentType: Type
        var elementCodec: Codec<Out>? = null
        var out: Out? = null
        while (true) {
            when (val index = compositeDecoder.decodeElementIndex(descriptor)) {
                0 -> {
                    currentType =
                        compositeDecoder.decodeSerializableElement(
                            descriptor,
                            0,
                            CodecSerializerAdapter(targetCodec, serialName)
                        )
                    elementCodec = codec.apply(currentType)
                }
                1 ->
                    out =
                        compositeDecoder.decodeSerializableElement(
                            descriptor,
                            1,
                            CodecSerializerAdapter(elementCodec!!, serialName)
                        )
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }

        decoder.endStructure(descriptor)
        return out!!
    }

    override fun serialize(encoder: Encoder, value: Out) {
        if (encoder !is DynamicEncoder<*>) {
            throw UnsupportedOperationException(
                "Codec serializers can only be used in Dynamic serialization"
            )
        }

        val compositeEncoder = encoder.beginStructure(descriptor)

        val currentType = type.apply(value)
        compositeEncoder.encodeSerializableElement(
            descriptor,
            0,
            CodecSerializerAdapter(targetCodec, serialName),
            currentType
        )
        val elementCodec = codec.apply(currentType)
        compositeEncoder.encodeSerializableElement(
            descriptor,
            1,
            CodecSerializerAdapter(elementCodec, serialName),
            value
        )

        encoder.endStructure(descriptor)
    }
}
