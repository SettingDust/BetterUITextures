package settingdust.betteruitextures.client

import net.mehvahdjukaar.moonlight.api.resources.ResType
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack
import net.mehvahdjukaar.moonlight.api.resources.textures.ImageTransformer
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

            transformed.applyOverlay(image)

            // Spell entries
            ImageTransformer.builder(
                    GenericAssetsGenerator.EnchantingElements.EntriesBackground.WIDTH,
                    GenericAssetsGenerator.EnchantingElements.EntriesBackground.HEIGHT,
                    transformed.imageWidth(),
                    transformed.imageHeight()
                )
                .apply {
                    copyRect(
                        0,
                        0,
                        GenericAssetsGenerator.EnchantingElements.EntriesBackground.WIDTH,
                        GenericAssetsGenerator.EnchantingElements.EntriesBackground.HEIGHT,
                        59,
                        13
                    )
                }
                .build()
                .apply(
                    TextureImage.open(
                        manager,
                        GenericAssetsGenerator.EnchantingElements.ENTRIES_BACKGROUND
                    ),
                    transformed
                )

            val entryStatuses =
                TextureImage.open(manager, GenericAssetsGenerator.EnchantingElements.ENTRY_STATUSES)

            // Overwrite spell entries with empty status to keep the same with default texture
            val emptyStatus =
                TextureImage.createNew(transformed.imageWidth(), transformed.imageHeight(), null)

            ImageTransformer.builder(
                    entryStatuses.imageWidth(),
                    entryStatuses.imageHeight(),
                    transformed.imageWidth(),
                    transformed.imageHeight()
                )
                .apply {
                    for (i in 0 until 3) {
                        copyRect(
                            0,
                            19,
                            entryStatuses.imageWidth(),
                            19,
                            60,
                            14 + i * 19,
                        )
                    }
                }
                .build()
                .apply(entryStatuses, emptyStatus)

            transformed.applyOverlay(emptyStatus)

            // Spell entry statuses
            ImageTransformer.builder(
                    GenericAssetsGenerator.EnchantingElements.EntryStatuses.WIDTH,
                    GenericAssetsGenerator.EnchantingElements.EntryStatuses.HEIGHT,
                    transformed.imageWidth(),
                    transformed.imageHeight()
                )
                .apply {
                    copyRect(
                        0,
                        0,
                        GenericAssetsGenerator.EnchantingElements.EntryStatuses.WIDTH,
                        GenericAssetsGenerator.EnchantingElements.EntryStatuses.HEIGHT,
                        0,
                        166
                    )
                }
                .build()
                .apply(entryStatuses, transformed)

            // Book icon
            val book = TextureImage.open(manager, Identifier("item/book"))
            book.toGrayscale()
            val overlay = TextureImage.createNew(book.imageWidth(), book.imageHeight(), null)
            overlay.image.fillRect(
                0,
                0,
                overlay.imageWidth(),
                overlay.imageHeight(),
                0xFFFFFFFF.toUInt().toInt()
            )
            book.applyOverlayOnExisting(overlay)
            ImageTransformer.builder(
                    book.imageWidth(),
                    book.imageHeight(),
                    transformed.imageWidth(),
                    transformed.imageHeight()
                )
                .apply { copyRect(0, 0, book.imageWidth(), book.imageHeight(), 240, 0) }
                .build()
                .apply(book, transformed)

            return transformed
        }
    }
}
