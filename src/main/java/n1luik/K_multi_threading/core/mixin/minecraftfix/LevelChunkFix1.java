package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public abstract class LevelChunkFix1 {
    @Shadow public abstract Level getLevel();

    boolean K_multi_threading$isChunkLocked;

    @Inject(method = "postProcessGeneration",at = @At("HEAD"))
    private void push(CallbackInfo ci) {
        if (getLevel().getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig) {
            K_multi_threading$isChunkLocked = iWorldChunkLockedConfig.pushThread() > 0;
        }
    }
    @Inject(method = "postProcessGeneration",at = @At("RETURN"))
    private void pop(CallbackInfo ci) {
        if (K_multi_threading$isChunkLocked && getLevel().getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig) {
            iWorldChunkLockedConfig.pop();
        }
    }
}
