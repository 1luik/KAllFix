package n1luik.KAllFix.mixin.mixinfix;

import n1luik.KAllFix.util.ModTags;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin2 {
    @Inject(method = "tickServer", at = @At("HEAD"))
    public void addTag(CallbackInfo ci) {
        ModTags.StartServerTag = true;
    }
}
