package n1luik.K_multi_threading.core.mixin.minecraftfix;

import net.minecraft.util.ClassInstanceMultiMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Mixin(ClassInstanceMultiMap.class)
public class ClassInstanceMultiMapFix1<T> {

    @Shadow @Final private List<T> allInstances;

    //@Inject(method = "lambda$find$0", at = @At("HEAD"), cancellable = true)
    //public void fix1(Class p_13538_, CallbackInfoReturnable<List> cir){
    //    List<Object> objects1 = new ArrayList<>();
    //    for (T allInstance : allInstances) {
    //        if (p_13538_.isInstance(allInstance)){
    //            objects1.add(allInstance);
    //        }
    //    }
    //    cir.setReturnValue(new CopyOnWriteArrayList<>(objects1));
    //}

    @Shadow @Final private Class<T> baseClass;

    @Shadow @Final private Map<Class<?>, List<T>> byClass;

    //@Redirect(method = "lambda$find$0", at = @At(value = "INVOKE", target = "Ljava/util/stream/Collectors;toList()Ljava/util/stream/Collector;"))
    //private static <T> Collector<T, ?, List<T>> fix1(){
    //    return Collectors.toCollection(CopyOnWriteArrayList::new);//Collector.of(()->Collections.synchronizedList(new ArrayList<>()), List::add, (left, right) -> { left.addAll(right); return left; });//Collectors.toList(Collections.synchronizedList(new ArrayList<>()))
    //}

    /**
     * @author n1luik
     * @reason mixin不支持
     */
    @Overwrite
    public <S> Collection<S> find(Class<S> p_13534_) {
        if (!this.baseClass.isAssignableFrom(p_13534_)) {
            throw new IllegalArgumentException("Don't know how to search for " + p_13534_);
        } else {
            List<? extends T> list = this.byClass.computeIfAbsent(p_13534_, (p_13538_) -> {
                return this.allInstances.stream().filter(p_13538_::isInstance).collect(Collectors.toCollection(CopyOnWriteArrayList::new));
            });
            return (Collection<S>)Collections.unmodifiableCollection(list);
        }
    }


}
