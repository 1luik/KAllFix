package n1luik.K_multi_threading.core.mixin.fix.create;

import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemVaultBlockEntity.class, remap = false)
public abstract class ItemVaultBlockEntityFix1 {
    @Shadow protected abstract void initCapability();

    @Unique
    private static final Object K_multi_threading$ALL_BLOCK_INIT = new Object();
    @Redirect(method = "getCapability", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/vault/ItemVaultBlockEntity;initCapability()V", remap = false), remap = false)
    private void K_multi_threading$initCapability(ItemVaultBlockEntity instance) {
        synchronized (K_multi_threading$ALL_BLOCK_INIT){
            initCapability();
        }
    }
}
