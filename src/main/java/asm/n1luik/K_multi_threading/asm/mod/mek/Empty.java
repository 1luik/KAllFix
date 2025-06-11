package asm.n1luik.K_multi_threading.asm.mod.mek;

import java.util.Map;
import java.util.NavigableMap;
import java.util.function.BiConsumer;

public class Empty {
    public static void empty(){

    }

    public static <K,V> void forEach(Map<K,V> map, BiConsumer<K,V> consumer){
        synchronized (map){
            map.forEach(consumer);
        }
    }
    public static <K,V> void forEach(NavigableMap<K,V> map, BiConsumer<K,V> consumer){
        synchronized (map){
            map.forEach(consumer);
        }
    }
}
