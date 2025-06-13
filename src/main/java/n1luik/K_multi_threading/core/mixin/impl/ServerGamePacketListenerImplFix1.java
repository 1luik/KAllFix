package n1luik.K_multi_threading.core.mixin.impl;

import n1luik.K_multi_threading.core.util.WaitCall;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Deprecated
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplFix1 {
    @Shadow @Final private MinecraftServer server;

    @Redirect(method = "handlePlayerAction", at = @At(value = "INVOKE",target = "Lnet/minecraft/server/level/ServerPlayerGameMode;handleBlockBreakAction(Lnet/minecraft/core/BlockPos;Lnet/minecraft/network/protocol/game/ServerboundPlayerActionPacket$Action;Lnet/minecraft/core/Direction;II)V"))
    public void fix1(ServerPlayerGameMode instance, BlockPos f, ServerboundPlayerActionPacket.Action blockstate, Direction f1, int j, int blockstate1){
        ((WaitCall<Object>)server).executeTask(()-> {
            instance.handleBlockBreakAction(f, blockstate, f1, j, blockstate1);
        });

    }
}
