package settingdust.betteruitextures.mixin.fwaystones;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import wraith.fwaystones.screen.WaystoneBlockScreen;

@Mixin(WaystoneBlockScreen.class)
public interface WaystoneBlockScreenAccessor {
    @Accessor("TEXTURE")
    static Identifier texture() {
        throw new IllegalStateException();
    }

    @Accessor("CONFIG_TEXTURE")
    static Identifier configTexture() {
        throw new IllegalStateException();
    }
}
