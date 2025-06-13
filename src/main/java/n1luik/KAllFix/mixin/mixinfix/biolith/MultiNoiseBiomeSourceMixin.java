package n1luik.KAllFix.mixin.mixinfix.biolith;

import n1luik.KAllFix.fix.biolith.Fun_biolith;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MultiNoiseBiomeSource.class, priority = Integer.MAX_VALUE - 100)
public class MultiNoiseBiomeSourceMixin {
    @Unique
    private boolean KAllFix$fixBiolith = false;
    @Inject(method = "parameters", at = @At("HEAD"))
    public synchronized void fix1(CallbackInfoReturnable<Climate.ParameterList<Holder<Biome>>> cir) {
        if (KAllFix$fixBiolith)return;
        KAllFix$fixBiolith = true;
        Fun_biolith.fixBiomeSource(Base.mcs.registryAccess());
    }
}
