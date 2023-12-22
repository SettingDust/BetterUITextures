package settingdust.betteruitextures.client

import net.mehvahdjukaar.moonlight.api.resources.pack.DynClientResourcesGenerator
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack
import net.mehvahdjukaar.moonlight.api.resources.textures.ImageTransformer
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage
import net.minecraft.resource.ResourceManager
import net.minecraft.resource.ResourcePackProfile
import net.minecraft.util.Identifier
import settingdust.betteruitextures.BetterUITextures
import wraith.fwaystones.FabricWaystones

object GenericAssetsGenerator :
    DynClientResourcesGenerator(
        DynamicTexturePack(
            BetterUITextures.identifier("generic"),
            ResourcePackProfile.InsertionPosition.BOTTOM,
            false,
            false,
        ),
    ) {

    init {}

    override fun getLogger() = BetterUITextures.logger

    override fun dependsOnLoadedPacks() = true

    override fun regenerateDynamicAssets(manager: ResourceManager) {
        GenericTextures.guiBackground = null
        dynamicPack.addAndCloseTexture(
            Identifier(FabricWaystones.MOD_ID, "gui/backround"),
            GenericTextures.getGuiBackground(manager).makeCopy()
        )
    }
}

object GenericTextures {

    var guiBackground: TextureImage? = null

    val UNIT = 5
    val SIZE = UNIT * 3

    fun getGuiBackground(manager: ResourceManager): TextureImage {
        if (guiBackground != null) return guiBackground!!
        val inventoryTexture =
            TextureImage.open(
                manager,
                Identifier("gui/container/inventory") // HandledScreen.BACKGROUND_TEXTURE
            )
        val result = TextureImage.createNew(SIZE, SIZE, null)
        val backgroundColor = inventoryTexture.getFramePixel(0, 5, 5)

        for (x in 0 until UNIT) {
            for (y in 0 until UNIT) {
                result.setFramePixel(0, UNIT + x, UNIT + y, backgroundColor)
            }
        }

        ImageTransformer.builder(256, 256, SIZE, SIZE)
            .apply {
                // Corners
                copyRect(0, 0, UNIT, UNIT, 0, 0)
                copyRect(176 - UNIT, 0, UNIT, UNIT, SIZE - UNIT, 0)
                copyRect(0, 166 - UNIT, UNIT, UNIT, 0, SIZE - UNIT)
                copyRect(176 - UNIT, 166 - UNIT, UNIT, UNIT, SIZE - UNIT, SIZE - UNIT)

                // Edges
                copyRect(UNIT, 0, UNIT, UNIT, UNIT, 0)
                copyRect(0, UNIT, UNIT, UNIT, 0, UNIT)
                copyRect(176 - UNIT, 166 - UNIT * 2, UNIT, UNIT, SIZE - UNIT, SIZE - UNIT * 2)
                copyRect(176 - UNIT * 2, 166 - UNIT, UNIT, UNIT, SIZE - UNIT * 2, SIZE - UNIT)
            }
            .build()
            .apply(inventoryTexture, result)
        guiBackground = result
        return result
    }

    fun getGuiBackgroundColor(manager: ResourceManager) =
        getGuiBackground(manager).getFramePixel(0, UNIT, UNIT)
}
