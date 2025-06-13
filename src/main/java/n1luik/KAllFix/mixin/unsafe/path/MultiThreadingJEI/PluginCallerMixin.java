package n1luik.KAllFix.mixin.unsafe.path.MultiThreadingJEI;

import com.google.common.base.Stopwatch;
import mezz.jei.api.IModPlugin;
import mezz.jei.forge.startup.StartEventObserver;
import mezz.jei.library.load.PluginCaller;
import mezz.jei.library.load.PluginCallerTimer;
import n1luik.K_multi_threading.core.base.CalculateTask;
import n1luik.K_multi_threading.core.base.CalculateTask2;
import n1luik.K_multi_threading.core.util.EventUtil;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

import static n1luik.KAllFix.util.Args.JEITaskMax;

@Mixin(value = PluginCaller.class, remap = false)
public abstract class PluginCallerMixin {
    @Unique
    @Shadow(remap = false) @Final private static Logger LOGGER;

    //@Redirect(method = "callOnPlugins", at = @At(value = "INVOKE", target = "java/util/function/Consumer.accept(Ljava/lang/Object;)V"), remap = false)
    //private static <T> void impl1(Consumer<T> instance, T t){
    //}
    //@Redirect(method = "callOnPlugins", at = @At(value = "INVOKE", target = "Lmezz/jei/library/load/PluginCallerTimer;end()V"), remap = false)
    //private static <T> void impl2(PluginCallerTimer instance){
    //}
    //@Inject(method = "callOnPlugins", at = @At(value = "INVOKE", target = "Lmezz/jei/library/load/PluginCallerTimer;end()V"), remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
    //private static void impl3(String title, List<IModPlugin> plugins, Consumer<IModPlugin> func, CallbackInfo ci, Stopwatch stopwatch, PluginCallerTimer timer, Iterator var5, IModPlugin plugin, ResourceLocation pluginUid){
    //    Util.backgroundExecutor().execute();
    //}
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static void callOnPlugins(String title, List<IModPlugin> plugins, Consumer<IModPlugin> func) {
        LOGGER.info("{}...", title);
        Stopwatch stopwatch = Stopwatch.createStarted();
        try (PluginCallerTimer timer = new PluginCallerTimer()) {

            if (Util.backgroundExecutor() instanceof ForkJoinPool pool) {
                CopyOnWriteArrayList<IModPlugin> erroredPlugins = new CopyOnWriteArrayList<>();
                int length = plugins.size();
                CalculateTask2 submit = new CalculateTask2(() -> "MultiThreadingJEI", 0, length, (i) -> {
                    if (i < length) {
                        IModPlugin plugin = plugins.get(i);
                        try {
                            ResourceLocation pluginUid = plugin.getPluginUid();
                            timer.begin(title, pluginUid);
                            func.accept(plugin);
                            timer.end();
                        } catch (RuntimeException | LinkageError e) {
                            LOGGER.error("Caught an error from mod plugin: {} {}", plugin.getClass(), plugin.getPluginUid(), e);
                            erroredPlugins.add(plugin);
                        }
                    }
                }, JEITaskMax);

                submit.call(pool);
                plugins.removeAll(erroredPlugins);
                return ;

            }else{
                List<IModPlugin> erroredPlugins = new ArrayList<>();

                for (IModPlugin plugin : plugins) {
                    try {
                        ResourceLocation pluginUid = plugin.getPluginUid();
                        timer.begin(title, pluginUid);
                        func.accept(plugin);
                        timer.end();
                    } catch (RuntimeException | LinkageError e) {
                        LOGGER.error("Caught an error from mod plugin: {} {}", plugin.getClass(), plugin.getPluginUid(), e);
                        erroredPlugins.add(plugin);
                    }
                }
                plugins.removeAll(erroredPlugins);
            }
        }

        LOGGER.info("{} took {}", title, stopwatch);
    }
}
