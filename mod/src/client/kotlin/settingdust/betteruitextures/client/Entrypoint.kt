package settingdust.betteruitextures.client

import com.terraformersmc.modmenu.api.ModMenuApi
import net.fabricmc.loader.api.FabricLoader

fun init() {
    GenericAssetsGenerator.register()
    if (FabricLoader.getInstance().isModLoaded("fwaystones"))
        FabricWaystonesAssetsGenerator.register()
}

object ModMenuEntrypoint : ModMenuApi {}
