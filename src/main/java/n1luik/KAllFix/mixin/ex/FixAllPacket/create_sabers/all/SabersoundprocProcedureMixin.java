package n1luik.KAllFix.mixin.ex.FixAllPacket.create_sabers.all;

import n1luik.KAllFix.Imixin.all.IOldBoolean;
import net.createsabers.network.CreateSabersModVariables;
import net.createsabers.procedures.SabersoundprocProcedure;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SabersoundprocProcedure.class)
public class SabersoundprocProcedureMixin {
    @Unique
    private static boolean NOT_OLD = true;
    @Redirect(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;)V", at = @At(value = "INVOKE", target = "Lnet/createsabers/network/CreateSabersModVariables$MapVariables;syncData(Lnet/minecraft/world/level/LevelAccessor;)V"), remap = false)
    private static void fix1(CreateSabersModVariables.MapVariables instance, LevelAccessor world) {

    }
    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;)V", at = @At("RETURN"), remap = false)
    private static void fix2(Event event, LevelAccessor world, CallbackInfo ci) {
        CreateSabersModVariables.MapVariables mapVariables = CreateSabersModVariables.MapVariables.get(world);
        IOldBoolean mapVariables1 = (IOldBoolean) mapVariables;
        if (NOT_OLD){
            NOT_OLD = false;
            mapVariables1.KAllFix$setOld(mapVariables.sabersound);
            return;
        }
        if (mapVariables1.KAllFix$getOld() != mapVariables.sabersound) {
            CreateSabersModVariables.MapVariables.get(world).syncData(world);
            mapVariables1.KAllFix$setOld(mapVariables.sabersound);
        }
    }
}
