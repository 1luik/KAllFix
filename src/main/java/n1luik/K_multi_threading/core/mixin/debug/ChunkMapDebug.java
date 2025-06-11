package n1luik.K_multi_threading.core.mixin.debug;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;

@Deprecated
@Mixin(value = ChunkMap.class, priority = 1111)
public class ChunkMapDebug {

    //@Shadow private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> visibleChunkMap;
//
    //@Mutable
    //@Shadow @Final private Long2ObjectLinkedOpenHashMap<ChunkHolder> updatingChunkMap;
//
    //@Mutable
    //@Shadow @Final private Long2ObjectLinkedOpenHashMap<ChunkHolder> pendingUnloads;
//
    //@Inject(method = "updateChunkScheduling",at = @At("RETURN"))
    //public void debug1(long p_140177_, int p_140178_, ChunkHolder p_140179_, int p_140180_, CallbackInfoReturnable<ChunkHolder> cir){
    //    for (Map.Entry<Long, ChunkHolder> longChunkHolderEntry : updatingChunkMap.entrySet()) {
    //        Base.LOGGER.info("ChunkMapDebug1 {} {}",longChunkHolderEntry.getKey(),longChunkHolderEntry.getValue().getTicketLevel());
    //    }
    //}
//
    //@Inject(method = "schedule",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/level/progress/ChunkProgressListener;onStatusChange(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/ChunkStatus;)V"))
    //public void debug2(ChunkHolder p_140293_, ChunkStatus p_140294_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir){
    //    Base.LOGGER.info("ChunkMapDebug2");
    //}
//
    //@Inject(method = "getChunkRangeFuture",at = @At(value = "INVOKE",target = "Ljava/util/function/IntFunction;apply(I)Ljava/lang/Object;",ordinal = 0))
    //public void debug3(ChunkHolder p_281446_, int p_282030_, IntFunction<ChunkStatus> p_282923_, CallbackInfoReturnable<CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>>> cir){
    //    Base.LOGGER.info("ChunkMapDebug3");
    //}
//
    //@Inject(method = "getChunkRangeFuture",at = @At(value = "INVOKE",target = "Ljava/util/function/IntFunction;apply(I)Ljava/lang/Object;",ordinal = 1))
    //public void debug4(ChunkHolder p_281446_, int p_282030_, IntFunction<ChunkStatus> p_282923_, CallbackInfoReturnable<CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>>> cir){
    //    Base.LOGGER.info("ChunkMapDebug4");
    //}
//
    //@Inject(method = "getChunkRangeFuture",at = @At(value = "INVOKE",target = "Ljava/util/concurrent/CompletableFuture;completedFuture(Ljava/lang/Object;)Ljava/util/concurrent/CompletableFuture;"),locals = LocalCapture.CAPTURE_FAILHARD)
    //public void debug5(ChunkHolder p_281446_, int p_282030_, IntFunction<ChunkStatus> p_282923_, CallbackInfoReturnable<CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>>> cir, List list, List list1, ChunkPos chunkpos, int i, int j, int k, int l, int i1, ChunkPos chunkpos1, long j1, ChunkHolder chunkholder){
    //    Base.LOGGER.info("ChunkMapDebug5 {} {} {}",j1,chunkpos1,p_282030_);
    //}


    @Inject(method = "readChunk",at = @At("HEAD"))
    public void debug5(ChunkPos p_214964_, CallbackInfoReturnable<CompletableFuture<Optional<CompoundTag>>> cir){
        Base.LOGGER.debug("ChunkMapDebug6 {} {} ", p_214964_.x, p_214964_.z);//, new Exception());
    }

    //lambda$scheduleChunkLoad$18
    @Inject(method = "m_214923_",at = @At("HEAD"), remap = false)
    private static void debug6(ChunkPos p_140418_, Optional p_214925_, CallbackInfoReturnable<Optional> cir){
        Base.LOGGER.debug("ChunkMapDebug7 {} {} ", p_140418_.x, p_140418_.z);//, new Exception());
    }
    //lambda$scheduleChunkLoad$19
    @Inject(method = "m_288112_",at = @At("RETURN"), remap = false)
    private void debug7(ChunkPos p_140418_, Optional p_269770_, CallbackInfoReturnable<Either> cir){
        Base.LOGGER.debug("ChunkMapDebug8 {} {} ", p_140418_.x, p_140418_.z);//, new Exception());
    }
    @Inject(method = "protoChunkToFullChunk", at = @At("HEAD"))
    private void debug8(ChunkHolder p_140384_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir){
        Base.LOGGER.debug("ChunkMapDebug9 {} {} ", p_140384_.getPos().x, p_140384_.getPos().z);//, new Exception());
    }
    //@Inject(method = "schedule", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/progress/ChunkProgressListener;onStatusChange(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/ChunkStatus;)V"))
    //private void debug8(ChunkHolder p_140293_, ChunkStatus p_140294_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir){
    //    Base.LOGGER.debug("ChunkMapDebug10 {} {} {}", p_140293_.getPos().x, p_140293_.getPos().z, p_140294_.getIndex());//, new Exception());
    //}
    //@Inject(method = "schedule", at = @At(value = "RETURN", ordinal = 2))
    //private void debug9(ChunkHolder p_140293_, ChunkStatus p_140294_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir){
    //    Base.LOGGER.debug("ChunkMapDebug11 {} {} {}", p_140293_.getPos().x, p_140293_.getPos().z, p_140294_.getIndex());//, new Exception());
    //}
    //@Inject(method = "schedule", at = @At(value = "RETURN", ordinal = 1))
    //private void debug10(ChunkHolder p_140293_, ChunkStatus p_140294_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir){
    //    Base.LOGGER.debug("ChunkMapDebug12 {} {} {}", p_140293_.getPos().x, p_140293_.getPos().z, p_140294_.getIndex());//, new Exception());
    //}
    @Inject(method = "schedule", at = @At("HEAD"))
    private void debug11(ChunkHolder p_140293_, ChunkStatus p_140294_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir){
        Base.LOGGER.debug("ChunkMapDebug13 {} {} {}", p_140293_.getPos().x, p_140293_.getPos().z, p_140294_.getIndex());//, new Exception());
    }
}
