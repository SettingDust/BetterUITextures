package settingdust.betteruitextures.client

import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import settingdust.betteruitextures.client.GenericAssetsGenerator.removeElementBackground
import settingdust.betteruitextures.mixin.DynamicResourcePackAccessor

data object SpellEngineAssetsGenerator : DynamicAssetsGenerator() {

    override val modId = "spell_engine"
    private val SPELL_BINDING = Identifier(modId, "gui/spell_binding")

    override fun regenerateDynamicAssets(
        manager: ResourceManager,
        dynamicPack: DynamicTexturePack
    ) {
        (dynamicPack as DynamicResourcePackAccessor).resources.remove(SPELL_BINDING)
        dynamicPack.addAndCloseTexture(SPELL_BINDING, SpellBinding.generate(manager), false)
    }

    private object SpellBinding {
        private const val WINDOW_WIDTH = 176
        private const val WINDOW_HEIGHT = 166
        private const val TEXTURE_SIZE = 256

        fun generate(manager: ResourceManager): TextureImage {
            val original =
                TextureImage.open(
                    manager,
                    Identifier(FabricWaystonesAssetsGenerator.modId, "gui/waystone_config")
                )
            val image = original.makeCopy()

            // Background
            image.removeElementBackground(
                WINDOW_WIDTH,
                WINDOW_HEIGHT,
                GenericAssetsGenerator.StandaloneWindow.NINE_PATCH
            )

            val transformed =
                manager.let {
                    GenericAssetsGenerator.StandaloneWindow.applyBackground(
                        it,
                        Size(WINDOW_WIDTH, WINDOW_HEIGHT),
                        TEXTURE_SIZE
                    )
                }

            transformed.applyOverlay(image)

            return transformed
        }
    }
}
