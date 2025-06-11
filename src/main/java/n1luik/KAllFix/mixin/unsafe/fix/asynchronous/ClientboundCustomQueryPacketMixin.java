package n1luik.KAllFix.mixin.unsafe.fix.asynchronous;

import net.minecraft.Util;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ClientboundCustomQueryPacket.class)
public abstract class ClientboundCustomQueryPacketMixin implements Packet<ClientLoginPacketListener>, net.minecraftforge.network.ICustomPacket<ClientboundCustomQueryPacket>{
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void handle(ClientLoginPacketListener p_134754_) {
        Util.backgroundExecutor().execute(()->p_134754_.handleCustomQuery((ClientboundCustomQueryPacket)(Object)this));
    }
}
