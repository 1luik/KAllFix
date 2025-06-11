package n1luik.K_multi_threading.core.mixin.k;

import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundStatusResponsePacket.class)
@Deprecated
public class ClientboundStatusResponsePacketDebug {
    @Unique
    private static final Logger k_multi_threading$logger3 = LoggerFactory.getLogger("[handleDisconnection-debug3]");
    @Inject(method = "<init>(Lnet/minecraft/network/protocol/status/ServerStatus;)V", at = @At("RETURN"))
    public void debug(ServerStatus status, CallbackInfo ci) {
        StackWalker.getInstance().forEach(v -> {
            k_multi_threading$logger3.info(v.toString());
        });
    }
}
