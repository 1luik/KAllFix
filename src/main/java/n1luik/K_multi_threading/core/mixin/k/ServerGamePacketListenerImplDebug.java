package n1luik.K_multi_threading.core.mixin.k;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
@Deprecated
public class ServerGamePacketListenerImplDebug {
    @Unique
    private static final Logger k_multi_threading$logger4 = LoggerFactory.getLogger("[handleDisconnection-debug4]");
    @Inject(method = "disconnect", at = @At("HEAD"))
    public void debug(Component p_9943_, CallbackInfo ci) {
        StackWalker.getInstance().forEach(v -> {
            k_multi_threading$logger4.info(v.toString());
        });
    }

}
