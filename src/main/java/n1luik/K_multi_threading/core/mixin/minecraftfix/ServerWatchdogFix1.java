package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.K_multi_threading.core.util.Util;
import net.minecraft.server.dedicated.ServerWatchdog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.management.ThreadInfo;

@Mixin(ServerWatchdog.class)
public class ServerWatchdogFix1 {
    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(Ljava/lang/Object;)Ljava/lang/StringBuilder;", ordinal = 0))
    public StringBuilder fix1(StringBuilder instance, Object obj){

        return instance.append(Util.notMaxThreadInfoToString((ThreadInfo) obj, Integer.MAX_VALUE));
    }
}
