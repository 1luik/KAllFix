package n1luik.K_multi_threading.core.Imixin;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {
    @Override
    public void connect() {
        if (System.getProperty("KMT_D") == null)
            Mixins.addConfigurations("mixins.K_multi_threading.json");
        if (System.getProperty("KAllFix_D") == null)
            Mixins.addConfigurations("mixins.KAllFix.json");
        if (Boolean.getBoolean("KAF-LoginProtectionMod"))
            Mixins.addConfigurations("mixins.KAllFix$KAF-LoginProtectionMod.json");
        if (Boolean.getBoolean("KMT-Debug"))
            Mixins.addConfigurations("mixins.K_multi_threading$debug.json");
    }
}

