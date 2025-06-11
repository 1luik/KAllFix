package n1luik.K_multi_threading.core.mixin.debug;

import com.mojang.datafixers.util.Either;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;

@Deprecated
@Mixin(ServerChunkCache.class)
public class ServerChunkCacheDebug {

    //@Inject(method = "getChunk", at = @At("HEAD"))
    //public void debug1(int p_8360_, int p_8361_, ChunkStatus p_8362_, boolean p_8363_, CallbackInfoReturnable<ChunkAccess> cir){
    //    Base.LOGGER.info("ServerChunkCache.class {} {} {}",p_8360_,p_8361_, ChunkLevel.byStatus(p_8362_));
    //}
//
    //@Inject(method = "getChunkFutureMainThread", at = @At("RETURN"),locals = LocalCapture.CAPTURE_FAILHARD)
    //public void debug2(int p_8457_, int p_8458_, ChunkStatus p_8459_, boolean p_8460_, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir, ChunkPos chunkpos, long i, int j, ChunkHolder chunkholder){
    //    Base.LOGGER.info("ServerChunkCache.class2 {} {} {}",chunkholder == null, chunkholder != null && chunkholder.getTicketLevel() > j,j);
    //}

    @Shadow @Final public ServerChunkCache.MainThreadExecutor mainThreadProcessor;

    @Shadow @Final public ChunkMap chunkMap;

    @Inject(method = "getChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache$MainThreadExecutor;managedBlock(Ljava/util/function/BooleanSupplier;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void debug1(int p_8360_, int p_8361_, ChunkStatus p_8362_, boolean p_8363_, CallbackInfoReturnable<ChunkAccess> cir, ProfilerFiller profilerfiller, long i, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture){
        Object o;
        ChunkHolder visibleChunkIfPresent = chunkMap.getVisibleChunkIfPresent(ChunkPos.asLong(p_8360_, p_8361_));
        if (visibleChunkIfPresent != null) {
            CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> eitherCompletableFuture = visibleChunkIfPresent.futures.get(p_8362_.getIndex());
            if (eitherCompletableFuture == null) {
                o = "h_null";
            }else if (eitherCompletableFuture == completablefuture) {
                if (eitherCompletableFuture.isDone()) {
                    o = "is_d";
                }else if (eitherCompletableFuture.getNow(null) == null){
                    o = "is_nd_n";
                }else {
                    o = "is_de_v";
                }
            }else {

                if (eitherCompletableFuture.isDone()) {
                    o = "d";
                }else if (eitherCompletableFuture.getNow(null) == null){
                    o = "nd_n";
                }else {
                    o = "de_v";
                }
            }
        }else {
            o = "null";
        }
        Base.LOGGER.info("ServerChunkCache.class3 等待 {} {} {} futures {}", p_8362_.getIndex(), p_8360_, p_8361_, o);
        mainThreadProcessor.managedBlock(() -> {
            return completablefuture.isDone();
        });
    }
}
