package n1luik.K_multi_threading.core.sync;

import n1luik.K_multi_threading.core.Imixin.GetChunkTickTask;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.function.Function;

public class EntityTickSyncNode extends GetterSyncNode<Void, ChunkPos, Entity>{

    public EntityTickSyncNode(LevelReader map, Node<GetterSyncNode.runFun<Function<ChunkPos, GetterSyncNode<?, ?, ?>>, Entity, Void>> n) {
        super(new runFun()
                , chunkPos -> ((GetChunkTickTask) map.getChunk(chunkPos.x << 4, chunkPos.z << 4)).getChunkTickTask(), n);
    }

    public static class runFun implements GetterSyncNode.runFun<Function<ChunkPos, GetterSyncNode<?, ?, ?>>, Entity, Void>{

        @Override
        public Void apply(GetterSyncNode<?,?,?> base, Function<ChunkPos, GetterSyncNode<?, ?, ?>> o, Entity o2) {
            throw new RuntimeException("这是一个不可运作的占位符");
        }
    }
}
