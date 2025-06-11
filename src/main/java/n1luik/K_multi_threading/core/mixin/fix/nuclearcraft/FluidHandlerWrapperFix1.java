package n1luik.K_multi_threading.core.mixin.fix.nuclearcraft;

import igentuman.nc.handler.sided.capability.FluidHandlerWrapper;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FluidHandlerWrapper.class, priority = 1001)
public class FluidHandlerWrapperFix1 {
    @Inject(method = "fill", at = @At("HEAD"), cancellable = true, remap = false)
    public void fill(FluidStack resource, IFluidHandler.FluidAction action, CallbackInfoReturnable<Integer> cir){
        if (resource.getFluid() == Fluids.EMPTY){
            cir.setReturnValue(0);
        }
    }
}
