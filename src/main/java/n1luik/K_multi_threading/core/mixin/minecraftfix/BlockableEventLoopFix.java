package n1luik.K_multi_threading.core.mixin.minecraftfix;

import net.minecraft.util.thread.BlockableEventLoop;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Deprecated
@Mixin(BlockableEventLoop.class)
public class BlockableEventLoopFix<R> {
    @Mutable
    @Shadow @Final private Queue<R> pendingRunnables;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void fix(String p_18686_, CallbackInfo ci){
        pendingRunnables = new ConcurrentLinkedQueue<>(){
            @Override
            public synchronized boolean add(R r) {
                return super.add(r);
            }

            @Override
            public synchronized boolean addAll(Collection<? extends R> c) {
                return super.addAll(c);
            }

            @Override
            public synchronized boolean removeAll(Collection<?> c) {
                return super.removeAll(c);
            }

            @Override
            public synchronized boolean remove(Object o) {
                return super.remove(o);
            }

            @Override
            public synchronized boolean contains(Object o) {
                return super.contains(o);
            }

            @Override
            public synchronized boolean containsAll(@NotNull Collection<?> c) {
                return super.containsAll(c);
            }

            @Override
            public synchronized void clear() {
                super.clear();
            }

            @Override
            public synchronized R peek() {
                return super.peek();
            }

            @Override
            public synchronized int size() {
                return super.size();
            }
        };
    }
}
