package n1luik.KAllFix.forge.LoginProtectionMod.mixin;

import n1luik.KAllFix.forge.LoginProtectionMod.LoginProtectionModEvent;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.realms.DisconnectedRealmsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({DisconnectedScreen.class, DisconnectedRealmsScreen.class})
public class DisconnectedScreenMixin {
    @Inject(method = "<init>*", at = @At("RETURN"))
    public void impl1(CallbackInfo ci){
        LoginProtectionModEvent.LoginProtection = false;
    }
}
