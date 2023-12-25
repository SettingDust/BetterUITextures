package settingdust.betteruitextures.client

import net.mehvahdjukaar.moonlight.api.resources.ResType
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import settingdust.betteruitextures.client.GenericAssetsGenerator.removeElementBackground

data object SpellEngineAssetsGenerator : DynamicAssetsGenerator() {

    override val modId = "spell_engine"
    private val SPELL_BINDING = Identifier(modId, "gui/spell_binding")

    override fun regenerateDynamicAssets(
        manager: ResourceManager,
        dynamicPack: DynamicTexturePack
    ) {
        dynamicPack.removeResource(ResType.TEXTURES.getPath(SPELL_BINDING))
        dynamicPack.addAndCloseTexture(SPELL_BINDING, SpellBinding.generate(manager), false)
    }

    private object SpellBinding {
        private const val WINDOW_WIDTH = 176
        private const val WINDOW_HEIGHT = 166
        private const val TEXTURE_SIZE = 256

        fun generate(manager: ResourceManager): TextureImage {
            val original = TextureImage.open(manager, SPELL_BINDING)
            val image = original.makeCopy()

            // Background
            image.removeElementBackground(
                WINDOW_WIDTH,
                WINDOW_HEIGHT,
                GenericAssetsGenerator.StandaloneWindow.NINE_PATCH
            )
            GenericAssetsGenerator.InventoryWindow.removeBottom(
                image,
                Size(WINDOW_WIDTH, WINDOW_HEIGHT)
            )

            val transformed =
                GenericAssetsGenerator.InventoryWindow.generateBackground(
                        manager,
                        Size(
                            GenericAssetsGenerator.InventoryWindow.WIDTH,
                            WINDOW_HEIGHT - GenericAssetsGenerator.InventoryWindow.BOTTOM_HEIGHT
                        )
                    )
                    .expandCanvas(Size(TEXTURE_SIZE, TEXTURE_SIZE))

            transformed.applyOverlay(image)

            return transformed
        }
    }
}
