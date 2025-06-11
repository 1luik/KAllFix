package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ThreadedLevelLightEngine.class, priority = Integer.MAX_VALUE-1)
public abstract class ThreadedLevelLightEngineFix2 extends LevelLightEngine {
    @Shadow @Final private ChunkMap chunkMap;
    @Shadow @Final private static Logger LOGGER;
    //@Unique
    //public final ConcurrentLinkedQueue<VOB2<Runnable, Object>> K_multi_threading$fixTask1 = new ConcurrentLinkedQueue<>();
    //@Unique
    //private ReentrantLock K_multi_threading$lock;
    //@Unique
    //private Condition K_multi_threading$condition;

    public ThreadedLevelLightEngineFix2(LightChunkGetter p_75805_, boolean p_75806_, boolean p_75807_) {
        super(p_75805_, p_75806_, p_75807_);
    }

    //@Inject(method = "<init>", at = @At("RETURN"))
    //public void init(LightChunkGetter p_9305_, ChunkMap p_9306_, boolean p_9307_, ProcessorMailbox p_9308_, ProcessorHandle p_9309_, CallbackInfo ci){
    //    K_multi_threading$lock = new ReentrantLock();
    //    K_multi_threading$condition = K_multi_threading$lock.newCondition();
    //}

    //@Redirect(method = "tryScheduleUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/thread/ProcessorMailbox;tell(Ljava/lang/Object;)V"))
    //public <T> void fix2(ProcessorMailbox<T> instance, T p_18750_){
    //    if (chunkMap.level.getChunkSource() instanceof ParaServerChunkProvider paraServerChunkProvider && paraServerChunkProvider.isGeneratorWait()) {
    //        instance.tell((T)(Runnable)()->{
    //            boolean b = paraServerChunkProvider.pushThread() > 0;
    //            ((Runnable)p_18750_).run();
    //            if (b) {
    //                paraServerChunkProvider.pop();
    //            }
    //        });
    //    }else {
    //        instance.tell(p_18750_);
    //    }
//
    //}

    @Inject(method = "runUpdate", at = @At("HEAD"))
    public void fix1(CallbackInfo ci){
        if (chunkMap.level.getChunkSource() instanceof ParaServerChunkProvider paraServerChunkProvider) {
            paraServerChunkProvider.lightChunkThread();
        }

    }
    @Inject(method = "runUpdate", at = @At("RETURN"))
    public void fix2(CallbackInfo ci){
        if (chunkMap.level.getChunkSource() instanceof ParaServerChunkProvider paraServerChunkProvider) {
            paraServerChunkProvider.lightChunkThreadEnd();
        }

    }
    //@Redirect(method = "runUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/lighting/LevelLightEngine;runLightUpdates()I", opcode = Opcodes.INVOKESPECIAL))
    //public int fix1(LevelLightEngine instance){
    //    //if (chunkMap.level.getChunkSource() instanceof ParaServerChunkProvider paraServerChunkProvider && paraServerChunkProvider.isGeneratorWait()) {
    //        AsyncWait<Integer> integerAsyncWait = new AsyncWait<>(K_multi_threading$lock, K_multi_threading$condition, super::runLightUpdates);
    //        Base.ForkJoinPool_ ex = Base.getEx();
    //        ex.submit(integerAsyncWait);
    //        integerAsyncWait.waitTask(()->{
    //            while (!K_multi_threading$fixTask1.isEmpty()){
    //                try{
    //                    //.run();
    //                    ex.submit(K_multi_threading$fixTask1.remove().getT1());
    //                }catch (Throwable e){
    //                    LOGGER.error("Error while running task", e);
    //                }
    //            }
    //        });
    //        return integerAsyncWait.getReturn_();
    //    //}else {
    //    //    return super.runLightUpdates();
    //    //}
    //}
    //@Redirect(method = "lightChunk", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    //public <U> CompletableFuture<U> fix3(Supplier<U> supplier, Executor executor){
//
    //    return CompletableFuture.supplyAsync(supplier, t->{
    //        VOB2<Runnable, Object> ob2 = new VOB2<>(null, null);
//
    //        Runnable t2 = ()->{
    //            boolean r;
    //            synchronized (ob2) {
    //                if (ob2.getT2_() == null) {
    //                    r = true;
    //                    ob2.setT2("");
    //                }else {
    //                    r = false;
    //                }
    //            }
    //            if (r) {
    //                t.run();
    //            }
    //        };
    //        ob2.setT1_(t2);
    //        K_multi_threading$fixTask1.add(ob2);
    //        executor.execute(t2);
    //    });
    //}
}
