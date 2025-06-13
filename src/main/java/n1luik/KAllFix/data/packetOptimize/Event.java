package n1luik.KAllFix.data.packetOptimize;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.TimeUnit;

public class Event {
    private static long ReOutputBufTime = System.currentTimeMillis();
    private static final long ReOutputTime = Long.getLong("-DKAF-packetOptimize.AttributesReOutputTime", TimeUnit.MINUTES.toMillis(2));

    @SubscribeEvent
    public static void onPlayerLonin(PlayerEvent.PlayerLoggedInEvent event) {
        AttributesPacket.AttributesRemove.getAndAdd(1);
    }
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        AttributesPacket.AttributesRemove.getAndAdd(1);
    }
    @SubscribeEvent
    public static void onServerEndTick(TickEvent.ServerTickEvent event) {
        if (ReOutputBufTime + ReOutputTime < System.currentTimeMillis()) {
            AttributesPacket.AttributesRemove.getAndAdd(1);
            ReOutputBufTime = System.currentTimeMillis();
        }
    }
}
