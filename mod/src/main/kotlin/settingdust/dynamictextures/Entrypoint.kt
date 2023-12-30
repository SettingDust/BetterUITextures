package settingdust.dynamictextures

import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager

fun init() {}

object DynamicTextures {
    const val ID = "dynamic-textures"

    val logger = LogManager.getLogger()!!

    fun identifier(name: String): Identifier {
        return Identifier(ID, name)
    }
}
