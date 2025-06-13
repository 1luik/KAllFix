package n1luik.K_multi_threading.core.mixin.impl;

import com.mojang.datafixers.DataFixer;
import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.Imixin.GetBlockTickSync;
import n1luik.K_multi_threading.core.Imixin.ITickNode;
import n1luik.K_multi_threading.core.base.CalculateTask;
import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import n1luik.K_multi_threading.core.sync.GetterSyncNode;
import n1luik.K_multi_threading.core.sync.Sync;
import n1luik.K_multi_threading.core.sync.SyncImpl;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.ticks.LevelTicks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelImpl implements GetBlockTickSync {
    @Shadow @Final private LevelTicks<Block> blockTicks;
    @Shadow @Final private LevelTicks<Fluid> fluidTicks;
    @Shadow @Final EntityTickList entityTickList;

    @Shadow public abstract <T extends Entity> List<? extends T> getEntities(EntityTypeTest<Entity, T> p_143281_, Predicate<? super T> p_143282_);

    //@Mutable
    //@Shadow @Final private ServerChunkCache chunkSource;
    private Sync<GetterSyncNode<Void, ChunkPos, ChunkAccess>> sync;
    //static volatile int min;//防止同时访问爆炸


    //@Redirect(method = "<init>", at = @At(value = "NEW", target = "net/minecraft/server/level/ServerChunkCache"))
    //public ServerChunkCache toType1(ServerLevel p_214982_, LevelStorageSource.LevelStorageAccess p_214983_, DataFixer p_214984_, StructureTemplateManager p_214985_, Executor p_214986_, ChunkGenerator p_214987_, int p_214988_, int p_214989_, boolean p_214990_, ChunkProgressListener p_214991_, ChunkStatusUpdateListener p_214992_, Supplier p_214993_){
    //    return new ParaServerChunkProvider(p_214982_, p_214983_, p_214984_, p_214985_, p_214986_, p_214987_, p_214988_, p_214989_, p_214990_, p_214991_, p_214992_, p_214993_);
    //}

    //@Redirect(method = "<init>", at = @At("RETURN"))
    //public void initParaServerChunkProvider(MinecraftServer p_214999_, Executor p_215000_, LevelStorageSource.LevelStorageAccess p_215001_, ServerLevelData p_215002_, ResourceKey p_215003_, LevelStem p_215004_, ChunkProgressListener p_215005_, boolean p_215006_, long p_215007_, List p_215008_, boolean p_215009_, RandomSequences p_288977_, CallbackInfo ci){
    //    ServerChunkCache chunkSource1 = chunkSource;
    //    ParaServerChunkProvider clone;
    //    try {
    //        clone = ParaServerChunkProvider.UnsafeClone.clone(chunkSource1);
    //    } catch (InstantiationException e) {
    //        throw new RuntimeException(e);
    //    }
    //    chunkSource = clone;
    //    clone.UnsafeInit();
    //}

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(MinecraftServer p_214999_, Executor p_215000_, LevelStorageSource.LevelStorageAccess p_215001_, ServerLevelData p_215002_, ResourceKey p_215003_, LevelStem p_215004_, ChunkProgressListener p_215005_, boolean p_215006_, long p_215007_, List p_215008_, boolean p_215009_, RandomSequences p_288977_, CallbackInfo ci){
        sync = new SyncImpl<>(Base.getEx());
        ((GetBlockTickSync)blockTicks).setBiockTickSync(sync);
        ((GetBlockTickSync)fluidTicks).setBiockTickSync(sync);
    }

    @Override
    public Sync<GetterSyncNode<Void, ChunkPos, ChunkAccess>> getBlockTickSync() {
        return sync;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE",target = "Lnet/minecraft/world/level/entity/EntityTickList;forEach(Ljava/util/function/Consumer;)V"))
    public void tickEntity1(EntityTickList tickList, Consumer<Entity> consumer){
        //ContainsMapList<Entity> entityContainsMapList = new ContainsMapList<>();


        Base.ForkJoinPool_ ex = Base.getEx();

        //Base.WaitInt waitInt = new Base.WaitInt();
        List<Entity> buffList1 = new ArrayList<>(); 
        entityTickList.forEach(buffList1::add);
        Entity[] entities = buffList1.toArray(new Entity[0]);

        //ex.getDataMap().put(Base.thisRunTaskName,new Object[]{ServerLevelBlockEntityTickImpl1_2_3.class,1});


        //Base.LOGGER.info("tickEntity1 {}",entities.length);
        if (entities.length >= 2) {
            CalculateTask submit = (new CalculateTask(()->"TickEntities", 0, entities.length, (i) -> {

                //blockTickSync._run(c-> {

                    if (entities.length > i) {
                        Entity e = entities[i];
                        if (e instanceof ITickNode iTickNode && !iTickNode.K_multi_threading$hasTick())return;
                        consumer.accept(e);
                    }

                    //synchronized (waitInt) {
                    //    waitInt.size--;
                    //}
                    //return null;
                //}, new ChunkTickSyncNode(null,null),(Base.ForkJoinWorkerThread_)Thread.currentThread());
            }));

            submit.call(ex);
        }else if(entities.length > 0) {
            //CompletableFuture.supplyAsync(() -> {//防止出现兼容性问题
            //    blockTickSync._run(c-> {

                    Entity e = entities[0];

                    if (e == null) {
                        //Base.LOGGER.debug("实体tick错误: null");
                    }else {
                        consumer.accept(e);
                    }

                    //synchronized (waitInt) {
                    //    waitInt.size--;
                    //}
            //        return null;
            //    }, new ChunkTickSyncNode(null,null),(Base.ForkJoinWorkerThread_)Thread.currentThread());
            //    return null;
            //}, ex).join();

        }



        //ex.getDataMap().put(Base.thisRunTaskName, null);

    }


}
