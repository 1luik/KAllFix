package n1luik.K_multi_threading.core.mixin.debug.tacz;

import com.tacz.guns.entity.sync.core.SyncedDataKey;
import com.tacz.guns.entity.sync.core.SyncedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SyncedEntityData.class, remap = false)
@Deprecated
public class SyncedEntityDataDebug {


    @Inject(method = "set", at = @At("HEAD"), remap = false)
    public <E extends Entity, T> void debug1(E entity, SyncedDataKey<?, ?> key, T value, CallbackInfo ci){
        assert entity != null;
    }
}
