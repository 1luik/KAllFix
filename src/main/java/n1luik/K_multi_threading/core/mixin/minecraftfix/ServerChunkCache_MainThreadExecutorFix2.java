package n1luik.K_multi_threading.core.mixin.minecraftfix;

import lombok.Getter;
import lombok.Setter;
import n1luik.K_multi_threading.core.Imixin.IMainThreadExecutor;
import n1luik.K_multi_threading.core.base.CalculateTask;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;

@Mixin(ServerChunkCache.MainThreadExecutor.class)
public abstract class ServerChunkCache_MainThreadExecutorFix2 extends BlockableEventLoop<Runnable> implements IMainThreadExecutor {
    @Unique
    private static final boolean K_multi_threading$StartMode2 = Boolean.getBoolean("KMT-ChunkGeneratorMode2Start");
    protected ServerChunkCache_MainThreadExecutorFix2(String p_18686_) {
        super(p_18686_);
    }
    @Unique
    @Getter
    private Thread callThread;
    @Unique
    @Getter
    public final Object lockCall = new Object();
    @Unique
    //@Getter
    //private final AtomicInteger isCall = new AtomicInteger();
    private volatile int isCall = 0;
    @Unique
    @Setter
    private boolean m2 = K_multi_threading$StartMode2;
    @Unique
    private int multiThreadingSize = 0;

    @Override
    public boolean isCall() {
        return isCall != 0;//return isCall.get() != 0;//
    }

    @Override
    public void k_multi_threading$pushThread() {
        callThread = Thread.currentThread();
    }

    @Override
    public void tell(Runnable p_18712_) {
        if (m2) {
            p_18712_.run();
        }else {
            this.pendingRunnables.add(p_18712_);
            LockSupport.unpark(this.getRunningThread());
        }
    }

    @Override
    public boolean k_multi_threading$notCallPollTask() {
        return super.pollTask();
    }


    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(ServerChunkCache p_8493_, Level p_8494_, CallbackInfo ci) {
        callThread = Thread.currentThread();
    }

    //@Override
    //public boolean isSameThread() {
    //    return isCall && super.isSameThread();
    //}
    //@Override
    //public void tell(Runnable p_18712_) {
    //    p_18712_.run();
    //}

    //@Override
    //public boolean isSameThread() {
    //    return true;
    //}

    //@Override
    //public void execute(Runnable p_18706_) {
    //    p_18706_.run();
    //}


    //@Override
    //protected void runAllTasks() {
    //    while(this.K_multi_threading$pollTask_2()) {
    //    }
    //}

    /**
     * @author
     * @reason
     */
    @Overwrite//@Redirect(method = "getRunningThread", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerChunkCache;mainThread:Ljava/lang/Thread;"))
    protected Thread getRunningThread() {
        return callThread;
    }

    @Override
    public void k_multi_threading$setMultiThreading(int size) {
        multiThreadingSize = size;
    }

    @Override
    public void execute(Runnable p_18706_) {
        if (m2){
            p_18706_.run();
        }else {
            super.execute(p_18706_);
        }
    }

    //@Override
    //protected void waitForTasks() {
    //    //Thread.yield();
    //    //LockSupport.parkNanos("waiting for tasks", 10000L);
    //    //super.waitForTasks();
    //}

    @Override
    public void managedBlock(BooleanSupplier p_18702_) {
        synchronized (lockCall) {
            isCall++;
        }
        //isCall.getAndAdd(1);
        callThread = Thread.currentThread();
        super.managedBlock(p_18702_);
        //{
//
        //    ++this.blockingCount;
//
        //    try {
        //        while(!p_18702_.getAsBoolean()) {
        //            if (!(this.pollTask() || super.pollTask())) {
        //                this.waitForTasks();
        //            }
        //        }
        //    } finally {
        //        --this.blockingCount;
        //    }
        //}
        //修复LevelFix1的代码有概率在处理地形生成后运行任务导致无法运行
        //isCall.getAndAdd(-1);
        synchronized (lockCall){
            isCall--;
        }
        if (multiThreadingSize > 0) {
            K_multi_threading$pollTask_2();
        }else{
            runAllTasks();
        }
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
        return K_multi_threading$pollTask_2();
    }
    @Unique
    public boolean K_multi_threading$pollTask_2() {
        if (multiThreadingSize > 0) {
            int i = 0;
            Runnable[] runnables = new Runnable[multiThreadingSize];
            while (true){
                Runnable r = this.pendingRunnables.peek();
                if (r == null) {
                    if (i != 0) break;
                    return false;
                } else if (this.blockingCount == 0 && !this.shouldRun(r)) {
                    if (i != 0) break;
                    return false;
                } else {
                    runnables[i++] = this.pendingRunnables.remove();
                    continue;
                }
            }
            new CalculateTask(()->"ChunkLoader", 0, multiThreadingSize, i2->{
                if (i2 < multiThreadingSize){
                    if (runnables[i2] != null){
                        this.doRunTask(runnables[i2]);
                    }
                }
            }).call();

            return true;
        }
        return !m2 && super.pollTask();//isCall && super.pollTask();//Runnable r = this.pendingRunnables.peek();
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
