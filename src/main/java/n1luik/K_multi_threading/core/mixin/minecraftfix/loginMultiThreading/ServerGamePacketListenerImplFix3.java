package n1luik.K_multi_threading.core.mixin.minecraftfix.loginMultiThreading;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = Integer.MAX_VALUE - 100)
@Deprecated
public class ServerGamePacketListenerImplFix3 {


    //@Redirect(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V", opcode = Opcodes.INVOKESTATIC))
    //public void fix1(){
    //}
}
