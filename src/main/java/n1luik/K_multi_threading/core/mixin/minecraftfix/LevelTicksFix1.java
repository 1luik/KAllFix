package n1luik.K_multi_threading.core.mixin.minecraftfix;

import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.ScheduledTick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelTicks.class)
@Deprecated
public class LevelTicksFix1 {
    @Redirect(method = "sortContainersToTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ticks/LevelChunkTicks;peek()Lnet/minecraft/world/ticks/ScheduledTick;"))
    public <T> ScheduledTick<T> fix1(LevelChunkTicks<T> instance){
        synchronized (instance){
            return instance.peek();
        }
    }
}
