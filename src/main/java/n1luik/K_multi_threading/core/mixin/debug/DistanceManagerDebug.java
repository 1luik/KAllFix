package n1luik.K_multi_threading.core.mixin.debug;

import com.mojang.datafixers.util.Either;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;

@Deprecated
@Mixin(DistanceManager.class)
public class DistanceManagerDebug {

    @Inject(method = "runAllUpdates", at = @At(value = "INVOKE",target = "Lnet/minecraft/server/level/DistanceManager$ChunkTicketTracker;runDistanceUpdates(I)I"))
    public void debug1(ChunkMap p_140806_, CallbackInfoReturnable<Boolean> cir) {
        Base.LOGGER.info("DistanceManager.class 1");
    }

    @Inject(method = "runAllUpdates", at = @At(value = "INVOKE",target = "Ljava/util/Set;forEach(Ljava/util/function/Consumer;)V"))
    public void debug2(ChunkMap p_140806_, CallbackInfoReturnable<Boolean> cir) {
        Base.LOGGER.info("DistanceManager.class 2");
    }

    @Inject(method = "runAllUpdates", at = @At(value = "INVOKE",target = "Lnet/minecraft/server/level/ChunkMap;getUpdatingChunkIfPresent(J)Lnet/minecraft/server/level/ChunkHolder;"))
    public void debug3(ChunkMap p_140806_, CallbackInfoReturnable<Boolean> cir) {
        Base.LOGGER.info("DistanceManager.class 3");
    }

}
