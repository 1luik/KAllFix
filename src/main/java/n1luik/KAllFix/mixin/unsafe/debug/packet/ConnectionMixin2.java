package n1luik.KAllFix.mixin.unsafe.debug.packet;

import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class ConnectionMixin2 {
    @Unique
    private static final Logger k_multi_threading$logger = LoggerFactory.getLogger("[unsafe.debug.packet-debug3]");
    @Inject(method = "doSendPacket", at = @At("HEAD"))
    public void debug1(Packet<?> p_243260_, PacketSendListener p_243290_, ConnectionProtocol p_243203_, ConnectionProtocol p_243307_, CallbackInfo ci){
        FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
        p_243260_.write(friendlyByteBuf);
        int size = friendlyByteBuf.writerIndex();
        if (p_243260_ instanceof ServerboundCustomPayloadPacket packet2){
            k_multi_threading$logger.debug("len [{}], name [{}], class [ServerboundCustomPayloadPacket]", size, packet2.getName(), new Exception());
        }else if (p_243260_ instanceof ClientboundCustomPayloadPacket packet2){
            k_multi_threading$logger.debug("len [{}], name [{}], class [ClientboundCustomPayloadPacket]", size, packet2.getName(), new Exception());
        }else if (p_243260_ instanceof ServerboundCustomQueryPacket packet2){
            if (packet2.getData() != null) {
                k_multi_threading$logger.debug("len [{}], nname [{}], class [ServerboundCustomQueryPacket]", size, packet2.getName(), new Exception());
            }else {
                k_multi_threading$logger.debug("len [{}], name [{}], class [ServerboundCustomQueryPacket]", size, packet2.getName(), new Exception());
            }
        }else if (p_243260_ instanceof ClientboundCustomQueryPacket packet2){
            k_multi_threading$logger.debug("len [{}], name [{}], class [ClientboundCustomQueryPacket]", size, packet2.getName(), new Exception());
        }else {
            k_multi_threading$logger.debug("len [{}], nclass [{}]", size, p_243260_.getClass().getName(), new Exception());
        }

    }
}
