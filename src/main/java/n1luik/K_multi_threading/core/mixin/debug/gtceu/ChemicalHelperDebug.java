package n1luik.K_multi_threading.core.mixin.debug.gtceu;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.UnificationEntry;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ChemicalHelper.class)
@Deprecated
public class ChemicalHelperDebug {
    @Unique
    private static final Logger k_multi_threading$logger = LoggerFactory.getLogger("[progressCondition-debug]");
    @Mutable
    @Shadow @Final public static Map<ItemLike, UnificationEntry> ITEM_UNIFICATION_ENTRY_COLLECTED;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void debug(CallbackInfo ci) {
        ITEM_UNIFICATION_ENTRY_COLLECTED = new ConcurrentHashMap<>(ITEM_UNIFICATION_ENTRY_COLLECTED){
            @Override
            public UnificationEntry put(@NotNull ItemLike key, @NotNull UnificationEntry value) {
                if (key == Items.AIR) {
                    StackWalker.getInstance().forEach(v -> {
                        k_multi_threading$logger.info(v.toString());
                    });
                    //k_multi_threading$logger.info(key.toString());
                }
                return super.put(key, value);
            }
        };
    }
}
