package settingdust.betteruitextures.client

import net.fabricmc.loader.api.FabricLoader
import net.mehvahdjukaar.moonlight.api.resources.pack.DynClientResourcesGenerator
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack
import net.mehvahdjukaar.moonlight.api.resources.textures.ImageTransformer
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourcePackProfile
import net.minecraft.util.Identifier
import settingdust.betteruitextures.BetterUITextures

data class Point(val x: Int, val y: Int)

data class Size(val width: Int, val height: Int)

data class NinePatch(val first: Point, val second: Point)

object GenericAssetsGenerator :
    DynClientResourcesGenerator(
        DynamicTexturePack(
            BetterUITextures.identifier("generic"),
            ResourcePackProfile.InsertionPosition.BOTTOM,
            false,
            false,
        ),
    ) {

    override fun getLogger() = BetterUITextures.logger

    override fun dependsOnLoadedPacks() = true

    override fun regenerateDynamicAssets(manager: ResourceManager) {
        StandaloneWindow.regenerateDynamicAssets(manager, dynamicPack)
        InventoryWindow.regenerateDynamicAssets(manager, dynamicPack)

        for (generator in
            DynamicAssetsGenerator::class
                .sealedSubclasses
                .map {
                    it.objectInstance ?: throw IllegalStateException("Generators have to be object")
                }
                .sortedBy { it.modId }
                .filter { it.modId != null && FabricLoader.getInstance().isModLoaded(it.modId) }) {
            generator.regenerateDynamicAssets(manager, dynamicPack)
        }
    }

    fun TextureImage.removeElementBackground(
        width: Int,
        height: Int,
        ninePatch: NinePatch,
        offsetX: Int = 0,
        offsetY: Int = 0,
        color: Int = -0x39393A,
    ) {
        val rightX = width - ninePatch.second.x
        val bottomY = height - ninePatch.second.y
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x < ninePatch.first.x || x > rightX) {
                    setFramePixel(0, x + offsetX, y + offsetY, 0)
                } else {
                    if (y < ninePatch.first.y || y > bottomY) {
                        setFramePixel(0, x + offsetX, y + offsetY, 0)
                    } else {
                        val originalColor = getFramePixel(0, x + offsetX, y + offsetY)
                        if (originalColor == color) setFramePixel(0, x + offsetX, y + offsetY, 0)
                    }
                }
            }
        }
    }

    data object StandaloneWindow : DynamicAssetsGenerator() {
        private val SIZE = Size(176, 166)
        private val BACKGROUND = Identifier(BetterUITextures.NAMESPACE, "gui/standalone_background")
        val NINE_PATCH = NinePatch(Point(8, 7), Point(6, 7))

        private fun generateBackground(manager: ResourceManager) =
            TextureImage.open(
                    manager,
                    Identifier("gui/container/inventory") // HandledScreen.BACKGROUND_TEXTURE
                )
                .generateBackgroundNinePatch(NINE_PATCH, SIZE, Size(48, 48))

        fun applyBackground(
            manager: ResourceManager,
            windowSize: Size,
            textureSize: Int
        ): TextureImage {
            val background = TextureImage.open(manager, BACKGROUND)
            if (windowSize == SIZE) return background

            val scaledBackground = background.resizeNinePatch(NINE_PATCH, windowSize)

            val alignedBackground =
                TextureImage.createNew(textureSize, textureSize, scaledBackground.metadata).also {
                    ImageTransformer.builder(
                            windowSize.width,
                            windowSize.height,
                            textureSize,
                            textureSize
                        )
                        .apply { copyRect(0, 0, windowSize.width, windowSize.height, 0, 0) }
                        .build()
                        .apply(scaledBackground, it)
                }

            return alignedBackground
        }

        override fun regenerateDynamicAssets(
            manager: ResourceManager,
            dynamicPack: DynamicTexturePack
        ) {
            dynamicPack.addAndCloseTexture(BACKGROUND, generateBackground(manager), false)
        }
    }

    data object InventoryWindow : DynamicAssetsGenerator() {
        val TOP = Identifier(BetterUITextures.NAMESPACE, "gui/inventory_top")
        val BOTTOM = Identifier(BetterUITextures.NAMESPACE, "gui/inventory_bottom")

        private const val BOTTOM_WIDTH = 176
        private const val BOTTOM_HEIGHT = 83
        private const val TOP_WIDTH = BOTTOM_WIDTH
        private const val TOP_HEIGHT = 139

        private fun generateBottom(manager: ResourceManager): TextureImage {
            val inventoryTexture =
                TextureImage.open(manager, Identifier("gui/container/generic_54"))
            val result = TextureImage.createNew(BOTTOM_WIDTH, BOTTOM_HEIGHT, null)

            inventoryTexture.image.copyRect(
                result.image,
                0,
                TOP_HEIGHT,
                0,
                0,
                BOTTOM_WIDTH,
                BOTTOM_HEIGHT,
                false,
                false
            )

            //            ImageTransformer.builder(256, 256, BOTTOM_WIDTH, BOTTOM_HEIGHT)
            //                .apply { copyRect(0, TOP_HEIGHT, BOTTOM_WIDTH, BOTTOM_HEIGHT, 0, 0) }
            //                .build()
            //                .apply(inventoryTexture, result)

            return result
        }

        private object Top {
            private val NINE_PATCH = NinePatch(Point(8, 17), Point(7, 14))
            private val SIZE = Size(176, 139)

            fun generate(manager: ResourceManager) =
                TextureImage.open(manager, Identifier("gui/container/generic_54"))
                    .generateBackgroundNinePatch(NINE_PATCH, SIZE)
        }

        override fun regenerateDynamicAssets(
            manager: ResourceManager,
            dynamicPack: DynamicTexturePack
        ) {
            dynamicPack.addAndCloseTexture(BOTTOM, generateBottom(manager), false)
            dynamicPack.addAndCloseTexture(TOP, Top.generate(manager), false)
        }
    }
}
