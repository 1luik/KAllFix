package n1luik.K_multi_threading.core.mixin.minecraftfix;

import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeFix1 {
    //由于是等待tick所以大概率会判外挂
    @Redirect(method = "handleBlockBreakAction", at = @At(value = "INVOKE", target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"))
    public boolean fix1(Object a, Object b){
        return true;
    }
}
