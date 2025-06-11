package n1luik.K_multi_threading.core.mixin.debug;

import com.mojang.datafixers.util.Either;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.IOWorker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IOWorker.class)
@Deprecated
public class IOWorkerDebug {
    @Inject(method = "m_223494_", at = @At("HEAD"), remap = false)
    public void debug1(ChunkPos p_156588_, CallbackInfoReturnable<Either> cir) {
        Base.LOGGER.debug("IOWorker.class {} {}", p_156588_.x, p_156588_.z);

    }

    @Inject(method = "loadAsync", at = @At("HEAD"))
    public void debug2(ChunkPos p_156588_, CallbackInfoReturnable<Either> cir) {
        Base.LOGGER.debug("IOWorker.class2 {} {}", p_156588_.x, p_156588_.z);//, new Exception());

    }
}
