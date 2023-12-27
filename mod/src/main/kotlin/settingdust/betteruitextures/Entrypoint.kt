package settingdust.betteruitextures

import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager

fun init() {}

object BetterUITextures {
    const val ID = "better-ui-textures"

    val logger = LogManager.getLogger()!!

    fun identifier(name: String): Identifier {
        return Identifier(ID, name)
    }
}
