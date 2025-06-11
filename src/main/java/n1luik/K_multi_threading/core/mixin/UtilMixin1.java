package n1luik.K_multi_threading.core.mixin;

import n1luik.K_multi_threading.core.Base;
import net.minecraft.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ExecutorService;

@Mixin(Util.class)
public class UtilMixin1 {
    @Inject(method = "makeExecutor", at = @At("HEAD"), cancellable = true)
    private static void makeExecutor1(String p_137478_, CallbackInfoReturnable<ExecutorService> cir) {
        if (p_137478_.equals("Main")) {
            cir.setReturnValue(Base.getAsync());// != null ? Base.getAsync(): Base.getEx());
        }
    }
}
