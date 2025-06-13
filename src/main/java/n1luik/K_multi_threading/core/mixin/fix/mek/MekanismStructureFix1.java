package n1luik.K_multi_threading.core.mixin.fix.mek;

import mekanism.common.lib.multiblock.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.NavigableMap;
import java.util.function.BiConsumer;

@Deprecated
@Mixin(value = Structure.class,remap = false, priority = 1001)
public class MekanismStructureFix1 {

    @Redirect(method = "add",at = @At(value = "INVOKE",target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V",remap = false))
    public <K,V> void fix1(Map<K,V> instance, BiConsumer<? super K, ? super V> v) {
        synchronized (instance){
            instance.forEach(v);
        }
    }
    @Redirect(method = "add",at = @At(value = "INVOKE",target = "Ljava/util/NavigableMap;forEach(Ljava/util/function/BiConsumer;)V",remap = false))
    public <K,V> void fix2(NavigableMap<K,V> instance, BiConsumer<K,V> biConsumer) {
        synchronized (instance){
            instance.forEach(biConsumer);
        }
    }
}
