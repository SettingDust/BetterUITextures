package settingdust.betteruitextures.client

import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import settingdust.betteruitextures.client.GenericAssetsGenerator.removeElementBackground

data object FabricWaystonesAssetsGenerator : DynamicAssetsGenerator() {

    override val modId = "fwaystones"

    override fun regenerateDynamicAssets(
        manager: ResourceManager,
        dynamicPack: DynamicTexturePack
    ) {
        dynamicPack.addAndCloseTexture(
            Identifier(modId, "gui/waystone"),
            Waystone.generate(manager),
            false
        )
        dynamicPack.addAndCloseTexture(
            Identifier(modId, "gui/waystone_config"),
            Config.generate(manager),
            false
        )
    }

    private object Waystone {
        private const val WINDOW_WIDTH = 177
        private const val WINDOW_HEIGHT = 176
        private const val TEXTURE_SIZE = 256

        fun generate(manager: ResourceManager): TextureImage {
            val original = TextureImage.open(manager, Identifier(modId, "gui/waystone"))
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

    private object Config {
        private const val WINDOW_WIDTH = 177
        private const val WINDOW_HEIGHT = 176
        private const val TEXTURE_SIZE = 256

        fun generate(manager: ResourceManager): TextureImage {
            val original = TextureImage.open(manager, Identifier(modId, "gui/waystone_config"))
            val image = original.makeCopy()

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
