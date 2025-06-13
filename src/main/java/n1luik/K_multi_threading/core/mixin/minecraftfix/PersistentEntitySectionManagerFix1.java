package n1luik.K_multi_threading.core.mixin.minecraftfix;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import n1luik.K_multi_threading.core.util.concurrent.ConcurrentInt2ObjectOpenHashMap;
import n1luik.K_multi_threading.core.util.concurrent.ConcurrentLong2ObjectOpenHashMap;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PersistentEntitySectionManager.class)
public class PersistentEntitySectionManagerFix1 {
    //@Redirect(method = "<init>", at = @At(value = "NEW", target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap;<init>()V", ordinal = 1, remap = false))
    //public Long2ObjectOpenHashMap fix1(){
    //    return new ConcurrentLong2ObjectOpenHashMap();
    //}
}
