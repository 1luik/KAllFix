package n1luik.K_multi_threading.core.mixin.minecraftfix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/server/level/ServerLevel$EntityCallbacks")
public class ServerLevel_EntityCallbacksFix1 {

    @Redirect(method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE",target = "Lnet/minecraft/Util;logAndPauseIfInIde(Ljava/lang/String;Ljava/lang/Throwable;)V"))
    private void fix1(String p_200891_, Throwable p_200892_){
    }
    @Redirect(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE",target = "Lnet/minecraft/Util;logAndPauseIfInIde(Ljava/lang/String;Ljava/lang/Throwable;)V"))
    private void fix2(String p_200891_, Throwable p_200892_){
    }
}
