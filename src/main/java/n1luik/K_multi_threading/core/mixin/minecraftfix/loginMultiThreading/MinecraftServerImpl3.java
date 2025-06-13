package n1luik.K_multi_threading.core.mixin.minecraftfix.loginMultiThreading;

import n1luik.K_multi_threading.core.Base;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConnectionListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerImpl3 {
    @Shadow @Nullable public abstract ServerConnectionListener getConnection();
    //可能可以不同步这样可以节省对齐浪费的时间
    @Unique
    private static final boolean K_multi_threading$ConnectionLock = Boolean.getBoolean("KMT-LoginMultiThreading.ConnectionLock");
    @Unique
    volatile long K_multi_threading$nullJ1 = 0;
    @Redirect(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerConnectionListener;tick()V"))
    public void impl1(ServerConnectionListener instance){
        Base.ForkJoinPool_ ex = Base.getEx();
        //这样可以保证tick不会无限创建任务
        synchronized (getConnection().getConnections()) {
            K_multi_threading$nullJ1 = 1;
        }
        RecursiveTask<?> task = new RecursiveTask<>() {
            @Override
            protected Void compute() {
                instance.tick();
                return null;
            }
        };

        ex.submit(task);
    }
    @Inject(method = "tickChildren", at = @At("RETURN"))
    public void impl2(BooleanSupplier p_129954_, CallbackInfo ci){
        if (K_multi_threading$ConnectionLock) {
            //ServerConnectionListener.tick会sync connections通过在sync这个就可以同步
            synchronized (getConnection().getConnections()) {
                K_multi_threading$nullJ1 = 1;
            }
        }
    }
}
