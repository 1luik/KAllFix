package n1luik.KAllFix.mixin.ex.FixAllPacket.theabyss.all.v1_0_1$5;

import n1luik.KAllFix.forge.fixpack.theabyss.TheabyssBuf;
import net.minecraft.world.entity.Entity;
import net.yezon.theabyss.network.TheabyssModVariables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TheabyssModVariables.PlayerVariables.class, remap = false)
public class TheabyssModVariables_PlayerVariablesMixin {
    @Inject(method = "syncPlayerVariables", at = @At("HEAD"), cancellable = true, remap = false)
    public void impl1(Entity entity, CallbackInfo ci){
        if (TheabyssBuf.optimize1.get() < 1)ci.cancel();
    }
}
