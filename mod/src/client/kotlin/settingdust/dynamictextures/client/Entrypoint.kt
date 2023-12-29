package settingdust.dynamictextures.client

import com.terraformersmc.modmenu.api.ModMenuApi
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.registry.RegistryKey
import net.minecraft.util.Identifier
import org.quiltmc.qkl.library.serialization.CodecFactory
import settingdust.dynamictextures.BetterUITextures

fun init() {
    PredefinedTextureTypes
    TextureModifierTypes
    GenericAssetsGenerator.register()
}

object ModMenuEntrypoint : ModMenuApi

internal val codecFactory = CodecFactory {
    ignoreUnknownKeys = true
    printErrorStackTraces = true
    polymorphism { flatten = true }
    codecs {
        unnamed(Identifier.CODEC)
        registry(Registries.TEXTURE_MODIFIERS)
        registry(Registries.PREDEFINED_TEXTURE)
    }
}

object RegistryKeys {
    @JvmStatic
    val PREDEFINED_TEXTURE =
        RegistryKey.ofRegistry<PredefinedTextureType<out PredefinedTexture>>(
            BetterUITextures.identifier("predefined_texture")
        )!!

    @JvmStatic
    val TEXTURE_MODIFIERS =
        RegistryKey.ofRegistry<TextureModifierType<out TextureModifier>>(
            BetterUITextures.identifier("texture_modifiers")
        )!!
}

object Registries {
    @JvmStatic
    val PREDEFINED_TEXTURE =
        FabricRegistryBuilder.createSimple(RegistryKeys.PREDEFINED_TEXTURE).buildAndRegister()!!

    @JvmStatic
    val TEXTURE_MODIFIERS =
        FabricRegistryBuilder.createSimple(RegistryKeys.TEXTURE_MODIFIERS).buildAndRegister()!!
}
