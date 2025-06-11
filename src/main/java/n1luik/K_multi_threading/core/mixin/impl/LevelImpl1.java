package n1luik.K_multi_threading.core.mixin.impl;

import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.base.CalculateTask;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(Level.class)
public abstract class LevelImpl1 {
    //Base.WaitInt waitInt;
    @Unique
    List<TickingBlockEntity> K_multi_threading$taskBuff;

    //@Shadow public abstract ProfilerFiller getProfiler();

    //@Shadow @Final private ArrayList<BlockEntity> pendingFreshBlockEntities;
    //@Shadow @Final private ArrayList<BlockEntity> freshBlockEntities;
    //@Shadow private boolean tickingBlockEntities;
    //@Shadow @Final private List<TickingBlockEntity> pendingBlockEntityTickers;
    //@Mutable
    //@Shadow @Final protected List<TickingBlockEntity> blockEntityTickers;

    //@Shadow public abstract boolean shouldTickBlocksAt(BlockPos p_220394_);

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CallbackInfo ci){
        //waitInt = new Base.WaitInt();
        K_multi_threading$taskBuff = new ArrayList<>(3000);
        //blockEntityTickers = new ContainsMapList<>();
    }

    //static volatile int min;//防止同时访问爆炸



    //提高兼容
    @Redirect(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V"))
    public void addTask(TickingBlockEntity tickingBlockEntity){
        K_multi_threading$taskBuff.add(tickingBlockEntity);
    }
    
    @Inject(method = "tickBlockEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
    public void callTask(CallbackInfo ci){;
        //waitInt.size = tickingBlockEntities1.length;

        //Sync<GetterSyncNode<Void, ChunkPos, ChunkAccess>> blockTickSync = ((GetBlockTickSync)this).getBlockTickSync();
        TickingBlockEntity[] tickingBlockEntities1 = K_multi_threading$taskBuff.toArray(new TickingBlockEntity[0]);
        K_multi_threading$taskBuff.clear();
        if (tickingBlockEntities1.length >= 2) {
            CalculateTask submit = (new CalculateTask(()->"TickBlockEntities", 0, tickingBlockEntities1.length, (i) -> {

                //blockTickSync._run(c -> {
                if (tickingBlockEntities1.length > i){
                    TickingBlockEntity tickingBlockEntity = tickingBlockEntities1[i];
                    tickingBlockEntity.tick();
                }

                //synchronized (waitInt) {
                //    waitInt.size--;
                //}
                //    return null;
                //}, new ChunkTickSyncNode(null, null), (Base.ForkJoinWorkerThread_) Thread.currentThread());
                //return null;
            }));

            submit.call(Base.getEx());
        }else if(tickingBlockEntities1.length > 0) {
            //CompletableFuture.supplyAsync(() -> {//防止出现兼容性问题
                //blockTickSync._run(c -> {

                tickingBlockEntities1[0].tick();

                //synchronized (waitInt) {
                //    waitInt.size--;
                //}
                //    return null;
                //}, new ChunkTickSyncNode(null, null), (Base.ForkJoinWorkerThread_) Thread.currentThread());
            //    return null;
            //}, Base.getEx()).join();

        }
    }


    ///**
    // * @author ++
    // * @reason ++
    // */
    //@Overwrite
    //protected void tickBlockEntities(){
    //    ProfilerFiller profilerfiller = this.getProfiler();
    //    profilerfiller.push("blockEntities");
    //    if (!this.pendingFreshBlockEntities.isEmpty()) {
    //        this.freshBlockEntities.addAll(this.pendingFreshBlockEntities);
    //        this.pendingFreshBlockEntities.clear();
    //    }
    //    this.tickingBlockEntities = true;
    //    if (!this.freshBlockEntities.isEmpty()) {
    //        this.freshBlockEntities.forEach(BlockEntity::onLoad);
    //        this.freshBlockEntities.clear();
    //    }
    //    if (!this.pendingBlockEntityTickers.isEmpty()) {
    //        this.blockEntityTickers.addAll(this.pendingBlockEntityTickers);
    //        this.pendingBlockEntityTickers.clear();
    //    }
//
    //    this.blockEntityTickers.removeIf(blockEntity -> blockEntity.isRemoved() || !this.shouldTickBlocksAt(blockEntity.getPos()));
    //    //TickingBlockEntity[] nodeList = blockEntityTickers.toArray(TickingBlockEntity[]::new);
//
    //    //Base.getEx().getDataMap().put(Base.thisRunTaskName,new Object[]{LevelTickImpl4Mixin .class,1});
    //    waitInt.size = blockEntityTickers.size();
//
    //    //Sync<GetterSyncNode<Void, ChunkPos, ChunkAccess>> blockTickSync = ((GetBlockTickSync)this).getBlockTickSync();
    //    if (blockEntityTickers.size() >= 2) {
    //        CalculateTask<List<Object>> submit = (new CalculateTask<>(0, blockEntityTickers.size()+1, (i) -> {
//
    //            //blockTickSync._run(c -> {
    //                if (blockEntityTickers.size() > i){
    //                    blockEntityTickers.get(i).tick();
    //                }
//
    //                synchronized (waitInt) {
    //                    waitInt.size--;
    //                }
    //            //    return null;
    //            //}, new ChunkTickSyncNode(null, null), (Base.ForkJoinWorkerThread_) Thread.currentThread());
    //            return null;
    //        }));
//
    //        Base.getEx().submit(submit);submit.waitThread();
    //    }else if(blockEntityTickers.size() > 0) {
    //        CompletableFuture.supplyAsync(() -> {//防止出现兼容性问题
    //            //blockTickSync._run(c -> {
//
    //                blockEntityTickers.get(0).tick();
//
    //                synchronized (waitInt) {
    //                    waitInt.size--;
    //                }
    //            //    return null;
    //            //}, new ChunkTickSyncNode(null, null), (Base.ForkJoinWorkerThread_) Thread.currentThread());
    //            return null;
    //        }, Base.getEx()).join();
//
    //    }
//
//
    //    //ex.getDataMap().put(Base.thisRunTaskName, null);
//
//
    //    profilerfiller.pop();
    //    this.tickingBlockEntities = false;
    //}
//
}
