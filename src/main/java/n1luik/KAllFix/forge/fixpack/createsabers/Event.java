package n1luik.KAllFix.forge.fixpack.createsabers;

import net.createsabers.CreateSabersMod;
import net.createsabers.network.CreateSabersModVariables;
import net.mcreator.mekanismexplosives.MekanismexplosivesMod;
import net.mcreator.mekanismexplosives.network.MekanismexplosivesModVariables;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public class Event {

    @SubscribeEvent
    public static void onPlayerLonin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            PacketDistributor.PacketTarget with = PacketDistributor.PLAYER.with(() -> sp);
            CreateSabersMod.PACKET_HANDLER.send(with, new CreateSabersModVariables.SavedDataSyncMessage(0, CreateSabersModVariables.MapVariables.get(sp.level())));
        }
    }
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            PacketDistributor.PacketTarget with = PacketDistributor.PLAYER.with(() -> sp);
            CreateSabersMod.PACKET_HANDLER.send(with, new CreateSabersModVariables.SavedDataSyncMessage(0, CreateSabersModVariables.MapVariables.get(sp.getServer().getLevel(event.getTo()))));
        }
    }
}
