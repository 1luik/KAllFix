package n1luik.K_multi_threading.core.mixin.fix.mek;

import mekanism.api.MekanismAPI;
import mekanism.common.content.network.transmitter.Transmitter;
import mekanism.common.lib.transmitter.DynamicNetwork;
import mekanism.common.lib.transmitter.TransmitterNetworkRegistry;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.CapabilityTileEntity;
import mekanism.common.tile.transmitter.TileEntityTransmitter;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityTransmitter.class, priority = 1001, remap = false)
public abstract class TileEntityTransmitterFix1 extends CapabilityTileEntity {
    public TileEntityTransmitterFix1(TileEntityTypeRegistryObject<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    //boolean reloadFix;

    @Shadow public abstract Transmitter<?, ?, ?> getTransmitter();

    @Shadow private boolean loaded;


    @Shadow protected abstract void onWorldJoin(boolean wasPresent);

    //@Inject(method = "<clinit>", at = @At("HEAD"))
    //private static void addDebug(CallbackInfo ci) {
    //    MekanismAPI.debug = true;
    //}



    @Inject(method = "onUpdateServer", at = @At("HEAD"))
    public void reloadFix(CallbackInfo ci) {
        if (System.currentTimeMillis() % 50000 == 0) {
            level.getServer().execute(()-> {
                if (isRemote()) {
                    onWorldJoin(false);
                }
            });

        }
    }
}

