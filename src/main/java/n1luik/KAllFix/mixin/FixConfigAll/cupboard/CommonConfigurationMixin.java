package n1luik.KAllFix.mixin.FixConfigAll.cupboard;

import com.cupboard.config.CommonConfiguration;
import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CommonConfiguration.class, remap = false)
public class CommonConfigurationMixin {
    @Shadow(remap = false) public boolean logOffthreadEntityAdd;

    @Inject(method = "deserialize", at = @At("RETURN"), remap = false)
    public void impl1(JsonObject data, CallbackInfo ci) {
        logOffthreadEntityAdd = false;
    }
}


