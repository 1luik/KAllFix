package n1luik.K_multi_threading.core.mixin.fix.create;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import n1luik.K_multi_threading.fix.create.MultiThreadingMechanicalCrafterBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CreateRegistrate.class, remap = false)
public class CreateRegistrateFix1 extends AbstractRegistrate<CreateRegistrate> {
    protected CreateRegistrateFix1(String modid) {
        super(modid);
    }

    @Inject(method = "blockEntity(Ljava/lang/String;Lcom/tterrag/registrate/builders/BlockEntityBuilder$BlockEntityFactory;)Lcom/simibubi/create/foundation/data/CreateBlockEntityBuilder;", remap = false, at = @At("HEAD"), cancellable = true)
    public void blockEntity(String par1, BlockEntityBuilder.BlockEntityFactory par2, CallbackInfoReturnable<BlockEntityBuilder> cir) {
        if (par1.equals("mechanical_crafter")){
            cir.setReturnValue(this.blockEntity(this.self(), par1, MultiThreadingMechanicalCrafterBlockEntity::new));
        }
    }
}
