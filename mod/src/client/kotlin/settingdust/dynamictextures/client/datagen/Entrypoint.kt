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
import settingdust.dynamictextures.client.codecFactory

fun init(generator: FabricDataGenerator) {
    val pack = generator.createPack()
    pack.addProvider(::PredefinedTextureProvider)
    pack.addProvider(::GenericDynamicTextureProvider)
    pack.addProvider(::DynamicTextureProvider)
}

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
                centerColorPoint = Point(6, 18)
            )
        )
    }
}

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
                targetTexture = DynamicTextures.identifier("dynamic_texture/defined/slot/raw"),
                size = Size(18, 18),
                modifiers =
                    setOf(
                        CopyRect(
                            sourceTexture = Identifier("gui/container/generic_54"),
                            fromRect = Rect(25, 35, 18, 18),
                            targetRect = Rect(0, 0, 18, 18)
                        )
                    )
            )
        )
        provider.accept(
            DynamicTextures.identifier("slot/abreast"),
            DynamicTexture(
                targetTexture = DynamicTextures.identifier("dynamic_texture/defined/slot/abreast"),
                size = Size(42, 23),
                modifiers =
                    setOf(
                        CopyRect(
                            sourceTexture =
                                DynamicTextures.identifier("dynamic_texture/defined/slot/single"),
                            fromRect = Rect(0, 0, 21, 23),
                            targetRect = Rect(0, 0, 21, 23)
                        ),
                        CopyRect(
                            sourceTexture =
                                DynamicTextures.identifier("dynamic_texture/defined/slot/single"),
                            fromRect = Rect(1, 0, 21, 23),
                            targetRect = Rect(21, 0, 21, 23)
                        ),
                    )
            )
        )
    }
}

private class DynamicTextureProvider(dataOutput: FabricDataOutput) :
    FabricCodecDataProvider<DynamicTexture>(
        dataOutput,
        DataOutput.OutputType.RESOURCE_PACK,
        "dynamic_texture/modifier",
        codecFactory.create()
    ) {
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
                        setOf(
                            RemoveBorder(
                                rect = Rect(0, 0, 176, 166),
                                border = Border(Size(6, 6), Size(6, 6))
                            ),
                            RemoveColor(rect = Rect(0, 0, 176, 166), color = "FFC6C6C6".toUInt(16)),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier(
                                        "dynamic_texture/defined/inventory/bottom"
                                    ),
                                targetRect = Rect(0, 83, 176, 83)
                            ),
                            Overlay(
                                invert = true,
                                sourceTextures =
                                    setOf(
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                setOf(
                                                    CopyNinePatch(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                "dynamic_texture/defined/inventory/top"
                                                            ),
                                                        border = Border(Size(7, 17), Size(7, 14)),
                                                        targetRect = Rect(0, 0, 176, 83)
                                                    )
                                                )
                                        )
                                    )
                            ),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier(
                                        "dynamic_texture/defined/slot/single"
                                    ),
                                targetRect = Rect(89, 51, 22, 23)
                            ),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier(
                                        "dynamic_texture/defined/slot/single"
                                    ),
                                targetRect = Rect(121, 51, 22, 23)
                            ),
                            CopyRect(
                                repeat = true,
                                sourceTexture =
                                    DynamicTextures.identifier("dynamic_texture/defined/slot/raw"),
                                targetRect = Rect(29, 16, 54, 36)
                            )
                        )
                )
            )
        })

    data object FWaystones :
        DynamicTextureProvider({
            val waystoneScreenModifiers =
                setOf(
                    RemoveBorder(
                        rect = Rect(0, 0, 177, 176),
                        border = Border(Size(4, 17), Size(7, 6))
                    ),
                    RemoveColor(rect = Rect(0, 0, 177, 176), color = "FFC6C6C6".toUInt(16)),
                    Overlay(
                        invert = true,
                        sourceTextures =
                            setOf(
                                DynamicTexture(
                                    size = Size(256, 256),
                                    modifiers =
                                        setOf(
                                            CopyNinePatch(
                                                sourceTexture =
                                                    DynamicTextures.identifier(
                                                        "dynamic_texture/defined/standalone_window"
                                                    ),
                                                border = Border(Size(4, 17), Size(7, 6)),
                                                targetRect = Rect(0, 0, 177, 176)
                                            )
                                        )
                                )
                            )
                    ),
                )
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
                        setOf(
                            RemoveBorder(
                                rect = Rect(0, 0, 176, 166),
                                border = Border(Size(6, 6), Size(6, 6))
                            ),
                            RemoveColor(rect = Rect(0, 0, 176, 166), color = "FFC6C6C6".toUInt(16)),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier(
                                        "dynamic_texture/defined/inventory/bottom"
                                    ),
                                targetRect = Rect(0, 83, 176, 83)
                            ),
                            Overlay(
                                invert = true,
                                sourceTextures =
                                    setOf(
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                setOf(
                                                    CopyNinePatch(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                "dynamic_texture/defined/inventory/top"
                                                            ),
                                                        border = Border(Size(7, 17), Size(7, 14)),
                                                        targetRect = Rect(0, 0, 176, 83)
                                                    )
                                                )
                                        )
                                    )
                            ),
                            RemoveBorder(
                                rect = Rect(23, 50, 22, 23),
                                border = Border(Size(2, 4), Size(3, 3))
                            ),
                            RemoveColor(rect = Rect(23, 50, 22, 23), color = "FF8B8B8B".toUInt(16)),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier(
                                        "dynamic_texture/defined/slot/single"
                                    ),
                                targetRect = Rect(77, 50, 22, 23)
                            ),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier("dynamic_texture/defined/slot/raw"),
                                targetRect = Rect(77, 10, 22, 23)
                            ),
                            RemoveBorder(
                                rect = Rect(131, 50, 22, 23),
                                border = Border(Size(2, 4), Size(3, 3))
                            ),
                            RemoveColor(
                                rect = Rect(133, 53, 18, 18),
                                color = "FF8B8B8B".toUInt(16)
                            ),
                            Overlay(
                                invert = true,
                                sourceTextures =
                                    setOf(
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                setOf(
                                                    CopyRect(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                "dynamic_texture/defined/slot/single"
                                                            ),
                                                        targetRect = Rect(23, 50, 22, 23)
                                                    ),
                                                    CopyRect(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                "dynamic_texture/defined/slot/single"
                                                            ),
                                                        targetRect = Rect(131, 50, 22, 23)
                                                    )
                                                )
                                        )
                                    )
                            ),
                        )
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
                        setOf(
                            RemoveBorder(
                                rect = Rect(0, 0, 176, 166),
                                border = Border(Size(6, 6), Size(6, 6))
                            ),
                            RemoveColor(rect = Rect(0, 0, 176, 166), color = "FFC6C6C6".toUInt(16)),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier(
                                        "dynamic_texture/defined/inventory/bottom"
                                    ),
                                targetRect = Rect(0, 83, 176, 83)
                            ),
                            Overlay(
                                invert = true,
                                sourceTextures =
                                    setOf(
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                setOf(
                                                    CopyNinePatch(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                "dynamic_texture/defined/inventory/top"
                                                            ),
                                                        border = Border(Size(7, 17), Size(7, 14)),
                                                        targetRect = Rect(0, 0, 176, 83)
                                                    )
                                                )
                                        )
                                    )
                            ),
                            RemoveBorder(
                                rect = Rect(24, 43, 22, 23),
                                border = Border(Size(3, 4), Size(3, 3))
                            ),
                            RemoveColor(rect = Rect(26, 46, 18, 18), color = "FF8B8B8B".toUInt(16)),
                            RemoveBorder(
                                rect = Rect(73, 43, 22, 23),
                                border = Border(Size(3, 4), Size(3, 3))
                            ),
                            RemoveColor(rect = Rect(75, 46, 18, 18), color = "FF8B8B8B".toUInt(16)),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier(
                                        "dynamic_texture/defined/slot/single"
                                    ),
                                targetRect = Rect(131, 43, 22, 23)
                            ),
                            Overlay(
                                invert = true,
                                sourceTextures =
                                    setOf(
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                setOf(
                                                    CopyRect(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                "dynamic_texture/defined/slot/single"
                                                            ),
                                                        targetRect = Rect(24, 43, 22, 23)
                                                    ),
                                                    CopyRect(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                "dynamic_texture/defined/slot/single"
                                                            ),
                                                        targetRect = Rect(73, 43, 22, 23)
                                                    )
                                                )
                                        )
                                    )
                            ),
                        )
                )
            )
            it.accept(
                DynamicTextures.identifier("pack_it_up/pack_screen"),
                DynamicTexture(
                    modId = "pack_it_up",
                    targetTexture = Identifier("pack_it_up:gui/pack_screen"),
                    modifiers =
                        setOf(
                            RemoveBorder(
                                rect = Rect(0, 0, 176, 240),
                                border = Border(Size(6, 6), Size(6, 6))
                            ),
                            RemoveColor(rect = Rect(0, 0, 176, 240), color = "FFC6C6C6".toUInt(16)),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier(
                                        "dynamic_texture/defined/inventory/bottom"
                                    ),
                                targetRect = Rect(0, 157, 176, 83)
                            ),
                            Overlay(
                                invert = true,
                                sourceTextures =
                                    setOf(
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                setOf(
                                                    CopyNinePatch(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                "dynamic_texture/defined/inventory/top"
                                                            ),
                                                        border = Border(Size(7, 17), Size(7, 14)),
                                                        targetRect = Rect(0, 0, 176, 157)
                                                    )
                                                )
                                        )
                                    )
                            ),
                            CopyRect(
                                repeat = true,
                                sourceTexture =
                                    DynamicTextures.identifier("dynamic_texture/defined/slot/raw"),
                                targetRect = Rect(7, 17, 162, 126)
                            )
                        )
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
                        setOf(
                            RemoveBorder(
                                rect = Rect(0, 0, 176, 166),
                                border = Border(Size(6, 6), Size(6, 6))
                            ),
                            RemoveColor(rect = Rect(0, 0, 176, 166), color = "FFC6C6C6".toUInt(16)),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier(
                                        "dynamic_texture/defined/inventory/bottom"
                                    ),
                                targetRect = Rect(0, 83, 176, 83)
                            ),
                            Overlay(
                                invert = true,
                                sourceTextures =
                                    setOf(
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                setOf(
                                                    CopyNinePatch(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                "dynamic_texture/defined/inventory/top"
                                                            ),
                                                        border = Border(Size(7, 17), Size(7, 14)),
                                                        targetRect = Rect(0, 0, 176, 83)
                                                    )
                                                )
                                        )
                                    )
                            ),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier(
                                        "dynamic_texture/defined/slot/single"
                                    ),
                                targetRect = Rect(24, 44, 22, 23)
                            ),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier(
                                        "dynamic_texture/defined/slot/single"
                                    ),
                                targetRect = Rect(73, 44, 22, 23)
                            ),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier(
                                        "dynamic_texture/defined/slot/single"
                                    ),
                                targetRect = Rect(131, 44, 22, 23)
                            ),
                        )
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
                        setOf(
                            RemoveBorder(
                                rect = Rect(0, 0, 176, 166),
                                border = Border(Size(4, 17), Size(7, 6))
                            ),
                            RemoveColor(rect = Rect(0, 0, 176, 166), color = "FFC6C6C6".toUInt(16)),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier(
                                        "dynamic_texture/defined/inventory/bottom"
                                    ),
                                targetRect = Rect(0, 83, 176, 83)
                            ),
                            Overlay(
                                invert = true,
                                sourceTextures =
                                    setOf(
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                setOf(
                                                    CopyNinePatch(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                "dynamic_texture/defined/inventory/top"
                                                            ),
                                                        border = Border(Size(7, 17), Size(7, 14)),
                                                        targetRect = Rect(0, 0, 176, 83)
                                                    )
                                                )
                                        )
                                    )
                            ),
                            RemoveRect(rect = Rect(12, 43, 21, 23)),
                            RemoveBorder(
                                rect = Rect(33, 43, 21, 23),
                                border = Border(Size(2, 4), Size(3, 3))
                            ),
                            RemoveColor(rect = Rect(34, 46, 18, 18), color = "FF8B8B8B".toUInt(16)),
                            Overlay(
                                invert = true,
                                sourceTextures =
                                    setOf(
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                setOf(
                                                    CopyRect(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                "dynamic_texture/defined/slot/abreast"
                                                            ),
                                                        targetRect = Rect(12, 43, 42, 23)
                                                    ),
                                                )
                                        )
                                    )
                            ),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier(
                                        "dynamic_texture/defined/enchanting_elements/entries_background"
                                    ),
                                targetRect = Rect(59, 13, 110, 59)
                            ),
                            Overlay(
                                onExisting = true,
                                sourceTextures =
                                    setOf(
                                        DynamicTexture(
                                            size = Size(256, 256),
                                            modifiers =
                                                setOf(
                                                    CopyRect(
                                                        sourceTexture =
                                                            DynamicTextures.identifier(
                                                                "dynamic_texture/defined/enchanting_elements/entry_statuses"
                                                            ),
                                                        fromRect = Rect(0, 19, 108, 19),
                                                        targetRect = Rect(60, 14, 108, 57)
                                                    ),
                                                )
                                        )
                                    )
                            ),
                            CopyRect(
                                sourceTexture =
                                    DynamicTextures.identifier(
                                        "dynamic_texture/defined/enchanting_elements/entry_statuses"
                                    ),
                                targetRect = Rect(0, 166, 108, 57)
                            ),
                        )
                )
            )
        })
}
