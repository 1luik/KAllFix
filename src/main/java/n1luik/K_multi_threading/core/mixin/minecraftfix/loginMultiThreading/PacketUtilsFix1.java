package n1luik.K_multi_threading.core.mixin.minecraftfix.loginMultiThreading;

import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.util.thread.BlockableEventLoop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PacketUtils.class)
public class PacketUtilsFix1 {
    @Redirect(method = "ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/thread/BlockableEventLoop;isSameThread()Z"))
    private static boolean fix1(BlockableEventLoop instance){
        return true;
    }
}
