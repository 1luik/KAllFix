package n1luik.KAllFix.forge.LoginProtectionMod.mixin;

import n1luik.KAllFix.forge.LoginProtectionMod.LoginProtectionModEvent;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraftforge.client.gui.ModMismatchDisconnectedScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModMismatchDisconnectedScreen.class)
public class ModMismatchDisconnectedScreenMixin {
    @Inject(method = "<init>*", at = @At("RETURN"))
    public void impl1(CallbackInfo ci){
        LoginProtectionModEvent.LoginProtection = false;
    }
}
