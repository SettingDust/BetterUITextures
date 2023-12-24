package settingdust.betteruitextures.client

import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack
import net.mehvahdjukaar.moonlight.api.resources.textures.ImageTransformer
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier

data object FabricWaystonesAssetsGenerator : DynamicAssetsGenerator() {

    override val modId = "fwaystones"

    override fun regenerateDynamicAssets(
        manager: ResourceManager,
        dynamicPack: DynamicTexturePack
    ) {
        dynamicPack.addAndCloseTexture(
            Identifier(modId, "gui/waystone"),
            Waystone.generate(manager)
        )
        dynamicPack.addAndCloseTexture(
            Identifier(modId, "gui/waystone_config"),
            Config.generate(manager)
        )
    }
}

private object Waystone {
    private const val WINDOW_WIDTH = 177
    private const val WINDOW_HEIGHT = 176
    private const val TEXTURE_SIZE = 256

    fun generateWindowBackground(manager: ResourceManager): TextureImage {
        val background = GenericTextures.getGuiBackground(manager)

        val scaledBackground =
            background.resizeNinePatch(
                GenericTextures.UNIT to GenericTextures.UNIT,
                GenericTextures.UNIT to GenericTextures.UNIT,
                WINDOW_WIDTH to WINDOW_HEIGHT
            )

        val alignedBackground =
            TextureImage.createNew(TEXTURE_SIZE, TEXTURE_SIZE, scaledBackground.metadata).also {
                ImageTransformer.builder(WINDOW_WIDTH, WINDOW_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE)
                    .apply { copyRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, 0, 0) }
                    .build()
                    .apply(scaledBackground, it)
            }

        return alignedBackground
    }

    fun generate(manager: ResourceManager): TextureImage {
        val original =
            TextureImage.open(
                manager,
                Identifier(FabricWaystonesAssetsGenerator.modId, "gui/waystone")
            )
        val image = original.makeCopy()

        // Background
        for (x in 0 until WINDOW_WIDTH) {
            for (y in 0 until WINDOW_HEIGHT) {
                if (x < GenericTextures.UNIT || x > WINDOW_WIDTH - GenericTextures.UNIT) {
                    image.setFramePixel(0, x, y, 0)
                } else if (y < GenericTextures.UNIT || y > WINDOW_HEIGHT - GenericTextures.UNIT) {
                    image.setFramePixel(0, x, y, 0)
                } else {

                    val originalColor = image.getFramePixel(0, x, y)
                    if (originalColor == -0x39393A) image.setFramePixel(0, x, y, 0)
                }
            }
        }

        val transformed = manager.let { generateWindowBackground(it) }

        transformed.applyOverlay(image)

        return transformed
    }
}

private object Config {
    private const val WINDOW_WIDTH = 177
    private const val WINDOW_HEIGHT = 176
    private const val TEXTURE_SIZE = 256

    fun generateWindowBackground(manager: ResourceManager): TextureImage {
        val background = GenericTextures.getGuiBackground(manager)

        val scaledBackground =
            background.resizeNinePatch(
                GenericTextures.UNIT to GenericTextures.UNIT,
                GenericTextures.UNIT to GenericTextures.UNIT,
                WINDOW_WIDTH to WINDOW_HEIGHT
            )

        val alignedBackground =
            TextureImage.createNew(TEXTURE_SIZE, TEXTURE_SIZE, scaledBackground.metadata).also {
                ImageTransformer.builder(WINDOW_WIDTH, WINDOW_HEIGHT, TEXTURE_SIZE, TEXTURE_SIZE)
                    .apply { copyRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, 0, 0) }
                    .build()
                    .apply(scaledBackground, it)
            }

        return alignedBackground
    }

    fun generate(manager: ResourceManager): TextureImage {
        val original =
            TextureImage.open(
                manager,
                Identifier(FabricWaystonesAssetsGenerator.modId, "gui/waystone_config")
            )
        val image = original.makeCopy()

        // Background
        for (x in 0 until WINDOW_WIDTH) {
            for (y in 0 until WINDOW_HEIGHT) {
                if (x < GenericTextures.UNIT || x > WINDOW_WIDTH - GenericTextures.UNIT) {
                    image.setFramePixel(0, x, y, 0)
                } else if (y < GenericTextures.UNIT || y > WINDOW_HEIGHT - GenericTextures.UNIT) {
                    image.setFramePixel(0, x, y, 0)
                } else {

                    val originalColor = image.getFramePixel(0, x, y)
                    if (originalColor == -0x39393A) image.setFramePixel(0, x, y, 0)
                }
            }
        }

        val transformed = manager.let { generateWindowBackground(it) }

        transformed.applyOverlay(image)

        return transformed
    }
}
