package n1luik.K_multi_threading.core.mixin.minecraftfix;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import n1luik.K_multi_threading.core.util.Unsafe;
import net.minecraft.server.level.*;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheFix1 {
    @Shadow @Nullable public abstract ChunkAccess getChunk(int p_8360_, int p_8361_, ChunkStatus p_8362_, boolean p_8363_);

    @Shadow @Final
    Thread mainThread;
    //IMainThreadExecutor iMainThreadExecutor;

    @Shadow protected abstract CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFutureMainThread(int p_8457_, int p_8458_, ChunkStatus p_8459_, boolean p_8460_);

    @Shadow @Nullable protected abstract ChunkHolder getVisibleChunkIfPresent(long p_8365_);

    @Shadow @Final private DistanceManager distanceManager;

    @Shadow @Final public ChunkMap chunkMap;

    @Shadow @Final public ServerChunkCache.MainThreadExecutor mainThreadProcessor;

    @Redirect(method = "getChunk", at = @At(value = "INVOKE",target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
    public Thread fix1() {
        return mainThread;
    }

    @Redirect(method = "getChunkFuture", at = @At(value = "INVOKE",target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
    public Thread fix2() {
        return mainThread;
    }

    @Redirect(method = "getChunkNow", at = @At(value = "INVOKE",target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
    public Thread fix3() {
        return mainThread;
    }

    //@Redirect(method = "pollTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache$MainThreadExecutor;pollTask()Z"))
    //public boolean fix4(ServerChunkCache.MainThreadExecutor instance) {
    //    iMainThreadExecutor.pushThread();
    //    return iMainThreadExecutor.notCallPollTask();
    //}

    @Inject(method = "tick", at = @At("HEAD"))
    public void fix4(BooleanSupplier p_201913_, boolean p_201914_, CallbackInfo ci) {
        //iMainThreadExecutor.setM2(true);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(ServerLevel p_214982_, LevelStorageSource.LevelStorageAccess p_214983_, DataFixer p_214984_, StructureTemplateManager p_214985_, Executor p_214986_, ChunkGenerator p_214987_, int p_214988_, int p_214989_, boolean p_214990_, ChunkProgressListener p_214991_, ChunkStatusUpdateListener p_214992_, Supplier p_214993_, CallbackInfo ci){
        //iMainThreadExecutor = (IMainThreadExecutor) mainThreadProcessor;
        //try {
        //    Unsafe.setfinal(ServerLevel.class.getDeclaredField("f_8547_"), p_214982_, this);
        //} catch (NoSuchFieldException e) {
        //    throw new RuntimeException(e);
        //}
    }

    @Inject(method = "runDistanceManagerUpdates", at = @At(value = "INVOKE",target = "Lnet/minecraft/server/level/ChunkMap;promoteChunkMap()Z"))
    public void fix5(CallbackInfoReturnable<Boolean> cir) {
        if (((Object)this) instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig){
            iWorldChunkLockedConfig.execTasks();
        }
    }
    //@Redirect(method = "runDistanceManagerUpdates", at = @At(value = "INVOKE",target = "Lnet/minecraft/server/level/DistanceManager;runAllUpdates(Lnet/minecraft/server/level/ChunkMap;)Z"))
    //public boolean fix6(DistanceManager instance, ChunkMap completablefuture) {
    //    synchronized (completablefuture){
    //        return instance.runAllUpdates(completablefuture);
    //    }
    //}

    //@Redirect(method = "getChunk", at = @At(value = "INVOKE",target = "Lnet/minecraft/server/level/ServerChunkCache$MainThreadExecutor;managedBlock(Ljava/util/function/BooleanSupplier;)V"))
    //public void fix4(ServerChunkCache.MainThreadExecutor instance, BooleanSupplier booleanSupplier) {
    //    //instance.managedBlock(booleanSupplier);
    //}

    //@Redirect(method = "getChunk", at = @At(value = "INVOKE",target = "Lnet/minecraft/server/level/ServerChunkCache;getChunkFutureMainThread(IILnet/minecraft/world/level/chunk/ChunkStatus;Z)Ljava/util/concurrent/CompletableFuture;"))
    //public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> fix1(ServerChunkCache _this,int x, int z, ChunkStatus status, boolean generate) {
    //    if (generate){
    //        ChunkPos chunkpos = new ChunkPos(x, z);
    //        long j = chunkpos.toLong();
    //        int i = ChunkLevel.byStatus(status);
    //        ChunkHolder chunkholder = getVisibleChunkIfPresent(j);
    //        if(chunkholder != null){
    //            CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> eitherCompletableFuture = chunkholder.futures.get(status.getIndex());
    //            synchronized (distanceManager){
    //                distanceManager.addTicket(TicketType.UNKNOWN, chunkpos, i, chunkpos);
    //            }
    //            if (eitherCompletableFuture != null) {
    //                Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> either = eitherCompletableFuture.getNow(ChunkHolder.NOT_DONE_YET);
    //                if (either == null) {
    //                    String s = "value in future for status: " + status + " was incorrectly set to null at chunk: " + chunkholder.getPos();
    //                    throw chunkMap.debugFuturesAndCreateReportedException(new IllegalStateException("null value previously set for chunk status"), s);
    //                }
//
    //                if (either == ChunkHolder.NOT_DONE_YET || either.right().isEmpty()) {
    //                    return eitherCompletableFuture;
    //                }
    //            }
    //            return chunkholder.getOrScheduleFuture(status, chunkMap);
    //        }
    //    }
    //    return getChunkFutureMainThread(x, z, status, generate);
    //}

}

