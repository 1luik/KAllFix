package n1luik.K_multi_threading.core.mixin.fix.mek;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "mekanism/common/lib/transmitter/TransmitterNetworkRegistry/OrphanPathFinder", priority = 1001)
public class TransmitterNetworkRegistry_OrphanPathFinder {
}
