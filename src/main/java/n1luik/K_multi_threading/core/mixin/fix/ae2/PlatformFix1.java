package n1luik.K_multi_threading.core.mixin.fix.ae2;

import n1luik.K_multi_threading.core.Base;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = appeng.util.Platform.class,remap = false,priority = 1100)
public class PlatformFix1 {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void assertServerThread() {
        if (Base.debugAE2Thread){
            if (!Base.isThreadPooled(Thread.currentThread())) {
                throw new UnsupportedOperationException("This code can only be called server-side and this is most likely a bug.");
            }
        }
    }
}
