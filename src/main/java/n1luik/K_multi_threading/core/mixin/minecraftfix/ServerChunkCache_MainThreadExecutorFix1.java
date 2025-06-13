package n1luik.K_multi_threading.core.mixin.minecraftfix;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BooleanSupplier;

@Deprecated
@Mixin(ServerChunkCache.MainThreadExecutor.class)
public abstract class ServerChunkCache_MainThreadExecutorFix1 extends BlockableEventLoop<Runnable> {
    protected ServerChunkCache_MainThreadExecutorFix1(String p_18686_) {
        super(p_18686_);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(ServerChunkCache p_8493_, Level p_8494_, CallbackInfo ci) {
    }

    @Override
    public void tell(Runnable p_18712_) {
        p_18712_.run();
    }

    @Override
    public boolean isSameThread() {
        return true;
    }

    @Override
    public void execute(Runnable p_18706_) {
        p_18706_.run();
    }

    @Override
    protected void waitForTasks() {
        //super.waitForTasks();
    }
/*
    public void managedBlock(BooleanSupplier p_18702_) {
        ++this.blockingCount;

        try {
            while(!p_18702_.getAsBoolean()) {
                if (!this.pollTask()) {
                    this.waitForTasks();
                }
            }
        } finally {
            --this.blockingCount;
        }

    }
*/
    @Redirect(method = "pollTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/thread/BlockableEventLoop;pollTask()Z"))
    public boolean pollTask_2(BlockableEventLoop<Runnable> eventLoop) {
        return false;//Runnable r = this.pendingRunnables.peek();
        //if (r == null) {
        //    return false;
        //} else /*if (!this.shouldRun(r)) {
        //    return false;
        //} else */{
        //    this.doRunTask(this.pendingRunnables.remove());
        //    return true;
        //}
    }

    //@Inject(method = "pollTask", at = @At(value = "RETURN",ordinal = 0))
    //public void debug1(CallbackInfoReturnable<Boolean> cir){
    //    System.out.println("!!!!!Debug1: " + cir.getReturnValue());
    //}
    //@Inject(method = "pollTask", at = @At(value = "RETURN",ordinal = 1))
    //public void debug2(CallbackInfoReturnable<Boolean> cir){
    //    System.out.println("!!!!!Debug2: " + cir.getReturnValue());
    //}
}
