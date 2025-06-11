package n1luik.K_multi_threading.core.mixin.fix.alexscaves;

import com.github.alexmodguy.alexscaves.server.event.CommonEvents;
import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommonEvents.class)
public class CommonEventsFix1 {

    @Unique
    boolean K_multi_threading$isChunkLocked;

    @Inject(method = "onEntityJoinWorld", at = @At("HEAD"), remap = false)
    public void fix1(MobSpawnEvent.FinalizeSpawn event, CallbackInfo ci){
        if (event.getLevel().getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig) {
            K_multi_threading$isChunkLocked = iWorldChunkLockedConfig.pushThread() > 0;
        }
    }

    @Inject(method = "onEntityJoinWorld", at = @At("RETURN"), remap = false)
    public void fix2(MobSpawnEvent.FinalizeSpawn event, CallbackInfo ci){
        if (K_multi_threading$isChunkLocked && event.getLevel().getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig) {
            iWorldChunkLockedConfig.pop();
        }
    }
}
