package n1luik.K_multi_threading.core.mixin.fix.destroy;

import com.petrolpark.destroy.events.DestroyCommonEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DestroyCommonEvents.class)
public class DestroyCommonEventsFix1 {
    @Inject(method = "playerTick", at = @At("HEAD"), cancellable = true, remap = false)
    private static void fix1(TickEvent.PlayerTickEvent event, CallbackInfo ci){
        BlockPos onPos = event.player.getOnPos();
        if (event.player.level().getChunkSource().getChunkNow(SectionPos.blockToSectionCoord(onPos.getX()), SectionPos.blockToSectionCoord(onPos.getZ())) == null){
            ci.cancel();
        }
    }
}
