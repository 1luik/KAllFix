package n1luik.K_multi_threading.core.Imixin;

import n1luik.K_multi_threading.core.sync.GetterSyncNode;
import n1luik.K_multi_threading.core.sync.Sync;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface GetChunkTickTask {
    GetterSyncNode<Void, ChunkPos,ChunkAccess> getChunkTickTask();
    void setChunkTickTask(GetterSyncNode<Void,ChunkPos,ChunkAccess> set);
}
