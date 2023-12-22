package settingdust.betteruitextures.client

import com.terraformersmc.modmenu.api.ModMenuApi

fun init() {
    GenericAssetsGenerator.register()
    FabricWaystonesAssetsGenerator.register()
}

object ModMenuEntrypoint : ModMenuApi {}
