package n1luik.KAllFix.mixin.unsafe.path.TooltipMultiThreading;

import n1luik.K_multi_threading.core.util.EventUtil;
import net.minecraft.Util;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.ForkJoinPool;

@Mixin(ForgeEventFactory.class)
public class ForgeEventFactoryImpl {
    @Redirect(method = "onItemTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z", remap = false), remap = false)
    private static boolean impl1(IEventBus instance, Event event){
        if (Util.backgroundExecutor() instanceof ForkJoinPool pool) {
            EventUtil.runEvent(pool, instance, event);
            return true;
        }else {
            return instance.post(event);
        }
    }
}
