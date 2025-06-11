package n1luik.K_multi_threading.core.mixin.debug.gtceu;

import com.gregtechceu.gtceu.api.capability.IMedicalConditionTracker;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.HazardProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.forge.ForgeCommonEventListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.items.IItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ForgeCommonEventListener.class)
@Deprecated
public class ForgeCommonEventListenerDebug {
     @Unique
     private static final Logger k_multi_threading$logger = LoggerFactory.getLogger("[progressCondition-debug]");
     @Inject(method = "tickPlayerInventoryHazards", at = @At(value = "INVOKE", target = "Lcom/gregtechceu/gtceu/api/capability/IMedicalConditionTracker;progressRelatedCondition(Lcom/gregtechceu/gtceu/api/data/chemical/material/Material;)V", remap = false), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
     private static void debug1(TickEvent.PlayerTickEvent event, CallbackInfo ci, Player player, IMedicalConditionTracker tracker, IItemHandler inventory, int i, ItemStack stack, Material material) {

          if (com.gregtechceu.gtceu.common.data.GTMedicalConditions.CARCINOGEN == material.getProperty(PropertyKey.HAZARD).condition) {
               k_multi_threading$logger.info(stack.toString());
          }
     }
}
