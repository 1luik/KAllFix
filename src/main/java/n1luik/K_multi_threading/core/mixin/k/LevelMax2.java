package n1luik.K_multi_threading.core.mixin.k;

import net.minecraft.core.BlockPos;
import net.minecraft.server.commands.ForceLoadCommand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({Level.class})
public abstract class LevelMax2 implements LevelReader {
    @Override
    public int getMaxLocalRawBrightness(BlockPos p_46850_, int p_46851_) {
        return 15;
    }
}
