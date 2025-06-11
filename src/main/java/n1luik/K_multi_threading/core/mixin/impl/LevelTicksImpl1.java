package n1luik.K_multi_threading.core.mixin.impl;

import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.Imixin.GetBlockTickSync;
import n1luik.K_multi_threading.core.sync.GetterSyncNode;
import n1luik.K_multi_threading.core.sync.Sync;
import n1luik.K_multi_threading.core.base.CalculateTask;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.ScheduledTick;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.function.BiConsumer;

@Mixin(LevelTicks.class)
public abstract class LevelTicksImpl1<T> implements LevelTickAccess<T>, GetBlockTickSync {
    @Shadow @Final private Queue<ScheduledTick<T>> toRunThisTick;
    @Shadow @Final private Set<ScheduledTick<?>> toRunThisTickSet;
    @Shadow @Final private List<ScheduledTick<T>> alreadyRunThisTick;

    @Shadow public abstract void addContainer(ChunkPos p_193232_, LevelChunkTicks<T> p_193233_);

    //static volatile int min;//防止同时访问爆炸

    private Sync<GetterSyncNode<Void, ChunkPos, ChunkAccess>> sync;
    /**
     * @author ++
     * @reason ++
     */
    @Inject(method = "runCollectedTicks",at = @At("HEAD"),cancellable = true)//@Overwrite
    private void runCollectedTicks(BiConsumer<BlockPos, T> p_193273_, CallbackInfo ci){
        ci.cancel();
        /////////////
        List<ScheduledTick<T>> ticks = new ArrayList<>();//Map<Long,ContainsMapList<ScheduledTick<T>>> objects = new HashMap<>();

        while(!this.toRunThisTick.isEmpty()) {
            ScheduledTick<T> scheduledtick = this.toRunThisTick.poll();
            if (!this.toRunThisTickSet.isEmpty()) {
                this.toRunThisTickSet.remove(scheduledtick);
            }

            this.alreadyRunThisTick.add(scheduledtick);

            ticks.add(scheduledtick);
        }

        Base.ForkJoinPool_ ex = Base.getEx();

        //Base.WaitInt waitInt = new Base.WaitInt();

        ScheduledTick<T>[] tickArray = ticks.toArray(new ScheduledTick[0]);

        //ex.getDataMap().put(Base.thisRunTaskName,new Object[]{LevelTickImpl.class,1});
        //waitInt.size = tickArray.length;


        //Base.LOGGER.info("runCollectedTicks {}",tickArray.length);
        if (tickArray.length >= 2) {
            (new CalculateTask(()->"blockTicks", 0, tickArray.length, (i) -> {

                //blockTickSync._run(c -> {


                    if (tickArray.length > i){
                        ScheduledTick<T> scheduledticks = tickArray[i];
                        p_193273_.accept(scheduledticks.pos(), scheduledticks.type());
                    }
                    //synchronized (waitInt) {
                    //    waitInt.size--;
                    //}
                    //return null;
                //}, new ChunkTickSyncNode(null, null), (Base.ForkJoinWorkerThread_) Thread.currentThread());
                //return null;
            })).call(ex);
        }else if (tickArray.length > 0){

            //CompletableFuture.supplyAsync(() -> {
            //    blockTickSync._run(c -> {

                    ScheduledTick<T> scheduledticks = tickArray[0];
                    p_193273_.accept(scheduledticks.pos(), scheduledticks.type());
                    //synchronized (waitInt) {
                    //    waitInt.size--;
                    //}
            //        return null;
            //    }, new ChunkTickSyncNode(null, null), (Base.ForkJoinWorkerThread_) Thread.currentThread());
            //    return null;
            //}, ex).join();
        }

    }

    @Override
    public Sync<GetterSyncNode<Void, ChunkPos, ChunkAccess>> getBlockTickSync() {
        return sync;
    }

    @Override
    public void setBiockTickSync(Sync<GetterSyncNode<Void, ChunkPos, ChunkAccess>> set) {
        sync = set;
    }
}
