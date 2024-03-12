package settingdust.dynamictextures.client.datagen

import java.util.function.BiConsumer
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider
import net.minecraft.data.DataOutput
import net.minecraft.util.Identifier
import settingdust.dynamictextures.DynamicTextures
import settingdust.dynamictextures.client.Border
import settingdust.dynamictextures.client.CopyNinePatch
import settingdust.dynamictextures.client.CopyRect
import settingdust.dynamictextures.client.DynamicTexture
import settingdust.dynamictextures.client.Fixed
import settingdust.dynamictextures.client.NinePatch
import settingdust.dynamictextures.client.Overlay
import settingdust.dynamictextures.client.Point
import settingdust.dynamictextures.client.PredefinedTexture
import settingdust.dynamictextures.client.Rect
import settingdust.dynamictextures.client.RemoveBorder
import settingdust.dynamictextures.client.RemoveColor
import settingdust.dynamictextures.client.RemoveRect
import settingdust.dynamictextures.client.Size
import settingdust.dynamictextures.client.TextureModifier
import settingdust.dynamictextures.client.codecFactory

fun init(generator: FabricDataGenerator) {
    val pack = generator.createPack()
    pack.addProvider(::PredefinedTextureProvider)
    pack.addProvider(::GenericDynamicTextureProvider)
    pack.addProvider(::DynamicTextureProvider)
}

private const val i = 176

private class PredefinedTextureProvider(dataOutput: FabricDataOutput) :
    FabricCodecDataProvider<PredefinedTexture>(
        dataOutput,
        DataOutput.OutputType.RESOURCE_PACK,
        "dynamic_texture/defined",
        codecFactory.create()
    ) {
    override fun getName() = "Predefined Textures"

    override fun configure(provider: BiConsumer<Identifier, PredefinedTexture>) {
        provider.accept(
            DynamicTextures.identifier("enchanting_elements/entries_background"),
            NinePatch(
                source = Identifier("gui/container/enchanting_table"),
                border = Border(first = Size(1, 1), second = Size(1, 1)),
                sourceRect = Rect(59, 13, 110, 59)
            )
        )
        provider.accept(
            DynamicTextures.identifier("enchanting_elements/entry_statuses"),
            NinePatch(
                source = Identifier("gui/container/enchanting_table"),
                border = Border(first = Size(2, 2), second = Size(2, 2)),
                sourceRect = Rect(0, 166, 108, 57)
            )
        )

        provider.accept(
            DynamicTextures.identifier("inventory/top"),
            NinePatch(
                source = Identifier("gui/container/generic_54"),
                border = Border(Size(7, 17), Size(7, 14)),
                sourceRect = Rect(0, 0, 176, 139),
                targetSize = Size(176, 83),
                centerColorPoint = Point(5, 7)
            )
        )
        provider.accept(
            DynamicTextures.identifier("inventory/bottom"),
            Fixed(
                source = Identifier("gui/container/generic_54"),
                sourceRect = Rect(0, 139, 176, 83)
            )
        )

        provider.accept(
            DynamicTextures.identifier("slot/single"),
            Fixed(source = Identifier("gui/container/beacon"), sourceRect = Rect(133, 106, 22, 23))
        )

        provider.accept(
            DynamicTextures.identifier("standalone_window"),
            NinePatch(
                source = Identifier("gui/container/creative_inventory/tab_items"),
                border = Border(Size(4, 17), Size(7, 6)),
                sourceRect = Rect(0, 0, 195, 136),
                targetSize = Size(176, 83),
                centerColorPoint = Point(6, 106)
            )
        )
    }
}

private val SLOT_BG_COLOR = "FF8B8B8B".toUInt(16)
private val BG_COLOR = "FFC6C6C6".toUInt(16)

private const val INVENTORY_BOTTOM = "dynamic_texture/defined/inventory/bottom"
private const val INVENTORY_TOP = "dynamic_texture/defined/inventory/top"

private const val SLOT_RAW = "dynamic_texture/defined/slot/raw"
private const val SLOT_SINGLE = "dynamic_texture/defined/slot/single"
private const val SLOT_ABREAST = "dynamic_texture/defined/slot/abreast"

private const val ENCHANTING_ENTRIES_BG =
    "dynamic_texture/defined/enchanting_elements/entries_background"
private const val ENCHANTING_ENTRIES_STATUSES =
    "dynamic_texture/defined/enchanting_elements/entry_statuses"

private const val STANDALONE_WINDOW = "dynamic_texture/defined/standalone_window"

private class GenericDynamicTextureProvider(dataOutput: FabricDataOutput) :
    FabricCodecDataProvider<DynamicTexture>(
        dataOutput,
        DataOutput.OutputType.RESOURCE_PACK,
        "dynamic_texture/generic_modifier",
        codecFactory.create()
    ) {
    override fun getName() = "Dynamic Textures Generic Modifiers"

    override fun configure(provider: BiConsumer<Identifier, DynamicTexture>) {
        provider.accept(
            DynamicTextures.identifier("slot/raw"),
            DynamicTexture(
                targetTexture = DynamicTextures.identifier(SLOT_RAW),
                size = Size(18, 18),
                modifiers =
                    CopyRect(
                        sourceTexture = Identifier("gui/container/generic_54"),
                        fromRect = Rect(25, 35, 18, 18),
                        targetRect = Rect(0, 0, 18, 18)
                    )
            )
        )
        provider.accept(
            DynamicTextures.identifier("slot/abreast"),
            DynamicTexture(
                targetTexture = DynamicTextures.identifier(SLOT_ABREAST),
                size = Size(42, 23),
                modifiers =
                    setOf(
                        CopyRect(
                            sourceTexture = DynamicTextures.identifier(SLOT_SINGLE),
                            fromRect = Rect(0, 0, 21, 23),
                            targetRect = Rect(0, 0, 21, 23)
                        ),
                        CopyRect(
                            sourceTexture = DynamicTextures.identifier(SLOT_SINGLE),
                            fromRect = Rect(1, 0, 21, 23),
                            targetRect = Rect(21, 0, 21, 23)
                        ),
                    )
            )
        )
    }
}

val inventoryBottom = DynamicTextures.identifier(INVENTORY_BOTTOM)

val slotSingle = DynamicTextures.identifier(SLOT_SINGLE)
val slotRaw = DynamicTextures.identifier(SLOT_RAW)

private class DynamicTextureProvider(dataOutput: FabricDataOutput) :
    FabricCodecDataProvider<DynamicTexture>(
        dataOutput,
        DataOutput.OutputType.RESOURCE_PACK,
        "dynamic_texture/modifier",
        codecFactory.create()
    ) {
    companion object {
        fun MutableSet<TextureModifier>.removeBorderAndBackground(width: Int, height: Int) {
            this +=
                RemoveBorder(
                    rect = Rect(0, 0, width, height),
                    border = Border(Size(6, 6), Size(6, 6))
                )

            this += RemoveColor(rect = Rect(0, 0, width, height), color = BG_COLOR)
        }

        fun MutableSet<TextureModifier>.removeSlotBackground(targetRect: Rect) {
            this += RemoveBorder(rect = targetRect, border = Border(Size(1, 1), Size(2, 2)))
            this += RemoveColor(rect = targetRect, color = SLOT_BG_COLOR)
        }

        fun MutableSet<TextureModifier>.inventoryTopOverlay(targetRect: Rect) =
            add(
                Overlay(
                    invert = true,
                    sourceTextures =
                        DynamicTexture(
                            size = Size(256, 256),
                            modifiers =
                                CopyNinePatch(
                                    sourceTexture = DynamicTextures.identifier(INVENTORY_TOP),
                                    border = Border(Size(7, 17), Size(7, 14)),
                                    targetRect = targetRect
                                )
                        )
                )
            )

        fun MutableSet<TextureModifier>.windowOverlay(targetRect: Rect) =
            add(
                Overlay(
                    invert = true,
                    sourceTextures =
                        DynamicTexture(
                            size = Size(256, 256),
                            modifiers =
                                CopyNinePatch(
                                    sourceTexture = DynamicTextures.identifier(STANDALONE_WINDOW),
                                    border = Border(Size(4, 17), Size(7, 6)),
                                    targetRect = targetRect
                                )
                        )
                )
            )
    }

    override fun getName() = "Dynamic Textures Modifiers"

    override fun configure(provider: BiConsumer<Identifier, DynamicTexture>) {
        DynamicTextureProvider::class
            .sealedSubclasses
            .map { it.objectInstance!! }
            .forEach { it.provide(provider) }
    }

    sealed class DynamicTextureProvider(
        val provide: (provider: BiConsumer<Identifier, DynamicTexture>) -> Unit
    )

    data object FarmersDelight :
        DynamicTextureProvider({
            it.accept(
                DynamicTextures.identifier("farmersdelight/cooking_pot"),
                DynamicTexture(
                    modId = "farmersdelight",
                    targetTexture = Identifier("farmersdelight:gui/cooking_pot"),
                    modifiers =
                        buildSet {
                            val width = 176
                            val height = 166
                            val halfHeight = height / 2
                            removeBorderAndBackground(width, height)
                            this +=
                                CopyRect(
                                    sourceTexture = inventoryBottom,
                                    targetRect = Rect(0, halfHeight, width, halfHeight)
                                )

                            this +=
                                CopyRect(
                                    sourceTexture = slotSingle,
                                    targetRect = Rect(89, 51, 22, 23)
                                )

                            this +=
                                CopyRect(
                                    sourceTexture = slotSingle,
                                    targetRect = Rect(121, 51, 22, 23)
                                )

                            this +=
                                CopyRect(
                                    repeat = true,
                                    sourceTexture = slotRaw,
                                    targetRect = Rect(29, 16, 54, 36)
                                )

                            inventoryTopOverlay(Rect(0, 0, width, halfHeight))
                        }
                )
            )
        })

    data object FWaystones :
        DynamicTextureProvider({
            val waystoneScreenModifiers = buildSet {
                val width = 176
                val height = 176
                removeBorderAndBackground(width, height)
                windowOverlay(Rect(0, 0, width, height))
            }
            it.accept(
                DynamicTextures.identifier("fwaystones/waystone_config"),
                DynamicTexture(
                    modId = "fwaystones",
                    targetTexture = Identifier("fwaystones:gui/waystone_config"),
                    modifiers = waystoneScreenModifiers
                )
            )
            it.accept(
                DynamicTextures.identifier("fwaystones/waystone"),
                DynamicTexture(
                    modId = "fwaystones",
                    targetTexture = Identifier("fwaystones:gui/waystone"),
                    modifiers = waystoneScreenModifiers
                )
            )
        })

    data object IllagerInvasion :
        DynamicTextureProvider({
            it.accept(
                DynamicTextures.identifier("illagerinvasion/imbuing_table"),
                DynamicTexture(
                    modId = "illagerinvasion",
                    targetTexture = Identifier("illagerinvasion:gui/container/imbuing_table"),
                    modifiers =
                        buildSet {
                            val width = 176
                            val height = 166
                            val halfHeight = height / 2
                            removeBorderAndBackground(width, height)
                            this +=
                                CopyRect(
                                    sourceTexture = inventoryBottom,
                                    targetRect = Rect(0, halfHeight, width, halfHeight)
                                )

                            this +=
                                CopyRect(
                                    sourceTexture = slotSingle,
                                    targetRect = Rect(77, 50, 22, 23)
                                )

                            this +=
                                CopyRect(sourceTexture = slotRaw, targetRect = Rect(77, 10, 22, 23))

                            removeSlotBackground(Rect(25, 53, 18, 18))
                            removeSlotBackground(Rect(133, 53, 18, 18))
                            this +=
                                Overlay(
                                    invert = true,
                                    sourceTextures =
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                setOf(
                                                    CopyRect(
                                                        sourceTexture = slotSingle,
                                                        targetRect = Rect(23, 50, 22, 23)
                                                    ),
                                                    CopyRect(
                                                        sourceTexture = slotSingle,
                                                        targetRect = Rect(131, 50, 22, 23)
                                                    )
                                                )
                                        )
                                )

                            inventoryTopOverlay(Rect(0, 0, width, halfHeight))
                        }
                )
            )
        })

    data object PackItUp :
        DynamicTextureProvider({
            it.accept(
                DynamicTextures.identifier("pack_it_up/pack_bench"),
                DynamicTexture(
                    modId = "pack_it_up",
                    targetTexture = Identifier("pack_it_up:gui/pack_bench"),
                    modifiers =
                        buildSet {
                            val width = 176
                            val height = 166
                            val halfHeight = height / 2
                            removeBorderAndBackground(width, height)
                            this +=
                                CopyRect(
                                    sourceTexture = inventoryBottom,
                                    targetRect = Rect(0, halfHeight, width, halfHeight)
                                )

                            removeSlotBackground(Rect(26, 46, 18, 18))
                            removeSlotBackground(Rect(75, 46, 18, 18))

                            this +=
                                CopyRect(
                                    sourceTexture = slotSingle,
                                    targetRect = Rect(131, 43, 22, 23)
                                )

                            this +=
                                Overlay(
                                    invert = true,
                                    sourceTextures =
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                setOf(
                                                    CopyRect(
                                                        sourceTexture = slotSingle,
                                                        targetRect = Rect(24, 43, 22, 23)
                                                    ),
                                                    CopyRect(
                                                        sourceTexture = slotSingle,
                                                        targetRect = Rect(73, 43, 22, 23)
                                                    )
                                                )
                                        )
                                )
                            inventoryTopOverlay(Rect(0, 0, width, halfHeight))
                        }
                )
            )
            it.accept(
                DynamicTextures.identifier("pack_it_up/pack_screen"),
                DynamicTexture(
                    modId = "pack_it_up",
                    targetTexture = Identifier("pack_it_up:gui/pack_screen"),
                    modifiers =
                        buildSet {
                            val width = 176
                            val height = 240
                            val halfHeight = height / 2
                            removeBorderAndBackground(width, height)
                            this +=
                                CopyRect(
                                    sourceTexture = inventoryBottom,
                                    targetRect = Rect(0, 157, width, halfHeight)
                                )

                            this +=
                                CopyRect(
                                    repeat = true,
                                    sourceTexture = slotRaw,
                                    targetRect = Rect(7, 17, 162, 126)
                                )

                            inventoryTopOverlay(Rect(0, 0, width, 157))
                        }
                )
            )
        })

    data object Runes :
        DynamicTextureProvider({
            it.accept(
                DynamicTextures.identifier("runes/crafting_altar"),
                DynamicTexture(
                    modId = "runes",
                    targetTexture = Identifier("runes:gui/crafting_altar"),
                    modifiers =
                        buildSet {
                            val width = 176
                            val height = 166
                            val halfHeight = height / 2
                            removeBorderAndBackground(width, height)
                            this +=
                                CopyRect(
                                    sourceTexture = inventoryBottom,
                                    targetRect = Rect(0, halfHeight, width, halfHeight)
                                )

                            this +=
                                CopyRect(
                                    sourceTexture = slotSingle,
                                    targetRect = Rect(24, 43, 22, 23)
                                )

                            this +=
                                CopyRect(
                                    sourceTexture = slotSingle,
                                    targetRect = Rect(73, 43, 22, 23)
                                )

                            this +=
                                CopyRect(
                                    sourceTexture = slotSingle,
                                    targetRect = Rect(131, 43, 22, 23)
                                )

                            inventoryTopOverlay(Rect(0, 0, width, 157))
                        }
                )
            )
        })

    data object SpellEngine :
        DynamicTextureProvider({
            it.accept(
                DynamicTextures.identifier("spell_engine/spell_binding"),
                DynamicTexture(
                    modId = "spell_engine",
                    targetTexture = Identifier("spell_engine:gui/spell_binding"),
                    modifiers =
                        buildSet {
                            val width = 176
                            val height = 166
                            val halfHeight = height / 2
                            removeBorderAndBackground(width, height)
                            this +=
                                CopyRect(
                                    sourceTexture = inventoryBottom,
                                    targetRect = Rect(0, halfHeight, width, halfHeight)
                                )

                            this += RemoveRect(rect = Rect(14, 46, 18, 18))
                            removeSlotBackground(Rect(34, 46, 18, 18))
                            this +=
                                Overlay(
                                    invert = true,
                                    sourceTextures =
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                CopyRect(
                                                    sourceTexture =
                                                        DynamicTextures.identifier(SLOT_ABREAST),
                                                    targetRect = Rect(12, 43, 42, 23)
                                                ),
                                        )
                                )

                            this +=
                                CopyRect(
                                    sourceTexture =
                                        DynamicTextures.identifier(ENCHANTING_ENTRIES_BG),
                                    targetRect = Rect(59, 13, 110, 59)
                                )
                            this +=
                                Overlay(
                                    onExisting = true,
                                    sourceTextures =
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                CopyRect(
                                                    repeat = true,
                                                    sourceTexture =
                                                        DynamicTextures.identifier(
                                                            ENCHANTING_ENTRIES_STATUSES
                                                        ),
                                                    fromRect = Rect(0, 19, 108, 19),
                                                    targetRect = Rect(60, 14, 108, 57)
                                                ),
                                        )
                                )
                            this +=
                                CopyRect(
                                    sourceTexture =
                                        DynamicTextures.identifier(ENCHANTING_ENTRIES_STATUSES),
                                    targetRect = Rect(0, 166, 108, 57)
                                )
                            inventoryTopOverlay(Rect(0, 0, width, halfHeight))
                        }
                )
            )
        })

    data object AmethystImbuement :
        DynamicTextureProvider({
            it.accept(
                DynamicTextures.identifier("amethyst_imbuement/crystal_altar"),
                DynamicTexture(
                    modId = "amethyst_imbuement",
                    targetTexture =
                        Identifier("amethyst_imbuement:gui/container/crystal_altar_gui"),
                    modifiers =
                        buildSet {
                            val width = 176
                            val height = 166
                            val halfHeight = height / 2
                            removeBorderAndBackground(width, height)
                            this +=
                                CopyRect(
                                    sourceTexture = inventoryBottom,
                                    targetRect = Rect(0, halfHeight, width, halfHeight)
                                )
                            this += RemoveColor(rect = Rect(30, 43, 58, 23), color = SLOT_BG_COLOR)
                            this +=
                                RemoveBorder(
                                    rect = Rect(32, 46, 18, 18),
                                    border = Border(Size(2, 2), Size(2, 2))
                                )
                            this +=
                                RemoveBorder(
                                    rect = Rect(50, 46, 18, 18),
                                    border = Border(Size(2, 2), Size(2, 2))
                                )
                            this +=
                                RemoveBorder(
                                    rect = Rect(68, 46, 18, 18),
                                    border = Border(Size(2, 2), Size(2, 2))
                                )
                            this +=
                                Overlay(
                                    invert = true,
                                    sourceTextures =
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                CopyNinePatch(
                                                    sourceTexture = slotSingle,
                                                    border = Border(Size(2, 3), Size(2, 2)),
                                                    targetRect = Rect(30, 43, 58, 23),
                                                    repeat = true
                                                )
                                        )
                                )

                            this +=
                                CopyRect(
                                    sourceTexture = slotSingle,
                                    targetRect = Rect(124, 43, 22, 23)
                                )

                            inventoryTopOverlay(Rect(0, 0, width, halfHeight))
                        }
                )
            )

            it.accept(
                DynamicTextures.identifier("amethyst_imbuement/imbuing_table"),
                DynamicTexture(
                    modId = "amethyst_imbuement",
                    targetTexture =
                        Identifier("amethyst_imbuement:gui/container/imbuing_table_gui"),
                    modifiers =
                        buildSet {
                            val width = 234
                            val height = 174
                            removeBorderAndBackground(width, height)
                            this +=
                                CopyRect(
                                    sourceTexture = inventoryBottom,
                                    fromRect = Rect(4, 0, 168, 81),
                                    targetRect = Rect(34, 91, 168, 81)
                                )

                            removeSlotBackground(Rect(71, 37, 18, 18))

                            this +=
                                Overlay(
                                    invert = true,
                                    sourceTextures =
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                CopyRect(
                                                    sourceTexture = slotRaw,
                                                    targetRect = Rect(71, 37, 18, 18)
                                                )
                                        )
                                )
                            this +=
                                CopyRect(
                                    sourceTexture = slotSingle,
                                    targetRect = Rect(5, 9, 22, 23)
                                )
                            this +=
                                CopyRect(
                                    sourceTexture = slotSingle,
                                    targetRect = Rect(5, 59, 22, 23)
                                )
                            this +=
                                CopyRect(
                                    sourceTexture = slotSingle,
                                    targetRect = Rect(92, 9, 22, 23)
                                )
                            this +=
                                CopyRect(
                                    sourceTexture = slotSingle,
                                    targetRect = Rect(92, 59, 22, 23)
                                )
                            this +=
                                CopyRect(sourceTexture = slotRaw, targetRect = Rect(29, 16, 18, 18))
                            this +=
                                CopyRect(sourceTexture = slotRaw, targetRect = Rect(50, 16, 18, 18))
                            this +=
                                CopyRect(sourceTexture = slotRaw, targetRect = Rect(71, 16, 18, 18))
                            this +=
                                CopyRect(sourceTexture = slotRaw, targetRect = Rect(29, 37, 18, 18))
                            this +=
                                CopyRect(sourceTexture = slotRaw, targetRect = Rect(49, 36, 20, 20))
                            this +=
                                CopyRect(sourceTexture = slotRaw, targetRect = Rect(29, 58, 18, 18))
                            this +=
                                CopyRect(sourceTexture = slotRaw, targetRect = Rect(50, 58, 18, 18))
                            this +=
                                CopyRect(sourceTexture = slotRaw, targetRect = Rect(71, 58, 18, 18))
                            this +=
                                Overlay(
                                    invert = true,
                                    sourceTextures =
                                        (DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                CopyNinePatch(
                                                    sourceTexture =
                                                        DynamicTextures.identifier(
                                                            STANDALONE_WINDOW
                                                        ),
                                                    border = Border(Size(4, 17), Size(7, 6)),
                                                    targetRect = Rect(0, 0, 234, 174)
                                                )
                                        ))
                                )
                            this +=
                                CopyRect(
                                    sourceTexture =
                                        DynamicTextures.identifier(ENCHANTING_ENTRIES_BG),
                                    targetRect = Rect(117, 17, 110, 59)
                                )
                            this +=
                                Overlay(
                                    onExisting = true,
                                    sourceTextures =
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                CopyRect(
                                                    repeat = true,
                                                    sourceTexture =
                                                        DynamicTextures.identifier(
                                                            ENCHANTING_ENTRIES_STATUSES
                                                        ),
                                                    fromRect = Rect(0, 19, 108, 19),
                                                    targetRect = Rect(118, 18, 108, 57)
                                                ),
                                        )
                                )
                            this +=
                                CopyRect(
                                    sourceTexture =
                                        DynamicTextures.identifier(ENCHANTING_ENTRIES_STATUSES),
                                    targetRect = Rect(0, 174, 108, 57)
                                )
                        }
                )
            )

            it.accept(
                DynamicTextures.identifier("amethyst_imbuement/disenchanting_table"),
                DynamicTexture(
                    modId = "amethyst_imbuement",
                    targetTexture =
                        Identifier("amethyst_imbuement:gui/container/disenchanting_table_gui"),
                    modifiers =
                        buildSet {
                            val width = 176
                            val height = 166
                            val halfHeight = height / 2
                            removeBorderAndBackground(width, height)
                            this +=
                                CopyRect(
                                    sourceTexture = inventoryBottom,
                                    targetRect = Rect(0, halfHeight, width, halfHeight)
                                )

                            this += RemoveRect(rect = Rect(14, 46, 18, 18))
                            removeSlotBackground(Rect(34, 46, 18, 18))
                            this +=
                                Overlay(
                                    invert = true,
                                    sourceTextures =
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                CopyRect(
                                                    sourceTexture =
                                                        DynamicTextures.identifier(SLOT_ABREAST),
                                                    targetRect = Rect(12, 43, 42, 23)
                                                ),
                                        )
                                )

                            this +=
                                CopyRect(
                                    sourceTexture =
                                        DynamicTextures.identifier(ENCHANTING_ENTRIES_BG),
                                    targetRect = Rect(59, 13, 110, 59)
                                )
                            this +=
                                CopyRect(
                                    sourceTexture =
                                        DynamicTextures.identifier(ENCHANTING_ENTRIES_STATUSES),
                                    targetRect = Rect(0, 166, 108, 57)
                                )
                            inventoryTopOverlay(Rect(0, 0, width, halfHeight))
                            this +=
                                Overlay(
                                    onExisting = true,
                                    sourceTextures =
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                CopyRect(
                                                    repeat = true,
                                                    sourceTexture =
                                                        DynamicTextures.identifier(
                                                            ENCHANTING_ENTRIES_STATUSES
                                                        ),
                                                    fromRect = Rect(0, 19, 108, 19),
                                                    targetRect = Rect(60, 14, 108, 57)
                                                ),
                                        )
                                )
                        }
                )
            )

            it.accept(
                DynamicTextures.identifier("amethyst_imbuement/altar_of_experience"),
                DynamicTexture(
                    modId = "amethyst_imbuement",
                    targetTexture =
                        Identifier("amethyst_imbuement:gui/container/altar_of_experience_gui"),
                    modifiers =
                        buildSet {
                            val width = 176
                            val height = 166
                            val halfHeight = height / 2
                            removeBorderAndBackground(width, height)
                            this +=
                                CopyRect(
                                    sourceTexture = inventoryBottom,
                                    targetRect = Rect(0, halfHeight, width, halfHeight)
                                )

                            this +=
                                CopyNinePatch(
                                    sourceTexture =
                                        DynamicTextures.identifier(ENCHANTING_ENTRIES_BG),
                                    border = Border(Size(1, 1), Size(1, 1)),
                                    targetRect = Rect(25, 32, 126, 46)
                                )

                            this +=
                                CopyNinePatch(
                                    sourceTexture =
                                        DynamicTextures.identifier(ENCHANTING_ENTRIES_STATUSES),
                                    border = Border(Size(1, 1), Size(1, 1)),
                                    sourceRect = Rect(0, 0, 108, 19),
                                    targetRect = Rect(0, 166, 124, 11)
                                )
                            this +=
                                CopyNinePatch(
                                    sourceTexture =
                                        DynamicTextures.identifier(ENCHANTING_ENTRIES_STATUSES),
                                    border = Border(Size(1, 1), Size(1, 1)),
                                    sourceRect = Rect(0, 19, 108, 19),
                                    targetRect = Rect(0, 177, 124, 11)
                                )
                            this +=
                                CopyNinePatch(
                                    sourceTexture =
                                        DynamicTextures.identifier(ENCHANTING_ENTRIES_STATUSES),
                                    border = Border(Size(1, 1), Size(1, 1)),
                                    sourceRect = Rect(0, 38, 108, 19),
                                    targetRect = Rect(0, 188, 124, 11)
                                )
                            inventoryTopOverlay(Rect(0, 0, width, halfHeight))
                            this +=
                                Overlay(
                                    onExisting = true,
                                    sourceTextures =
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                setOf(
                                                    CopyNinePatch(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                ENCHANTING_ENTRIES_STATUSES
                                                            ),
                                                        border = Border(Size(1, 1), Size(1, 1)),
                                                        sourceRect = Rect(0, 19, 108, 19),
                                                        targetRect = Rect(25, 33, 124, 11)
                                                    ),
                                                    CopyNinePatch(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                ENCHANTING_ENTRIES_STATUSES
                                                            ),
                                                        border = Border(Size(1, 1), Size(1, 1)),
                                                        sourceRect = Rect(0, 19, 108, 19),
                                                        targetRect = Rect(25, 44, 124, 11)
                                                    ),
                                                    CopyNinePatch(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                ENCHANTING_ENTRIES_STATUSES
                                                            ),
                                                        border = Border(Size(1, 1), Size(1, 1)),
                                                        sourceRect = Rect(0, 19, 108, 19),
                                                        targetRect = Rect(25, 55, 124, 11)
                                                    ),
                                                    CopyNinePatch(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                ENCHANTING_ENTRIES_STATUSES
                                                            ),
                                                        border = Border(Size(1, 1), Size(1, 1)),
                                                        sourceRect = Rect(0, 19, 108, 19),
                                                        targetRect = Rect(25, 66, 124, 11)
                                                    )
                                                ),
                                        )
                                )
                        }
                )
            )
        })
}
