package asm.n1luik.K_multi_threading.asm;

import lombok.Getter;
import lombok.Setter;

public class OB2_ASM<T1, T2> {
    @Getter
    @Setter
    public T1 t1;
    @Getter
    @Setter
    public T2 t2;

    public OB2_ASM() {
    }

    public OB2_ASM(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }
}
