package n1luik.K_multi_threading.core.mixin.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Deprecated
@Mixin(RandomizableContainerBlockEntity.class)
public abstract class ContainerDebug extends BlockEntity{

    public ContainerDebug(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Inject(method = "stillValid",at = @At(value = "HEAD"))
    private void debug1(Player p_59619_, CallbackInfoReturnable<Boolean> cir){

        Level level = getLevel();
        BlockPos blockpos = this.getBlockPos();
        if (level == null) {
            System.out.printf("Container1 %s %n",false);
        } else if (level.getBlockEntity(blockpos) != ((RandomizableContainerBlockEntity)(Object)this)) {
            System.out.printf("Container2 %s %n",false);
        } else {
            System.out.printf("Container3 %s %n",p_59619_.distanceToSqr((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D) <= (double)(8 * 8));
        }

    }
}
