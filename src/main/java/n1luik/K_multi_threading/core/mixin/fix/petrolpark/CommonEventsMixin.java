package n1luik.K_multi_threading.core.mixin.fix.petrolpark;

import com.petrolpark.Petrolpark;
import com.petrolpark.event.CommonEvents;
import com.petrolpark.item.decay.DecayingItemHandler;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CommonEvents.class)
public class CommonEventsMixin {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public static void onTickLevel(TickEvent.LevelTickEvent event) {
        // Decaying Items
        if (event.phase == TickEvent.LevelTickEvent.Phase.END) {
            if (!event.level.isClientSide() && event.level.getServer().overworld() == event.level) {
                if (Petrolpark.DECAYING_ITEM_HANDLER.get() instanceof DecayingItemHandler.ServerDecayingItemHandler serverDecayingItemHandler) {
                    serverDecayingItemHandler.gameTime++;
                }
            }
        };

    };
}
