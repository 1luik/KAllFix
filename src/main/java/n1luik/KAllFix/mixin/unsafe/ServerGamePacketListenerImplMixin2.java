package n1luik.KAllFix.mixin.unsafe;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin2 {
    @Redirect(method = {"handleMoveVehicle", "handleMovePlayer"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;isSingleplayerOwner()Z"))
    public boolean remove(ServerGamePacketListenerImpl instance) {
        return true;
    }
}
