package n1luik.K_multi_threading.core.mixin.minecraftfix.loginMultiThreading;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.DistanceManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(DistanceManager.class)
public class DistanceManagerFix1 {
    @Inject(method = "removePlayer", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectSet;remove(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void updatePlayer(SectionPos p_140829_, ServerPlayer p_140830_, CallbackInfo ci, ChunkPos chunkpos, long i, ObjectSet objectset) {
        if (objectset == null) {
            ci.cancel();
        }
    }
}
