package n1luik.KAllFix.mixin.unsafe.gtceu;

import com.gregtechceu.gtceu.api.data.medicalcondition.MedicalCondition;
import com.gregtechceu.gtceu.common.capability.MedicalConditionTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**不知道为什么会一直给辐射只能把他扣掉*/
@Mixin(value = MedicalConditionTracker.class, remap = false)
public class MedicalConditionTrackerMixin {
    //@Unique
    //private static final Logger k_multi_threading$logger = LoggerFactory.getLogger("[progressCondition-debug]");
    @Inject(method = "progressCondition", at = @At("HEAD"), remap = false, cancellable = true)
    public void debug1(MedicalCondition condition, float strength, CallbackInfo ci) {

        if (com.gregtechceu.gtceu.common.data.GTMedicalConditions.CARCINOGEN == condition) {
            ci.cancel();
            //    StackWalker.getInstance().forEach(v -> {
            //        k_multi_threading$logger.info(v.toString());
            //    });
        }
    }
}
