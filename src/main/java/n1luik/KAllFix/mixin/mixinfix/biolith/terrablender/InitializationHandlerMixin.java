package n1luik.KAllFix.mixin.mixinfix.biolith.terrablender;

import n1luik.KAllFix.fix.biolith.Fun_biolith;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrablender.handler.InitializationHandler;

@Mixin(value = InitializationHandler.class, priority = Integer.MAX_VALUE-3)
public class InitializationHandlerMixin {
    @Inject(method = "onServerAboutToStart", at = @At("HEAD"), remap = false)
    private static void fix1(ServerAboutToStartEvent event, CallbackInfo ci) {
        Fun_biolith.fixBiomeSource(event.getServer().registryAccess());
    }
}
