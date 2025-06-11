package n1luik.K_multi_threading.core.mixin.k;

import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
@Deprecated
public class ConnectionDebug {
    @Unique
    private static final Logger k_multi_threading$logger = LoggerFactory.getLogger("[handleDisconnection-debug]");
    @Unique
    private static final Logger k_multi_threading$logger2 = LoggerFactory.getLogger("[handleDisconnection-debug2]");
    @Shadow private Channel channel;

    //@Inject(method = "handleDisconnection", at = @At("HEAD"))
    //public void debug1(CallbackInfo ci){
    //    if (this.channel != null && !this.channel.isOpen()) {
    //        if (this.channel != null){
    //            k_multi_threading$logger.info("this.channel.isOpen: {}", this.channel.isOpen());
    //            k_multi_threading$logger.info("this.channel type: [{}]", this.channel.getClass().getName());
    //        }else {
    //            k_multi_threading$logger.info("this.channel is null");
    //        }
    //        StackWalker.getInstance().forEach(v -> {
    //            k_multi_threading$logger.info(v.toString());
    //        });
    //    }else {
    //        StackWalker.getInstance().forEach(v -> {
    //            k_multi_threading$logger.debug(v.toString());
    //        });
    //    }
    //}
    @Inject(method = "disconnect", at = @At("HEAD"))
    public void debug2(CallbackInfo ci){
        StackWalker.getInstance().forEach(v -> {
            k_multi_threading$logger2.info(v.toString());
        });
    }
}
