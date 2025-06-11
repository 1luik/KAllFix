package n1luik.K_multi_threading.core.util;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VOB3_OOI<T1, T2> {
    public static final long t1_pos;
    public static final long t2_pos;
    public static final long t3_pos;
    static {

        try {
            t1_pos = Unsafe.unsafe.objectFieldOffset(VOB3_OOI.class.getDeclaredField("t1"));
            t2_pos = Unsafe.unsafe.objectFieldOffset(VOB3_OOI.class.getDeclaredField("t2"));
            t3_pos = Unsafe.unsafe.objectFieldOffset(VOB3_OOI.class.getDeclaredField("t3"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

    }
    public volatile T1 t1;
    public volatile T2 t2;
    public volatile int t3;

    public T1 getT1_() {
        return (T1) Unsafe.unsafe.getObjectVolatile(this, t1_pos);
    }

    public T2 getT2_() {
          return (T2) Unsafe.unsafe.getObjectVolatile(this, t2_pos);
    }

    public int getT3_() {
        return Unsafe.unsafe.getIntVolatile(this, t3_pos);
    }

    public void setT1_(T1 v) {
        Unsafe.unsafe.putObjectVolatile(this, t1_pos, v);
    }

    public void setT2_(T2 v) {
        Unsafe.unsafe.putObjectVolatile(this, t2_pos, v);
    }

    public void setT3_(int v) {
        Unsafe.unsafe.putIntVolatile(this, t3_pos, v);
    }


    public VOB3_OOI() {
    }

    public VOB3_OOI(T1 t1, T2 t2, int t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }
}
