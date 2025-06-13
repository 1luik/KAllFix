package n1luik.K_multi_threading.core.mixin.minecraftfix.loginMultiThreading;

import n1luik.K_multi_threading.core.Base;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(value = ServerLoginPacketListenerImpl.class, priority = Integer.MAX_VALUE - 1000)
public class ServerLoginPacketListenerImplImpl1 {
    //@Unique private static final Object K_multi_threading$lockLoginTaskMax = new Object();//K_multi_threading$waitTaskSize的
    @Unique private static final Object K_multi_threading$lockLoginTaskGet = new Object();//防止在可能的一小段检测空白时间提交任务导致无法执行
    @Unique private static final Object K_multi_threading$lockLoginTaskRun = new Object();//在运行完执行等待的任务的时候防止执行出现问题
    @Unique private static final List<Runnable> K_multi_threading$waitTask = new CopyOnWriteArrayList<>();
    @Unique private static final AtomicInteger K_multi_threading$waitTaskSize = new AtomicInteger(0);
    @Unique private static final int K_multi_threading$waitTaskMax = Integer.getInteger("KMT-LoginMultiThreading.TaskSizeMax", 8);
    @Unique volatile boolean asynchronous = false;
    @Unique final Object lockLogin = new Object();
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void impl1(CallbackInfo ci) {
        if (asynchronous) ci.cancel();
    }

    @Unique
    private static void K_multi_threading$runTask() {
        if (K_multi_threading$waitTaskMax >= K_multi_threading$waitTaskSize.get()) {
            K_multi_threading$waitTaskSize.getAndAdd(1);
            Base.ForkJoinPool_ ex = Base.getEx();
            RecursiveTask<?> poolTask = new RecursiveTask<>() {
                @Override
                protected Void compute() {
                    Runnable remove;
                    synchronized (K_multi_threading$lockLoginTaskGet) {
                        if (K_multi_threading$waitTask.isEmpty()) {
                            return null;
                        }
                        remove = K_multi_threading$waitTask.remove(K_multi_threading$waitTask.size() - 1);
                    }
                    remove.run();
                    K_multi_threading$waitTaskSize.getAndDecrement();
                    synchronized (K_multi_threading$lockLoginTaskRun) {
                        if (K_multi_threading$waitTaskMax >= K_multi_threading$waitTaskSize.get()) {
                            int size = K_multi_threading$waitTaskMax - K_multi_threading$waitTaskSize.get();
                            for (int i = 0; i < size; i++) {
                                K_multi_threading$runTask();
                            }
                        }
                    }
                    return null;
                }
            };

            ex.submit(poolTask);//网上没有查到资料
        }
    }
    @Unique
    private static void K_multi_threading$addTask(Runnable task) {
        synchronized (K_multi_threading$lockLoginTaskGet) {
            K_multi_threading$waitTask.add(task);
        }
        K_multi_threading$runTask();
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;placeNewPlayer(Lnet/minecraft/network/Connection;Lnet/minecraft/server/level/ServerPlayer;)V"))
    public synchronized void impl2(PlayerList instance, Connection s, ServerPlayer serverlevel1) {
        asynchronous = true;

        K_multi_threading$addTask(()->{
            synchronized (lockLogin) {
                instance.placeNewPlayer(s, serverlevel1);
                asynchronous = false;
            }
        });
    }


}
