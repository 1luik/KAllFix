package n1luik.K_multi_threading.core.mixin.minecraftfix;

import net.minecraft.util.ThreadingDetector;
import net.minecraft.world.level.levelgen.BitRandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.atomic.AtomicLong;

@Mixin(LegacyRandomSource.class)
public abstract class LegacyRandomSourceFix1 implements BitRandomSource {
    @Shadow @Final private AtomicLong seed;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int next(int p_188581_) {
        long i;
        long j;
        do {
            i = this.seed.get();
            j = i * 25214903917L + 11L & 281474976710655L;
        } while(!this.seed.compareAndSet(i, j));

        return (int)(j >>> 48 - p_188581_);
    }
//默认是单线程的覆盖就没有锁了
    public int nextInt() {
        return this.next(32);
    }

    public int nextInt(int p_188504_) {
        if (p_188504_ <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        } else if ((p_188504_ & p_188504_ - 1) == 0) {
            return (int)((long)p_188504_ * (long)this.next(31) >> 31);
        } else {
            int i;
            int j;
            do {
                i = this.next(31);
                j = i % p_188504_;
            } while(i - j + (p_188504_ - 1) < 0);

            return j;
        }
    }

    public long nextLong() {
        int i = this.next(32);
        int j = this.next(32);
        long k = (long)i << 32;
        return k + (long)j;
    }

    public boolean nextBoolean() {
        return this.next(1) != 0;
    }

    public float nextFloat() {
        return (float)this.next(24) * 5.9604645E-8F;
    }

    public double nextDouble() {
        int i = this.next(26);
        int j = this.next(27);
        long k = ((long)i << 27) + (long)j;
        return (double)k * (double)1.110223E-16F;
    }
}
