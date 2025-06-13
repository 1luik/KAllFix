package n1luik.K_multi_threading.core.mixin;

import n1luik.K_multi_threading.core.Imixin.ChunkLoadingManagementState;
import n1luik.K_multi_threading.core.Imixin.GetChunkTickTask;
import n1luik.K_multi_threading.core.Imixin.IChunkLoad;
import n1luik.K_multi_threading.core.sync.GetterSyncNode;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin implements GetChunkTickTask , IChunkLoad {
    GetterSyncNode<Void, ChunkPos, ChunkAccess> sync;
    ChunkLoadingManagementState state;

    @Override
    public GetterSyncNode<Void, ChunkPos, ChunkAccess> getChunkTickTask() {
        return sync;
    }

    @Override
    public void setChunkTickTask(GetterSyncNode<Void, ChunkPos, ChunkAccess> set) {
        sync = set;
    }

    @Override
    public ChunkLoadingManagementState getChunkLoadingManagementState() {
        return state;
    }

    @Override
    public void setChunkLoadingManagementState(ChunkLoadingManagementState state) {
        this.state = state;
    }
}
