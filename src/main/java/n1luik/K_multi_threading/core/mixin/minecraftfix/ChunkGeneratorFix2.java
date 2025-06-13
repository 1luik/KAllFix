package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.K_multi_threading.core.Imixin.IToLongData1;
import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkGenerator.class, priority = Integer.MIN_VALUE + 1000)
public class ChunkGeneratorFix2 {
    @Inject(method = {
            "applyBiomeDecoration"
    }, at = {@At("RETURN"),
            @At(value = "INVOKE", target = "Lnet/minecraft/ReportedException;<init>(Lnet/minecraft/CrashReport;)V", ordinal = 2)})
    public void fix2(WorldGenLevel p_223087_, ChunkAccess p_223088_, StructureManager p_223089_, CallbackInfo ci){
        if (((IToLongData1) (Object)this).K_multi_threading$getIToLongData1() > 0 && p_223087_.getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig) {
            iWorldChunkLockedConfig.pop();
        }
    }
}
