package n1luik.KAllFix.mixin.unsafe.path.MultiThreadingJEICommon;

import mezz.jei.common.util.ErrorUtil;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ErrorUtil.class)
public class ErrorUtilMixin {
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static void assertMainThread() {
    }
}
