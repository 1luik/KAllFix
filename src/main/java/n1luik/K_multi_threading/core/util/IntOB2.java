package n1luik.K_multi_threading.core.util;

import lombok.Getter;
import lombok.Setter;

public class IntOB2<T2> {
    @Getter
    @Setter
    public int t1;
    @Getter
    @Setter
    public T2 t2;

    public IntOB2() {
    }

    public IntOB2(int t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }
}
