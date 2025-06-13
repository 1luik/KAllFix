package n1luik.K_multi_threading.core.mixin.fix.nuclearcraft;

import com.google.common.collect.Iterables;
import igentuman.nc.handler.sided.capability.FluidCapabilityHandler;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Mixin(value = FluidCapabilityHandler.class, priority = 1100)
public class FluidCapabilityHandlerFix1 {
    @Shadow(remap = false) public HashMap<Integer, List<FluidStack>> allowedFluids;

    @Inject(method = "isValidSlotFluid", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), cancellable = true, remap = false)
    public void fix2(int id, FluidStack fluid, CallbackInfoReturnable<Boolean> cir){
        if (allowedFluids == null) cir.setReturnValue(false);
    }

    @Redirect(method = "isValidSlotFluid", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"), remap = false)
    public Iterator fix1(List instance){
        if (instance == null) return Collections.emptyIterator();
        return instance.iterator();
    }
}
