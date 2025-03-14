package n1luik.KAllFix.mixin.ex.FixAllPacket.createsabers.inject;

import n1luik.KAllFix.forge.ModInit;
import n1luik.KAllFix.forge.fixpack.createsabers.Event;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModInit.class)
public class This {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.register(Event.class);

    }
}
