package n1luik.K_multi_threading.core.mixin;

import n1luik.K_multi_threading.core.Imixin.GetChunkTickTask;
import n1luik.K_multi_threading.core.sync.ChunkTickSyncNode;
import n1luik.K_multi_threading.core.sync.GetterSyncNode;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Deprecated
@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin{
    @Shadow public abstract Level getLevel();

    @Inject(method = "<init>*",at = @At(value = "RETURN"))
    public void setChunkTickTask() {

        ((GetChunkTickTask)this).setChunkTickTask(new ChunkTickSyncNode(getLevel(),null));
    }
}
