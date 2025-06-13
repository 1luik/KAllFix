package n1luik.KAllFix.mixin.unsafe.debug.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.channels.ClosedChannelException;

@Mixin(Connection.class)
public abstract class ConnectionMixin1 {
    @Unique
    private static final Logger k_multi_threading$logger = LoggerFactory.getLogger("[unsafe.debug.packet-debug1]");
    @Inject(method = "sendPacket", at = @At("HEAD"))
    public void debug1(Packet<?> p_129521_, PacketSendListener p_243246_, CallbackInfo ci){
        if (p_129521_ instanceof ServerboundCustomPayloadPacket packet2){
            k_multi_threading$logger.debug("len [{}], name [{}], class [ServerboundCustomPayloadPacket]", packet2.getData().array().length, packet2.getName(), new Exception());
        }else if (p_129521_ instanceof ClientboundCustomPayloadPacket packet2){
            k_multi_threading$logger.debug("len [{}], name [{}], class [ClientboundCustomPayloadPacket]", packet2.getData().array().length, packet2.getName(), new Exception());
        }else if (p_129521_ instanceof ServerboundCustomQueryPacket packet2){
            if (packet2.getData() != null) {
                k_multi_threading$logger.debug("name [{}], class [ServerboundCustomQueryPacket]", packet2.getName(), new Exception());
            }else {
                k_multi_threading$logger.debug("len [{}], name [{}], class [ServerboundCustomQueryPacket]", packet2.getData().array().length, packet2.getName(), new Exception());
            }
        }else if (p_129521_ instanceof ClientboundCustomQueryPacket packet2){
            k_multi_threading$logger.debug("len [{}], name [{}], class [ClientboundCustomQueryPacket]", packet2.getData().array().length, packet2.getName(), new Exception());
        }else {
            k_multi_threading$logger.debug("class [{}]", p_129521_.getClass().getName(), new Exception());
        }

    }
}
