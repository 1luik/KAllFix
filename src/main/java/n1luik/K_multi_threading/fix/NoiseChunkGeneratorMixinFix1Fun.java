package n1luik.K_multi_threading.fix;

import net.minecraft.world.level.chunk.LevelChunkSection;

public class NoiseChunkGeneratorMixinFix1Fun {
    //用于复制net.minecraft.world.level.chunk.ChunkAccess#getSections的输出防止输出的数组被修改
    public static LevelChunkSection[] fun1(LevelChunkSection[] array){
        return array.clone();
    }
}
