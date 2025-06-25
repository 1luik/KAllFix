package n1luik.K_multi_threading.core;

import lombok.Getter;

public class UnsafeEnable {
    public static final UnsafeEnable INSTANCE = new UnsafeEnable();
    public final boolean IndependencePlayer = Boolean.getBoolean("KMT_IndependencePlayer");
    public final boolean SafeUnloadChunk = Boolean.getBoolean("KMT-SafeUnloadChunk");

    private UnsafeEnable(){}


}
