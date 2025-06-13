package n1luik.KAllFix.mixin.unsafe.debug;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.channels.ClosedChannelException;

@Mixin(Connection.class)
public abstract class ConnectionMixin {
    @Unique
    boolean KAllFix$isConnectedOut = false;
    @Shadow @Final private static Logger LOGGER;

    @Shadow public abstract boolean isConnected();

    @Inject(method = "exceptionCaught", at = @At("HEAD"))
    public void debug1(ChannelHandlerContext p_129533_, Throwable p_129534_, CallbackInfo ci){
        if (p_129534_ instanceof ClosedChannelException && KAllFix$isConnectedOut){
            return;
        }
        if (isConnected()){
            KAllFix$isConnectedOut = true;
        }
        LOGGER.info("exceptionCaught", p_129534_);
    }
}
