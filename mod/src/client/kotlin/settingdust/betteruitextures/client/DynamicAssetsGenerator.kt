package settingdust.betteruitextures.client

import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicTexturePack
import net.minecraft.resource.ResourceManager

sealed class DynamicAssetsGenerator {
    abstract val modId: String

    abstract fun regenerateDynamicAssets(manager: ResourceManager, dynamicPack: DynamicTexturePack)
}
