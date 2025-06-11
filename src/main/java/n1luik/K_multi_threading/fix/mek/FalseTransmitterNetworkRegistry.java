package n1luik.K_multi_threading.fix.mek;

import lombok.Getter;
import mekanism.common.lib.transmitter.TransmitterNetworkRegistry;
import net.minecraft.server.level.ServerLevel;

public class FalseTransmitterNetworkRegistry {
    @Getter
    protected TransmitterNetworkRegistry base = new TransmitterNetworkRegistry();
    @Getter
    protected ServerLevel level;

    public FalseTransmitterNetworkRegistry(ServerLevel level) {
        this.level = level;

    }
}
