package n1luik.K_multi_threading.core.Imixin;

import java.util.Objects;

public record ChunkLoadLevelBuff(long id, int level) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkLoadLevelBuff that = (ChunkLoadLevelBuff) o;
        return id == that.id && level == that.level;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >> 32)) ^ level;
    }
}
