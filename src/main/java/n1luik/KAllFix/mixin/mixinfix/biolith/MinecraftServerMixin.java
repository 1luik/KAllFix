package n1luik.KAllFix.mixin.mixinfix.biolith;

import n1luik.KAllFix.fix.biolith.Fun_biolith;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinecraftServer.class, priority = Integer.MAX_VALUE-3)
public abstract class MinecraftServerMixin {
    @Shadow public abstract RegistryAccess.Frozen registryAccess();

    @Inject(method = "loadLevel", at = @At("HEAD"))
    public void fix1(CallbackInfo ci) {
        Fun_biolith.fixBiomeSource(registryAccess());
    }
}
