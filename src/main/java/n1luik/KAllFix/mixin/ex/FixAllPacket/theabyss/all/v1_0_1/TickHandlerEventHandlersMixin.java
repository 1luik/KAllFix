package n1luik.KAllFix.mixin.ex.FixAllPacket.theabyss.all.v1_0_1;

import net.yezon.theabyss.eventhandlers.TickHandlerEventHandlers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TickHandlerEventHandlers.class)
@Deprecated
public class TickHandlerEventHandlersMixin {
    @Redirect(method = {
            "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V",
            "lambda$execute$0",
            "lambda$execute$1",
            "lambda$execute$2",
            "lambda$execute$3",
            "lambda$execute$4",
            "lambda$execute$5"
    }, at = @At(value = "INVOKE", target = "Lnet/yezon/theabyss/network/TheabyssModVariables$PlayerVariables;syncPlayerVariables(Lnet/minecraft/world/entity/Entity;)V", remap = false), remap = false)
    private static void fix1(){

    }
}
