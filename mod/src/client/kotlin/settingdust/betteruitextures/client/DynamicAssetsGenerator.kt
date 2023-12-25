package settingdust.betteruitextures.client

import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack
import net.minecraft.resource.ResourceManager

sealed class DynamicAssetsGenerator {
    open val modId: String? = null

    internal abstract fun regenerateDynamicAssets(
        manager: ResourceManager,
        dynamicPack: DynamicTexturePack
    )
}
