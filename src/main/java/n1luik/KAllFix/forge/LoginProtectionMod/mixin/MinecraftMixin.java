package n1luik.KAllFix.forge.LoginProtectionMod.mixin;

import n1luik.KAllFix.forge.LoginProtectionMod.LoginProtectionModEvent;
import n1luik.KAllFix.forge.LoginProtectionMod.LoginProtectionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.realms.DisconnectedRealmsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow @Nullable public Screen screen;

    @Shadow public abstract void setScreen(@org.jetbrains.annotations.Nullable Screen p_91153_);

    //@Inject(method = "lambda$createSearchTrees$10", at = @At("HEAD"))
    //public void impl2(CallbackInfo ci){
//
    //}
    @Inject(method = "tick", at = @At("HEAD"))
    public void impl1(CallbackInfo ci){
        if (LoginProtectionModEvent.LoginProtection) {
            if (!(screen instanceof ConnectScreen) && !(screen instanceof ReceivingLevelScreen) && !(screen instanceof DisconnectedScreen) && !(screen instanceof DisconnectedRealmsScreen)) {
                setScreen(new LoginProtectionScreen());
            }
        }
    }
}
