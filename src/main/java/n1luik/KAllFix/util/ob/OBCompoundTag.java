package n1luik.KAllFix.util.ob;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public final class OBCompoundTag {
    @Getter
    @Setter
    public CompoundTag t1;

    public OBCompoundTag() {
    }
    public OBCompoundTag(CompoundTag t1) {
        this.t1 = t1;
    }

    public CompoundTag t1() {
        return t1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (OBCompoundTag) obj;
        return Objects.equals(this.t1, that.t1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t1);
    }

    @Override
    public String toString() {
        return "OBCompoundTag[" +
                "t1=" + t1 + ']';
    }
}