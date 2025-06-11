package n1luik.K_multi_threading.core.mixin.minecraftfix;

import com.mojang.datafixers.util.Either;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

@Mixin(ChunkHolder.class)
@Deprecated
public abstract class ChunkHolderFix1_Debug {
    @Shadow @Final private static List<ChunkStatus> CHUNK_STATUSES;
    @Shadow @Final public AtomicReferenceArray<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> futures;

    @Shadow public abstract ChunkPos getPos();

    @Unique
    public final boolean[] K_multi_threading$startDebug = new boolean[CHUNK_STATUSES.size()];

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(ChunkPos p_142986_, int p_142987_, LevelHeightAccessor p_142988_, LevelLightEngine p_142989_, ChunkHolder.LevelChangeListener p_142990_, ChunkHolder.PlayerProvider p_142991_, CallbackInfo ci){
        Arrays.fill(K_multi_threading$startDebug, false);
    }

    @Inject(method = "getOrScheduleFuture", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/atomic/AtomicReferenceArray;get(I)Ljava/lang/Object;"))
    public void fix1(ChunkStatus p_140050_, ChunkMap p_140051_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir) {
        int index = p_140050_.getIndex();
        if (K_multi_threading$startDebug[index]) {
            if (this.futures.get(index) == null) {
                Base.LOGGER.debug("ChunkHolder.class1", new Exception());//, new Exception());
            }
        }else {
            K_multi_threading$startDebug[index] = true;
        }
    }
    @Inject(method = "getOrScheduleFuture", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;schedule(Lnet/minecraft/server/level/ChunkHolder;Lnet/minecraft/world/level/chunk/ChunkStatus;)Ljava/util/concurrent/CompletableFuture;"))
    public void debug1(ChunkStatus p_140050_, ChunkMap p_140051_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir) {
        Base.LOGGER.debug("ChunkHolder.class2 {} {} {}", getPos().x, getPos().z, p_140050_.getIndex());//, new Exception());
    }


}
