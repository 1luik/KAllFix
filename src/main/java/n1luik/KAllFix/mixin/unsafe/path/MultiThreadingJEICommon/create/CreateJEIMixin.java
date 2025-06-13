package n1luik.KAllFix.mixin.unsafe.path.MultiThreadingJEICommon.create;

import com.simibubi.create.compat.jei.CreateJEI;
import mezz.jei.api.IModPlugin;
import n1luik.K_multi_threading.core.base.CalculateTask2;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

import static n1luik.KAllFix.util.Args.JEITaskMax;

@Mixin(value = CreateJEI.class, remap = false)
public class CreateJEIMixin {
    @Redirect(method = "registerRecipes", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", remap = false), remap = false)
    public <T> void impl1(List<T> list, Consumer<T> consumer) {

        if (Util.backgroundExecutor() instanceof ForkJoinPool pool) {

            CopyOnWriteArrayList<IModPlugin> erroredPlugins = new CopyOnWriteArrayList<>();
            int length = list.size();
            CalculateTask2 submit = new CalculateTask2(() -> "MultiThreadingJEI", 0, length, (i) -> {
                if (i < length) {
                    consumer.accept(list.get(i));
                }
            }, JEITaskMax);

            submit.call(pool);
        }else {
            list.forEach(consumer);
        }
    }
}
