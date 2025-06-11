package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.K_multi_threading.core.Imixin.IToData1;
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

@Mixin(value = ChunkGenerator.class, priority = Integer.MAX_VALUE - 1000)
public class ChunkGeneratorFix1 implements IToLongData1{
    long IToLongData1 = -1;

    @Inject(method = {
            "applyBiomeDecoration"
    }, at = @At("HEAD"))
    public void fix1(WorldGenLevel p_223087_, ChunkAccess p_223088_, StructureManager p_223089_, CallbackInfo ci){
        if (p_223087_.getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig) {
            IToLongData1 = iWorldChunkLockedConfig.pushThread();
        }
    }

    @Override
    public long K_multi_threading$getIToLongData1() {
        return IToLongData1;
    }
}
