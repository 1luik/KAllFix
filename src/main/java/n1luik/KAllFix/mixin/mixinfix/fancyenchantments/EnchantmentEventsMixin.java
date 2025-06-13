package n1luik.KAllFix.mixin.mixinfix.fancyenchantments;

import com.foolsix.fancyenchantments.events.EnchantmentEvents;
import n1luik.KAllFix.data.fancyenchantments.UtilData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentEvents.class)
public class EnchantmentEventsMixin {
    @Redirect(method = "livingTickEvent", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/entity/living/LivingEvent$LivingTickEvent;isCanceled()Z", remap = false), remap = false)
    public boolean fix1(LivingEvent.LivingTickEvent instance) {
        LivingEntity entity = instance.getEntity();
        if (!instance.isCanceled() && entity != null){
            Integer i = entity.getEntityData().get(UtilData.fancyenchantments_up_tag);
            if (i > 0) {

                entity.getEntityData().set(UtilData.fancyenchantments_up_tag, i-1);
                return true;
            }

        }
        return false;
    }
}
