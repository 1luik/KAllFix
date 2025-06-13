package n1luik.KAllFix.forge.LoginProtectionMod;

import n1luik.KAllFix.forge.LoginProtectionMod.packet.ClientboundRemoveLoginProtectionPacket;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class LoginProtectionModEvent {
    private static final String PROTOCOL_VERSION = "1";
    public static boolean LoginProtection = false;

    public static final SimpleChannel PACKET_INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Base.MOD_ID2,"login_protection_mod"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    static {
        PACKET_INSTANCE.registerMessage(0x00, ClientboundRemoveLoginProtectionPacket.class, ClientboundRemoveLoginProtectionPacket::write, ClientboundRemoveLoginProtectionPacket::new,ClientboundRemoveLoginProtectionPacket::handle);
    }
    @SubscribeEvent
    public static void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer entity = (ServerPlayer) event.getEntity();
        PACKET_INSTANCE.send(PacketDistributor.PLAYER.with(() -> entity), new ClientboundRemoveLoginProtectionPacket());
    }
}
