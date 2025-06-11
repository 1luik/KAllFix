package n1luik.K_multi_threading.core.mixin.minecraftfix;

import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.ScheduledTick;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import javax.annotation.Nullable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

@Mixin(LevelChunkTicks.class)
public class LevelChunkTicksFix1<T> {

    //@Mutable
    @Shadow @Final private Queue<ScheduledTick<T>> tickQueue;

    @Shadow// @Nullable
    private BiConsumer<LevelChunkTicks<T>, ScheduledTick<T>> onTickAdded;

    //@Inject(method = "<init>*", at = @At("RETURN"))
    //public void fix2() {
    //    tickQueue = new ConcurrentLinkedQueue<>(tickQueue);
    //}
    /**
     * @author
     * @reason
     */
    @Overwrite
    private void scheduleUnchecked(ScheduledTick<T> p_193194_) {
        synchronized (this) {
            this.tickQueue.add(p_193194_);
        }
        if (this.onTickAdded != null) {
            this.onTickAdded.accept((LevelChunkTicks)(Object)this, p_193194_);
        }

    }
}
