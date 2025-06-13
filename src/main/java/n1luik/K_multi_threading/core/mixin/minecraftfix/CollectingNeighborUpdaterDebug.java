package n1luik.K_multi_threading.core.mixin.minecraftfix;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.redstone.CollectingNeighborUpdater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CollectingNeighborUpdater.class)
public class CollectingNeighborUpdaterDebug {
    @Inject(method = "addAndRun",at = @At("HEAD"))
    public void debug1(BlockPos p_230661_, CollectingNeighborUpdater.NeighborUpdates p_230662_, CallbackInfo ci){
        if (p_230661_ == null) {
            throw new RuntimeException();
        }
    }
}
