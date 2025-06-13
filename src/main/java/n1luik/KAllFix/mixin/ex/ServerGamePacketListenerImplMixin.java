package n1luik.KAllFix.mixin.ex;

import net.minecraft.Util;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    //@ModifyConstant(method = "tick", constant = @Constant(longValue = 15000L))
    //public long fix1(long constant){
    //    return Long.getLong("KAF-ClientboundKeepAlivePacket_Max", 15000L);
    //}
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getMillis()J", ordinal = 0))
    public long fix2(){
        return Util.getMillis() - (Long.getLong("KAF-ClientboundKeepAlivePacket_Max", 15000L) + 15000L);
    }
}
