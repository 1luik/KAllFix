package n1luik.KAllFix.mixin.mixinfix.destroy;

import com.petrolpark.destroy.badge.BadgeHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.CompletableFuture;

@Mixin(BadgeHandler.class)
public class BadgeHandlerMixin {
    @Redirect(method = "getAndAddBadges", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;join()Ljava/lang/Object;"), remap = false)
    private static Object fix1(CompletableFuture<?> future) {
    return future.getNow(null);
    }
}
