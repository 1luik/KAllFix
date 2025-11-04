package n1luik.KAllFix.mixin.unsafe.path.ChunkBreedingControlSizeEnable;

import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.entity.EntitySection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Animal.class, priority = Integer.MAX_VALUE)
public abstract class AnimalMixin extends AgeableMob {
    @Unique
    private static final int CHUNK_BREEDING_CONTROL_SIZE_ENABLE = Integer.getInteger("KAF-ChunkBreedingControlSize", 0);

    protected AnimalMixin(EntityType<? extends AgeableMob> p_146738_, Level p_146739_) {
        super(p_146738_, p_146739_);
    }

    @Inject(method = "canMate", at = @At("RETURN"), cancellable = true)
    private void impl1(Animal otherAnimal, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            if (level instanceof ServerLevel serverLevel) {
                int x = (int) getX();
                int z = (int) getZ();
                int minBuildHeight = serverLevel.getMinBuildHeight() / 16;
                int maxBuildHeight = serverLevel.getMaxBuildHeight() / 16;
                int size = 0;
                for (int i = minBuildHeight; i <= maxBuildHeight; i++) {
                    EntitySection<Entity> section = serverLevel.entityManager.sectionStorage.getSection(SectionPos.asLong(x >> 4, i, z >> 4));
                    if (section != null && !section.isEmpty() && (size += section.size()) > CHUNK_BREEDING_CONTROL_SIZE_ENABLE) {
                        cir.setReturnValue(false);
                        break;
                    }

                }
            }
        }
    }
}
