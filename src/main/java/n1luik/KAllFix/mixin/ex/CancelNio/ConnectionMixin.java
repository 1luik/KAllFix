package n1luik.KAllFix.mixin.ex.CancelNio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import n1luik.KAllFix.Imixin.IConnection1;
import net.minecraft.network.Connection;
import net.minecraft.network.SkipPacketException;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;

@Mixin(Connection.class)
@Deprecated
public class ConnectionMixin implements IConnection1 {
    @Shadow private Channel channel;
    @Unique
    @Nullable
    ChannelFuture KAllFix$serverChannelFuture = null;
    @Override
    public void KAllFix$setServerChannelFuture(ChannelFuture cf) {
        this.KAllFix$serverChannelFuture = cf;
    }




    @Inject(method = "connectToServer", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void fix1(InetSocketAddress p_178301_, boolean p_178302_, CallbackInfoReturnable<Connection> cir, Connection connection, ChannelFuture channelfuture){
        ((IConnection1)connection).KAllFix$setServerChannelFuture(channelfuture);
    }
    @Inject(method = "exceptionCaught", at = @At("RETURN"))
    public void fix1(ChannelHandlerContext p_129533_, Throwable p_129534_, CallbackInfo ci){
        if (!(p_129534_ instanceof SkipPacketException) && this.KAllFix$serverChannelFuture != null && !this.KAllFix$serverChannelFuture.isCancelled()) {
            //channel.close();
            this.KAllFix$serverChannelFuture.cancel(true);
            this.KAllFix$serverChannelFuture = null;
        }
    }



}
