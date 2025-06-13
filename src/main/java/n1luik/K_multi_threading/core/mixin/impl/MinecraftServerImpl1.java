package n1luik.K_multi_threading.core.mixin.impl;

import com.mojang.datafixers.DataFixer;
import lombok.Getter;
import lombok.Setter;
import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.Imixin.IMinecraftServerTickMixin1;
import n1luik.K_multi_threading.core.WorldError;
import n1luik.K_multi_threading.core.base.CalculateTask;
import n1luik.K_multi_threading.core.util.Util;
import n1luik.K_multi_threading.core.util.EventUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

@Mixin(MinecraftServer.class)
public class MinecraftServerImpl1 implements IMinecraftServerTickMixin1 {

    @Shadow private ProfilerFiller profiler;
    @Shadow private int tickCount;
    long K_multi_threading$startTime;
    @Getter
    @Setter
    int K_multi_threading$removeErrorSize = 50;
    List<List<Runnable>> K_multi_threading$taskList;
    List<Runnable> K_multi_threading$taskListBuff;
    //Field busID;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(Thread p_236723_, LevelStorageSource.LevelStorageAccess p_236724_, PackRepository p_236725_, WorldStem p_236726_, Proxy p_236727_, DataFixer p_236728_, Services p_236729_, ChunkProgressListenerFactory p_236730_, CallbackInfo ci) {
        Base.regThread("Server base",Thread.currentThread());
        Base.mcs = (MinecraftServer)(Object)this;
        K_multi_threading$taskList = new ArrayList<>();
        K_multi_threading$taskListBuff = new ArrayList<>();
    }

    @Inject(method = "runServer", at = @At("HEAD"))
    private void init(CallbackInfo ci) {
        Base.regThread("Server base",Thread.currentThread());
    }

    @Inject(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getWorldArray()[Lnet/minecraft/server/level/ServerLevel;", remap = false))
    private void addTime(CallbackInfo ci) {
        K_multi_threading$startTime = net.minecraft.Util.getNanos();
    }

    @Redirect(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onPreLevelTick(Lnet/minecraft/world/level/Level;Ljava/util/function/BooleanSupplier;)V", remap = false))
    public synchronized void forgeEvent1(Level level, BooleanSupplier haveTime) {
        //if (busID == null) {
        //    try {
        //        busID = EventBus.class.getDeclaredField("busID");
        //        busID.setAccessible(true);
        //    } catch (NoSuchFieldException e) {
        //        throw new RuntimeException(e);
        //    }
        //}

        assert level != null;

        K_multi_threading$taskListBuff.add(()->{
            TickEvent.LevelTickEvent levelTickEvent = new TickEvent.LevelTickEvent(level.isClientSide ? LogicalSide.CLIENT : LogicalSide.SERVER, TickEvent.Phase.START, level, haveTime);
            EventUtil.runEvent(Base.getEx(), MinecraftForge.EVENT_BUS, levelTickEvent);
        });
    }

    @Redirect(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tick(Ljava/util/function/BooleanSupplier;)V"))
    public synchronized void addTick(ServerLevel instance, BooleanSupplier k) {
        //if (busID == null) {
        //    try {
        //        busID = EventBus.class.getDeclaredField("busID");
        //        busID.setAccessible(true);
        //    } catch (NoSuchFieldException e) {
        //        throw    new RuntimeException(e);
        //    }
        //}

        K_multi_threading$taskListBuff.add(()->{
            instance.tick(k);
        });
    }

    @Redirect(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onPostLevelTick(Lnet/minecraft/world/level/Level;Ljava/util/function/BooleanSupplier;)V", remap = false))
    public synchronized void forgeEvent2(Level level, BooleanSupplier haveTime) {
        //if (busID == null) {
        //    try {
        //        busID = EventBus.class.getDeclaredField("busID");
        //        busID.setAccessible(true);
        //    } catch (NoSuchFieldException e) {
        //        throw    new RuntimeException(e);
        //    }
        //}

        assert level != null;

        K_multi_threading$taskListBuff.add(()->{

            TickEvent.LevelTickEvent levelTickEvent = new TickEvent.LevelTickEvent(level.isClientSide ? LogicalSide.CLIENT : LogicalSide.SERVER, TickEvent.Phase.END, level, haveTime);
            EventUtil.runEvent(Base.getEx(), MinecraftForge.EVENT_BUS,levelTickEvent);
        });
    }

    @Redirect(method = "tickChildren", at = @At(value = "INVOKE", target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"))
    public Object addTask(Map<Object, long[]> map, Object isClientSide, Function function) {
        K_multi_threading$taskListBuff.add(()->{
            map.computeIfAbsent(isClientSide, function)[this.tickCount % 100] = net.minecraft.Util.getNanos() - K_multi_threading$startTime;
        });
        K_multi_threading$taskList.add(K_multi_threading$taskListBuff);
        K_multi_threading$taskListBuff = new ArrayList<>();
        return Util.EmptyTps;
    }

    @Inject(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 1))
    public void callTask(BooleanSupplier p_129954_, CallbackInfo ci) {
        this.profiler.push("WorldTickMultiThreading");
        CalculateTask submit = new CalculateTask(()->"worldTick", 0, K_multi_threading$taskList.size(), i -> {
            if(i < K_multi_threading$taskList.size()){
                for (Runnable runnable : K_multi_threading$taskList.get(i)) {
                    runnable.run();
                }
                //return null;
            }
        });
        Base.getEx().submit(submit);
        WorldError worldError = submit.unsafeWaitThread(t -> {
            if (K_multi_threading$removeErrorSize-- >= 0) {
                Base.LOGGER.info("", t);
                return null;
            }else {
                throw new RuntimeException(t);
            }
        });
        K_multi_threading$taskList.clear();
        this.profiler.pop();
    }

    //@Redirect(method = "pollTaskInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getAllLevels()Ljava/lang/Iterable;"))
    //public synchronized Iterable<ServerLevel> pollChunkLoad(MinecraftServer instance) throws ExecutionException, InterruptedException {
    //    @Nullable ServerLevel[] serverLevels = Iterables.toArray(instance.getAllLevels(), ServerLevel.class);
//
    //    Base.WaitInt waitInt = new Base.WaitInt();
    //    Base.ForkJoinPool_ ex = Base.getEx();
//
    //    waitInt.size = serverLevels.length;
//
    //    CalculateTask<List<Object>> submit = (new CalculateTask<>(0, serverLevels.length, (i) -> {
    //        serverLevels[i].getChunkSource().pollTask();
    //        synchronized (waitInt) {
    //            waitInt.size--;
    //        }
    //        return null;
    //    }));
//
    //    ex.submit(submit);
    //    submit.waitThread();
    //    return List.of();
    //}



    @Redirect(method = "tickServer", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onPreServerTick(Ljava/util/function/BooleanSupplier;Lnet/minecraft/server/MinecraftServer;)V"), remap = false)
    public synchronized void forgeEvent3(BooleanSupplier haveTime, MinecraftServer server) {

        EventUtil.runEvent(Base.getEx(), MinecraftForge.EVENT_BUS, new TickEvent.ServerTickEvent(TickEvent.Phase.START, haveTime, server));
    }

    @Redirect(method = "tickServer", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onPostServerTick(Ljava/util/function/BooleanSupplier;Lnet/minecraft/server/MinecraftServer;)V"), remap = false)
    public synchronized void forgeEvent4(BooleanSupplier haveTime, MinecraftServer server) {
        EventUtil.runEvent(Base.getEx(), MinecraftForge.EVENT_BUS, new TickEvent.ServerTickEvent(TickEvent.Phase.END, haveTime, server));
    }
}
