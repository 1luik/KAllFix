package n1luik.KAllFix.util;

import lombok.Getter;
import lombok.Setter;

public class OB1<T1> {
    @Getter
    @Setter
    public T1 t1;

    public OB1() {
    }

    public OB1(T1 t1) {
        this.t1 = t1;
    }
}