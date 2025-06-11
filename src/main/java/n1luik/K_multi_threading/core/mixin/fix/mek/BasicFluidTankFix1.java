package n1luik.K_multi_threading.core.mixin.fix.mek;

import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BasicFluidTank.class, priority = 1100)
public class BasicFluidTankFix1 {
    @Inject(method = "insert", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/FluidStack;grow(I)V"), cancellable = true, remap = false)
    public void fix1(FluidStack stack, Action action, AutomationType automationType, CallbackInfoReturnable<FluidStack> cir){
        if (stack.getFluid() == Fluids.EMPTY)
            cir.setReturnValue(stack);
    }
}

