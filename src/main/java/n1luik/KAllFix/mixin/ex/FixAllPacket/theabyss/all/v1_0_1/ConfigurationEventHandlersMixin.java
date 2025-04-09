package n1luik.KAllFix.mixin.ex.FixAllPacket.theabyss.all.v1_0_1;

import n1luik.KAllFix.forge.fixpack.theabyss.TheabyssBuf;
import n1luik.KAllFix.util.OB1;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.Event;
import net.yezon.theabyss.configuration.ConfigConfiguration;
import net.yezon.theabyss.eventhandlers.ConfigurationEventHandlers;
import net.yezon.theabyss.network.TheabyssModVariables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nullable;

import static n1luik.KAllFix.forge.fixpack.theabyss.All.PLAYER_VARIABLES_CAPABILITY_OLD;

@Mixin(ConfigurationEventHandlers.class)
public class ConfigurationEventHandlersMixin {

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private static void execute(@Nullable Event event, Entity entity) {
        if (entity != null) {
            double _setval =
                    ConfigConfiguration.SOMNIUM_XPOS.get();
            double _setval2 =
                    ConfigConfiguration.SOMNIUM_YPOS.get();
            entity.getCapability(TheabyssModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent((capability) -> {
                capability.HudX = _setval;
                capability.HudY = _setval2;
                LazyOptional<OB1<CompoundTag>> capability1 = entity.getCapability(PLAYER_VARIABLES_CAPABILITY_OLD, null);
                capability1.ifPresent(o-> {
                    Tag t1 = o.getT1();
                    CompoundTag tag = (CompoundTag)capability.writeNBT();
                    TheabyssBuf.optimize1.getAndAdd(1);
                    try {
                        if (t1 == null) {
                            capability.syncPlayerVariables(entity);
                            o.setT1(tag);
                        } else {
                            if (!t1.equals(tag)) {
                                capability.syncPlayerVariables(entity);
                                o.setT1(tag);
                            }
                        }
                    }finally {
                        TheabyssBuf.optimize1.getAndAdd(-1);
                    }
                });
            });
        }
    }
}
