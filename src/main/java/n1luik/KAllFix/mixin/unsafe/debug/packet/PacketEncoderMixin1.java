package n1luik.KAllFix.mixin.unsafe.debug.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.PacketEncoder;
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

@Mixin(PacketEncoder.class)
public class PacketEncoderMixin1 {
    @Unique
    private static final Logger k_multi_threading$logger = LoggerFactory.getLogger("[unsafe.debug.packet-size]");
    @Inject(method = "encode(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;Lio/netty/buffer/ByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/jfr/JvmProfiler;onPacketSent(IILjava/net/SocketAddress;I)V"))
    public void debug1(ChannelHandlerContext p_130545_, Packet<?> p_130546_, ByteBuf p_130547_, CallbackInfo ci){
        
        if (p_130546_ instanceof ServerboundCustomPayloadPacket packet2){
            k_multi_threading$logger.debug("len [{}], name [{}], class [ServerboundCustomPayloadPacket]", p_130547_.writerIndex(), packet2.getName(), new Exception());
        }else if (p_130546_ instanceof ClientboundCustomPayloadPacket packet2){
            k_multi_threading$logger.debug("len [{}], name [{}], class [ClientboundCustomPayloadPacket]", p_130547_.writerIndex(), packet2.getName(), new Exception());
        }else if (p_130546_ instanceof ServerboundCustomQueryPacket packet2){
            if (packet2.getData() != null) {
                k_multi_threading$logger.debug("name [{}], class [ServerboundCustomQueryPacket]", packet2.getName(), new Exception());
            }else {
                k_multi_threading$logger.debug("len [{}], name [{}], class [ServerboundCustomQueryPacket]", p_130547_.writerIndex(), packet2.getName(), new Exception());
            }
        }else if (p_130546_ instanceof ClientboundCustomQueryPacket packet2){
            k_multi_threading$logger.debug("len [{}], name [{}], class [ClientboundCustomQueryPacket]", p_130547_.writerIndex(), packet2.getName(), new Exception());
        }else {
            k_multi_threading$logger.debug("len [{}], class [{}]", p_130547_.writerIndex(), p_130546_.getClass().getName(), new Exception());
        }
    }

}
