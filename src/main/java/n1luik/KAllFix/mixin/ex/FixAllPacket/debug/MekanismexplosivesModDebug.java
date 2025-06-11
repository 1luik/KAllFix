package n1luik.KAllFix.mixin.ex.FixAllPacket.debug;

import n1luik.KAllFix.util.DebugUtil;
import net.mcreator.mekanismexplosives.MekanismexplosivesMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MekanismexplosivesMod.class, remap = false)
@Deprecated
public class MekanismexplosivesModDebug {
    @Inject(method = "addNetworkMessage", at = @At("HEAD"), remap = false)
    private static void debug(CallbackInfo ci) {
        DebugUtil.debugPos("MekanismexplosivesMod1");
    }
}
