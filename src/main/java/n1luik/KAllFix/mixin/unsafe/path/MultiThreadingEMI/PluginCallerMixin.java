package n1luik.KAllFix.mixin.unsafe.path.MultiThreadingEMI;

import com.google.common.base.Stopwatch;
import mezz.jei.api.IModPlugin;
import mezz.jei.library.load.PluginCaller;
import mezz.jei.library.load.PluginCallerTimer;
import n1luik.K_multi_threading.core.base.CalculateTask;
import n1luik.K_multi_threading.core.base.CalculateTask2;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import com.google.common.collect.Sets;

import dev.emi.emi.EmiPort;
import dev.emi.emi.jemi.JemiUtil;
import mezz.jei.api.IModPlugin;
import mezz.jei.library.load.PluginCaller;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static n1luik.KAllFix.util.Args.JEITaskMax;


@Mixin(value = PluginCaller.class, remap = false, priority = Integer.MAX_VALUE-3)
public abstract class PluginCallerMixin {
    @Unique
    private static final Set<ResourceLocation> KAllFix$SKIPPED = Sets.newHashSet(
            EmiPort.id("jei", "minecraft"), EmiPort.id("jei", "gui"), EmiPort.id("jei", "fabric_gui"), EmiPort.id("jei", "forge_gui"), EmiPort.id("jei", "neoforge_gui")
    );
    @Unique
    private static final Set<String> KAllFix$SKIPPED_MODS = JemiUtil.getHandledMods();
    @Shadow(remap = false) @Final private static Logger LOGGER;
    @Unique
    private static final int KAllFix$taskMax = Integer.getInteger("KAF-JeiMultiThreading-TasxMax", CalculateTask.callMax);

    @Unique
    private static void KAllFix$emiCall(Consumer<IModPlugin> target, Object value, String title, List<IModPlugin> plugins, Consumer<IModPlugin> func) {
        IModPlugin plugin = (IModPlugin) value;
        ResourceLocation uid = plugin.getPluginUid();
        if (KAllFix$SKIPPED.contains(uid)) {
            switch (title) {
                case "Registering categories" -> {}
                case "Registering ingredients" -> {}
                case "Registering vanilla category extensions" -> {}
                case "Sending Runtime" -> {}
                case "Sending Runtime Unavailable" -> {}
                default -> { return; }
            }
        } else if (uid != null) {
            String namespace = uid.getNamespace();
            if (KAllFix$SKIPPED_MODS.contains(namespace) && !namespace.equals("jei") && !namespace.equals("emi")) {
                return;
            }
        }
        target.accept(plugin);
    }
    @Inject(method = "callOnPlugins", at = @At("HEAD"), cancellable = true)
    private static void callOnPlugins(String title, List<IModPlugin> plugins, Consumer<IModPlugin> func, CallbackInfo ci) {
        ci.cancel();
        LOGGER.info("{}...", title);
        Stopwatch stopwatch = Stopwatch.createStarted();
        try (PluginCallerTimer timer = new PluginCallerTimer()) {

            if (Util.backgroundExecutor() instanceof ForkJoinPool pool) {
                CopyOnWriteArrayList<IModPlugin> erroredPlugins = new CopyOnWriteArrayList<>();
                int length = plugins.size();
                CalculateTask2 submit = new CalculateTask2(()->"MultiThreadingJEI", 0, length, (i) -> {
                    if (i < length) {
                        IModPlugin plugin = plugins.get(i);
                        try {
                            ResourceLocation pluginUid = plugin.getPluginUid();
                            timer.begin(title, pluginUid);
                            KAllFix$emiCall(func, plugin, title, plugins, func);
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
                        KAllFix$emiCall(func, plugin, title, plugins, func);
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
