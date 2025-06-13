package n1luik.KAllFix.forge.LoginProtectionMod.mixin;

import n1luik.KAllFix.forge.LoginProtectionMod.LoginProtectionModEvent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {
    @Inject(method = "init()V", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
        LoginProtectionModEvent.LoginProtection = true;
    }
    //@Inject(method = "lambda$init$0", at = @At("HEAD"))
    //public void impl1(Button b, CallbackInfo ci) {
    //    LoginProtectionModEvent.LoginProtection = false;
    //}
    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/Button;builder(Lnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/components/Button$OnPress;)Lnet/minecraft/client/gui/components/Button$Builder;"))
    public Button.Builder impl1(Component p_254439_, Button.OnPress p_254567_) {
        return Button.builder(p_254439_, v-> {
            LoginProtectionModEvent.LoginProtection = false;
            p_254567_.onPress(v);
        });
    }
}
