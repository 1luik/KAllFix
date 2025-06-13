package n1luik.K_multi_threading.core.mixin.minecraftfix;


import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChunkGenerator.class)
public class ChunkGeneratorFix3 {
    @Redirect(method = "getStructureGeneratingAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelReader;getChunk(IILnet/minecraft/world/level/chunk/ChunkStatus;)Lnet/minecraft/world/level/chunk/ChunkAccess;"))
    private static ChunkAccess fix1(LevelReader instance, int p_46820_, int p_46821_, ChunkStatus p_46822_){
        if (instance instanceof ParaServerChunkProvider paraServerChunkProvider) {
            return paraServerChunkProvider.generatorGetChunk(p_46820_, p_46821_, p_46822_, true);
        }
        if (instance instanceof ServerLevel serverLevel) {
            if (serverLevel.getChunkSource() instanceof ParaServerChunkProvider paraServerChunkProvider) {
                return paraServerChunkProvider.generatorGetChunk(p_46820_, p_46821_, p_46822_, true);
            }
        }
        return instance.getChunk(p_46820_, p_46821_, p_46822_);
    }
}
