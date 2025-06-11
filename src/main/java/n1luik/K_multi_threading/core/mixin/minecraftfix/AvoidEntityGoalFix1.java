package n1luik.K_multi_threading.core.mixin.minecraftfix;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(AvoidEntityGoal.class)
public class AvoidEntityGoalFix1<T extends LivingEntity> {
    @Shadow @Nullable protected T toAvoid;

    @Inject(method = "tick()V", at = @At("HEAD"),cancellable = true)
    public void fix1(CallbackInfo ci){
        if (this.toAvoid == null)ci.cancel();
    }
}
