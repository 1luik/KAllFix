package n1luik.KAllFix.forge;

import n1luik.KAllFix.data.packetOptimize.ClientboundCompress1HashPacket;
import n1luik.KAllFix.data.packetOptimize.ClientboundCompress1Packet;
import n1luik.KAllFix.forge.LoginProtectionMod.packet.ClientboundRemoveLoginProtectionPacket;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketOptimizeAll {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel PACKET_INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Base.MOD_ID2,"compress_packet"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    static {
        PACKET_INSTANCE.registerMessage(0x00, ClientboundCompress1Packet.class, ClientboundCompress1Packet::write, ClientboundCompress1Packet::new,ClientboundCompress1Packet::handle);
        PACKET_INSTANCE.registerMessage(0x01, ClientboundCompress1HashPacket.class, ClientboundCompress1HashPacket::write, ClientboundCompress1HashPacket::new,ClientboundCompress1HashPacket::handle);
    }
}
