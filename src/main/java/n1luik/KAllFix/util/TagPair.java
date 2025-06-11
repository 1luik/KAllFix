package n1luik.KAllFix.util;

import com.mojang.datafixers.util.Pair;

public class TagPair<F, S> extends Pair<F, S> {
    public TagPair(F first, S second) {
        super(first, second);
    }
    public TagPair(Pair<F, S> v) {
        super(v.getFirst(), v.getSecond());
    }
}
