package n1luik.K_multi_threading.core.mixin.impl;

import n1luik.K_multi_threading.core.util.AbstractWaitImpl;
import n1luik.K_multi_threading.core.util.WaitCall;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class,priority = Integer.MAX_VALUE-10000)//防止提前运行一些不该运行的
public class WaitImplMinecraftServerMixin implements WaitCall<Object> {
    public AbstractWaitImpl<Object> waitBase;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        waitBase = new AbstractWaitImpl<>();
    }

    @Inject(method = "tickServer", at = @At("HEAD"))
    private void wait(CallbackInfo ci) {
        waitBase.setDisable(false);
    }

    @Inject(method = "tickServer", at = @At("RETURN"))
    private void unpack(CallbackInfo ci) {
        waitBase.unpark();
        waitBase.setDisable(true);
    }

    @Override
    public void wait(Object o) {
        waitBase.wait(o);
    }

    @Override
    public void executeTask(@NotNull Runnable command) {
        waitBase.executeTask(command);

    }
}
