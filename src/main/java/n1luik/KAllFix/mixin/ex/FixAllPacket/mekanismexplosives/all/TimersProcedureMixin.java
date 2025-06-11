//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package n1luik.KAllFix.mixin.ex.FixAllPacket.mekanismexplosives.all;

import net.mcreator.mekanismexplosives.network.MekanismexplosivesModVariables;
import net.mcreator.mekanismexplosives.procedures.TimersProcedure;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
    value = {TimersProcedure.class},
    remap = false
)
public class TimersProcedureMixin {
    public TimersProcedureMixin() {
    }

    @Redirect(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;)V", at = @At(value = "INVOKE", target = "Lnet/mcreator/mekanismexplosives/network/MekanismexplosivesModVariables$MapVariables;syncData(Lnet/minecraft/world/level/LevelAccessor;)V"), remap = false)
    private static void fix1(MekanismexplosivesModVariables.MapVariables instance, LevelAccessor world) {

    }
}
