package n1luik.K_multi_threading.core.mixin.fix.gtceu;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(value = MetaMachine.class, priority = 1001)
@Deprecated
public class MetaMachineFix1 {

    @Mutable
    @Shadow(remap = false) @Final protected List<MachineTrait> traits;

    @Mutable
    @Shadow(remap = false) @Final private List<TickableSubscription> serverTicks;

    @Mutable
    @Shadow(remap = false) @Final private List<TickableSubscription> waitingToAdd;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void fix1(IMachineBlockEntity holder, CallbackInfo ci){
        traits = Collections.synchronizedList(traits);
        serverTicks = Collections.synchronizedList(serverTicks);
        waitingToAdd = Collections.synchronizedList(waitingToAdd);
    }

}
