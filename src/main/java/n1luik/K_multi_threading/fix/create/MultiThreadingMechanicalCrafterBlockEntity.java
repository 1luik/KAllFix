package n1luik.K_multi_threading.fix.create;

import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.content.kinetics.crafter.RecipeGridHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class MultiThreadingMechanicalCrafterBlockEntity extends MechanicalCrafterBlockEntity {
    public MultiThreadingMechanicalCrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    private static final Object groupLock = new Object();
    private static final Object linkLock = new Object();
    @Nullable
    protected Object thisLock = new Object();

    /**
     * 警告这个不支持多线程
     * */
    protected void K_multi_threading$Up(MultiThreadingMechanicalCrafterBlockEntity src){
        thisLock = src.thisLock;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        synchronized (groupLock){
            MechanicalCrafterBlockEntity targetingCrafter = RecipeGridHandler.getTargetingCrafter(this);
            if (targetingCrafter != null && targetingCrafter instanceof MultiThreadingMechanicalCrafterBlockEntity e){
                e.K_multi_threading$Up(this);
            }
        }
    }

    @Override
    public void ejectWholeGrid() {
        synchronized (linkLock){
            super.ejectWholeGrid();
        }
    }

    @Override
    public void tick() {
        synchronized (thisLock){
            super.tick();
        }
    }
}
