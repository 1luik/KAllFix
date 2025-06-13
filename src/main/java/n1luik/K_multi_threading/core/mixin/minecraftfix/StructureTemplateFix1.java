package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureTemplate.class)
@Deprecated
public class StructureTemplateFix1 {
    @Unique
    boolean K_multi_threading$isChunkLocked;

    @Inject(method = "placeInWorld", at = @At("HEAD"))
    public void push(ServerLevelAccessor p_230329_, BlockPos p_230330_, BlockPos p_230331_, StructurePlaceSettings p_230332_, RandomSource p_230333_, int p_230334_, CallbackInfoReturnable<Boolean> cir){
        if (p_230329_.getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig) {
            K_multi_threading$isChunkLocked = iWorldChunkLockedConfig.pushThread() > 0;
        }
    }
    @Inject(method = "placeInWorld",at = @At("RETURN"))
    private void pop(ServerLevelAccessor p_230329_, BlockPos p_230330_, BlockPos p_230331_, StructurePlaceSettings p_230332_, RandomSource p_230333_, int p_230334_, CallbackInfoReturnable<Boolean> cir) {
        if (K_multi_threading$isChunkLocked && p_230329_.getChunkSource() instanceof IWorldChunkLockedConfig iWorldChunkLockedConfig) {
            iWorldChunkLockedConfig.pop();
        }
    }
}
