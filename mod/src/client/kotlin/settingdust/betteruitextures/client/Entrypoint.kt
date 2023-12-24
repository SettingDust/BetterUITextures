package settingdust.betteruitextures.client

import com.terraformersmc.modmenu.api.ModMenuApi

fun init() {
    GenericAssetsGenerator.register()
}

object ModMenuEntrypoint : ModMenuApi {}
