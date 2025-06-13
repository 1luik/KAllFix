package n1luik.K_multi_threading.core.mixin.JProfiler;

import n1luik.K_multi_threading.core.Imixin.JProfiler;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class serverRunTick {
    @Inject(method = "runServer",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/MinecraftServer;buildServerStatus()Lnet/minecraft/network/protocol/status/ServerStatus;"))
    public void runServer(CallbackInfo ci) {
        JProfiler.serverRunTick();
    }
}
