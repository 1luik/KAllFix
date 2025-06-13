package n1luik.K_multi_threading.core;

import lombok.Getter;

public class UnsafeEnable {
    public static final UnsafeEnable INSTANCE = new UnsafeEnable();
    private UnsafeEnable(){}

    public final boolean IndependencePlayer = Boolean.getBoolean("KMT_IndependencePlayer");

}
