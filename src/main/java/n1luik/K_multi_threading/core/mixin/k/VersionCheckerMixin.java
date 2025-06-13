package n1luik.K_multi_threading.core.mixin.k;

import net.minecraftforge.fml.VersionChecker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VersionChecker.class, priority = 1100)
@Deprecated
public class VersionCheckerMixin {
    @Inject(method = "startVersionCheck", at = @At("HEAD"), cancellable = true, remap = false)
    private static void startVersionCheck(CallbackInfo ci) {
        ci.cancel();
    }
}
