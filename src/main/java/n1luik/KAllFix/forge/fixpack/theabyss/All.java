package n1luik.KAllFix.forge.fixpack.theabyss;

import n1luik.KAllFix.util.OB1;
import n1luik.KAllFix.util.ob.OBCompoundTag;
import n1luik.K_multi_threading.core.Base;
import net.mcreator.mekanismexplosives.MekanismexplosivesMod;
import net.mcreator.mekanismexplosives.network.MekanismexplosivesModVariables;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.yezon.theabyss.network.TheabyssModVariables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class All {
    //用于备份检查是否修改
    public static final ResourceLocation PLAYER_VARIABLES_CAPABILITY_OLD_ID = new ResourceLocation(Base.MOD_ID2, "theabyss/player_variables_old");
    public static final Capability<OBCompoundTag> PLAYER_VARIABLES_CAPABILITY_OLD = CapabilityManager.get(new CapabilityToken<>() {
    });
    private static class ImplClass implements ICapabilitySerializable<Tag> {
        public final OBCompoundTag tag = new OBCompoundTag(null);
        public final LazyOptional<OBCompoundTag> tag2 = LazyOptional.of(()->tag);


        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
            return cap == PLAYER_VARIABLES_CAPABILITY_OLD ? tag2.cast() : LazyOptional.empty();
        }

        public Tag serializeNBT() {
            return new CompoundTag();
        }

        public void deserializeNBT(Tag nbt) {
        }



        ;
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<?> event) {
        if (event.getObject() instanceof ServerPlayer) {
            event.addCapability(PLAYER_VARIABLES_CAPABILITY_OLD_ID, new ImplClass());
        }
    }
    //AttachCapabilitiesEvent
}


