package n1luik.K_multi_threading.core.mixin.minecraftfix;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
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

@Mixin(ChunkHolder.class)
@Deprecated
public class ChunkHolderFix1 {
    @Shadow @Final private static List<ChunkStatus> CHUNK_STATUSES;
    @Shadow @Final public AtomicReferenceArray<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> futures;
    @Unique
    public final boolean[] K_multi_threading$startLock = new boolean[CHUNK_STATUSES.size()];

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(ChunkPos p_142986_, int p_142987_, LevelHeightAccessor p_142988_, LevelLightEngine p_142989_, ChunkHolder.LevelChangeListener p_142990_, ChunkHolder.PlayerProvider p_142991_, CallbackInfo ci){
        Arrays.fill(K_multi_threading$startLock, false);
    }

    @Inject(method = "getOrScheduleFuture", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/atomic/AtomicReferenceArray;get(I)Ljava/lang/Object;"))
    public void fix1(ChunkStatus p_140050_, ChunkMap p_140051_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir) {
        int index = p_140050_.getIndex();
        re:{
            while (K_multi_threading$getStartLock(index)) Thread.onSpinWait();
            if (!K_multi_threading$setStartLock(index, false, true)) {
                break re;
            }
        }
        if (this.futures.get(index) != null) {
            K_multi_threading$uStartLock(index);
        }
    }
    @Inject(method = "getOrScheduleFuture", at = @At(value = "RETURN", ordinal = 1))
    public void fix2(ChunkStatus p_140050_, ChunkMap p_140051_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir) {
        K_multi_threading$uStartLock(p_140050_.getIndex());
    }

    @Unique
    public void K_multi_threading$uStartLock(int i){
        re:{
            while (!K_multi_threading$getStartLock(i)) Thread.onSpinWait();
            if (!K_multi_threading$setStartLock(i, true, false)) {
                break re;
            }
        }
    }
    @Unique
    public boolean K_multi_threading$getStartLock(int i){
        synchronized (K_multi_threading$startLock) {
            return K_multi_threading$startLock[i];
        }
    }

    @Unique
    public boolean K_multi_threading$setStartLock(int i, boolean src, boolean b){
        synchronized (K_multi_threading$startLock) {
            if (K_multi_threading$startLock[i] == src) {
                K_multi_threading$startLock[i] = b;
                return true;
            }else {
                return false;
            }
        }
    }




}
