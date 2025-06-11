package n1luik.KAllFix.util.ob;

import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.List;

public class OBListDeque<T, D> {
    @Getter
    @Setter
    public List<T> t1;
    @Getter
    @Setter
    public Deque<D> t2;

    public OBListDeque() {
    }

    public OBListDeque(List<T> t1, Deque<D> t2) {
        this.t1 = t1;
        this.t2 = t2;
    }
}
