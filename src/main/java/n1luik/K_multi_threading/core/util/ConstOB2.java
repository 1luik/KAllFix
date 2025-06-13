package n1luik.K_multi_threading.core.util;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ConstOB2<T1, T2> {
    public final T1 t1;
    public final T2 t2;

    public ConstOB2(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }
}
