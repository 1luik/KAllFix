package n1luik.K_multi_threading.neoforge.mixin.debug;

import n1luik.K_multi_threading.core.Base;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Aquifer;
import org.spongepowered.asm.mixin.*;

@Mixin(value = Aquifer.NoiseBasedAquifer.class)
public abstract class NoiseBasedAquiferMixin {
    @Shadow protected abstract int gridX(int p_158040_);

    @Shadow protected abstract int gridY(int p_158046_);

    @Shadow protected abstract int gridZ(int p_158048_);

    @Shadow protected abstract int getIndex(int p_158028_, int p_158029_, int p_158030_);

    @Shadow @Final protected Aquifer.FluidStatus[] aquiferCache;

    @Shadow protected abstract Aquifer.FluidStatus computeFluid(int p_188448_, int p_188449_, int p_188450_);

    @Overwrite
    private Aquifer.FluidStatus getAquiferStatus(long packedPos) {
        if (packedPos == Long.MIN_VALUE || packedPos == Long.MAX_VALUE || packedPos == 0L) {
            Base.LOGGER.warn("⚠️ INVALID POS in Aquifer: packedPos=" + packedPos, new Exception());
        }

        int i = BlockPos.getX(packedPos);
        int j = BlockPos.getY(packedPos);
        int k = BlockPos.getZ(packedPos);

        // DEBUG: 打印异常值
        if (i < -1000000 || i > 1000000 || j < -1000000 || j > 1000000 || k < -1000000 || k > 1000000) {
            Minecraft.getInstance().execute(() -> {
                Base.LOGGER.warn("⚠️ INVALID POS in Aquifer: packedPos=" + packedPos + ", x=" + i + ", y=" + j + ", z=" + k, new Exception());
                // 可选：打印堆栈
            });
        }
        int l = this.gridX(i);
        int i1 = this.gridY(j);
        int j1 = this.gridZ(k);
        int k1 = this.getIndex(l, i1, j1);
        Aquifer.FluidStatus aquifer$fluidstatus = this.aquiferCache[k1];
        if (aquifer$fluidstatus != null) {
            return aquifer$fluidstatus;
        } else {
            Aquifer.FluidStatus aquifer$fluidstatus1 = this.computeFluid(i, j, k);
            this.aquiferCache[k1] = aquifer$fluidstatus1;
            return aquifer$fluidstatus1;
        }
    }
}