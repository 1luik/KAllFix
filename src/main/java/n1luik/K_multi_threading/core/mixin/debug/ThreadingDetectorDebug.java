
package n1luik.K_multi_threading.core.mixin.debug;

import n1luik.K_multi_threading.core.util.ThreadingDetectorDebugImpl;
import net.minecraft.ReportedException;
import net.minecraft.util.ThreadingDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sun.misc.Unsafe;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

@Mixin(value = ThreadingDetector.class, priority = Integer.MAX_VALUE)
public class ThreadingDetectorDebug /*implements ThreadingDetectorDebugImpl*/ {
    //@Shadow @Final private Lock stackTraceLock;
    //@Mutable
    //@Shadow @Final private Semaphore lock;
    //@Shadow @Nullable private volatile Thread threadThatFailedToAcquire;
    //@Shadow @Nullable private volatile ReportedException fullException;
    @Unique
    public Throwable K_multi_threading$lockPos = null;
    @Unique
    private static final Logger k_multi_threading$logger1 = LoggerFactory.getLogger("[ThreadingDetector-debug1]");

    @Inject(method = "checkAndLock", at = @At("HEAD"))
    public void debug2(CallbackInfo ci) {
        if (K_multi_threading$lockPos == null){
            K_multi_threading$lockPos = new Throwable();
        }else {
            long l = System.nanoTime();
            k_multi_threading$logger1.debug("lockPos is null {}", l, K_multi_threading$lockPos);
            k_multi_threading$logger1.debug("lockPos is null_this {}", l, new Exception());

        }
    }
    @Inject(method = "checkAndUnlock", at = @At("HEAD"))
    public void debug3(CallbackInfo ci) {
        K_multi_threading$lockPos = null;
    }


    @Inject(method = "<init>", at = @At("RETURN"))
    public void debug1(String p_199415_, CallbackInfo ci){
        //lock = K_multi_threading$create(1);

    }

    //@Override
    public String K_multi_threading$debugText() {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        K_multi_threading$lockPos.printStackTrace(new PrintStream(byteArrayOutputStream));
        return byteArrayOutputStream.toString();
    }
}
