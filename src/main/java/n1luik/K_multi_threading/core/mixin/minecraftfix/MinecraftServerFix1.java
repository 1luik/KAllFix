package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.K_multi_threading.core.Imixin.IMainThreadExecutor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class)
public abstract class MinecraftServerFix1 extends BlockableEventLoop<TickTask> {
    //@Shadow protected abstract ServerLevel[] getWorldArray();

    protected MinecraftServerFix1(String p_18686_) {
        super(p_18686_);
    }

    ////速度可能慢
    //@Redirect(method = "pollTaskInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;pollTask()Z"))
    //public boolean fix2(ServerChunkCache instance){
    //    if ((instance.mainThreadProcessor) instanceof IMainThreadExecutor iMainThreadExecutor) {
    //        iMainThreadExecutor.pushThread();
    //    }
    //    instance.pollTask();
    //}

    //兼容不好
    //@Inject(method = "waitUntilNextTick", at = @At("HEAD"))
    //public void fix1(CallbackInfo ci){
    //    for (ServerLevel serverLevel : getWorldArray()) {
    //        if ((serverLevel.getChunkSource().mainThreadProcessor) instanceof IMainThreadExecutor iMainThreadExecutor) {
    //            iMainThreadExecutor.pushThread();
    //        }
    //    }
    //}

    //@Override
    //public void tell(TickTask p_18712_) {
    //    this.pendingRunnables.add(p_18712_);
    //    //LockSupport.unpark(this.getRunningThread());
    //}
}
