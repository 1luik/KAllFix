package n1luik.K_multi_threading.core.asm;

import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.Imixin.GetChunkTickTask;
import n1luik.K_multi_threading.core.sync.GetterSyncNode;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.chunk.ChunkAccess;

@Deprecated
public class SyncImplGetterChunk_ASMCall {
    public static void addChunkSync1(ChunkAccess access, ServerChunkCache serverChunkCache){
        if (access == null)return;
        if (Thread.currentThread() instanceof Base.ForkJoinWorkerThread_ t){
            GetterSyncNode<?,?,?> o = (GetterSyncNode<?,?,?>)t.getDataMap().get(t.hashCode());
            if (o == null) return;
            o.wait(((GetChunkTickTask)access).getChunkTickTask());
        }
    }
}
