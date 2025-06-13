package n1luik.KAllFix.util.ob;

import lombok.Getter;
import lombok.Setter;

public class OBbyteArrayInt {
    @Getter
    @Setter
    public byte[] t1;
    @Getter
    @Setter
    public int t2;

    public OBbyteArrayInt() {
    }

    public OBbyteArrayInt(byte[] t1, int t2) {
        this.t1 = t1;
        this.t2 = t2;
    }
}
