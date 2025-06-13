package n1luik.KAllFix.mixin.ex.FixAllPacket.create_sabers.all;

import n1luik.KAllFix.Imixin.all.IOldBoolean;
import net.createsabers.network.CreateSabersModVariables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CreateSabersModVariables.MapVariables.class)
public class CreateSabersModVariables_MapVariablesMixin implements IOldBoolean {
    @Unique
    boolean KAllFix$old;
    @Override
    public boolean KAllFix$getOld() {
        return KAllFix$old;
    }

    @Override
    public void KAllFix$setOld(boolean b) {
        KAllFix$old = b;;
    }
}
