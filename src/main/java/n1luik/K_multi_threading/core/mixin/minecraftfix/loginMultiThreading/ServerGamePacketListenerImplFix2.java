package n1luik.K_multi_threading.core.mixin.minecraftfix.loginMultiThreading;

import n1luik.K_multi_threading.core.util.TruePacketThreadTest;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplFix2 {
    @Shadow @Final private MinecraftServer server;

    @Redirect(method = "disconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;executeBlocking(Ljava/lang/Runnable;)V"))
    public void fix1(MinecraftServer instance, Runnable runnable){
        instance.execute(()->{
            instance.executeBlocking(runnable);
        });
    }
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;doTick()V"))
    public void fix2(ServerPlayer instance){
        server.execute(instance::doTick);
    }


    @Redirect(method = {
            "handleClientCommand",
            "handleUseItemOn",
            "handleUseItem",
            "handleMovePlayer"
    }, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V"))
    public <T extends PacketListener> void fix3(Packet<T> p_131360_, T p_131361_, ServerLevel p_131362_){
        TruePacketThreadTest.ensureRunningOnSameThread(p_131360_, p_131361_, p_131362_);
    }
}
