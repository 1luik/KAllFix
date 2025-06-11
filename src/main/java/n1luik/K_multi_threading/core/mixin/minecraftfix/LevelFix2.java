package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.K_multi_threading.core.base.ConcurrentCollectingNeighborUpdater;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(value = Level.class, priority = Integer.MAX_VALUE)
@Deprecated
public class LevelFix2 {
    @Mutable
    @Shadow @Final public NeighborUpdater neighborUpdater;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void fix1(WritableLevelData p_270739_, ResourceKey p_270683_, RegistryAccess p_270200_, Holder p_270240_, Supplier p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_, CallbackInfo ci) {
        neighborUpdater = new ConcurrentCollectingNeighborUpdater((Level) (Object) this, p_270466_);
    }

}
