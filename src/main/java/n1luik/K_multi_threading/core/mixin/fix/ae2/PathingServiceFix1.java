package n1luik.K_multi_threading.core.mixin.fix.ae2;

import appeng.api.networking.pathing.ControllerState;
import appeng.core.AEConfig;
import appeng.core.AELog;
import appeng.me.Grid;
import appeng.me.pathfinding.AdHocChannelUpdater;
import appeng.me.pathfinding.ChannelFinalizer;
import appeng.me.pathfinding.PathingCalculation;
import appeng.me.service.AdHocNetworkError;
import appeng.me.service.PathingService;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
/**
 * 我忘了这个是干什么的了，好像没用
 * */
@Mixin(value = PathingService.class,priority = 1100)
public abstract class PathingServiceFix1 {
    @Shadow(remap = false) private ControllerState controllerState;

    @Shadow(remap = false) protected abstract int calculateAdHocChannels();

    @Shadow(remap = false) private int channelsInUse;

    @Shadow(remap = false) @Final private Grid grid;

    @Shadow(remap = false) private int channelsByBlocks;

    @Shadow(remap = false) private int bootingTicks;

    @Shadow(remap = false) private PathingCalculation ongoingCalculation;

    @Shadow(remap = false) protected abstract void setChannelPowerUsage(double channelPowerUsage);

    @Shadow(remap = false) private boolean booting;

    @Shadow(remap = false) protected abstract void achievementPost();

    @Shadow(remap = false) protected abstract void postBootingStatusChange();

    @Shadow(remap = false) private @Nullable AdHocNetworkError adHocNetworkError;

    @Shadow(remap = false) private boolean reboot;

    @Shadow(remap = false) private boolean recalculateControllerNextTick;

    @Shadow(remap = false) protected abstract void updateControllerState();

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public void onServerEndTick() {
        if (this.recalculateControllerNextTick) {
            this.updateControllerState();
        }

        int nodes;
        if (this.reboot) {
            this.reboot = false;
            if (!this.booting) {
                this.booting = true;
                this.bootingTicks = 0;
                this.postBootingStatusChange();
            }

            this.channelsInUse = 0;
            this.adHocNetworkError = null;
            if (this.grid.isEmpty()) {
                return;
            }

            if (this.controllerState == ControllerState.NO_CONTROLLER) {
                this.channelsInUse = this.calculateAdHocChannels();
                nodes = this.grid.size();
                this.channelsByBlocks = nodes * this.channelsInUse;
                this.setChannelPowerUsage((double)this.channelsByBlocks / 128.0);
                this.grid.getPivot().beginVisit(new AdHocChannelUpdater(this.channelsInUse));
            } else if (this.controllerState == ControllerState.CONTROLLER_CONFLICT) {
                this.grid.getPivot().beginVisit(new AdHocChannelUpdater(0));
            } else {
                this.ongoingCalculation = new PathingCalculation(this.grid);
            }
        }

        if (this.booting) {
            if (this.ongoingCalculation != null) {
                PathingCalculation ongoingCalculation1 = this.ongoingCalculation;
                for(nodes = 0; nodes < AEConfig.instance().getPathfindingStepsPerTick(); ++nodes) {
                    ongoingCalculation1.step();
                    if (ongoingCalculation1.isFinished()) {
                        this.channelsByBlocks = ongoingCalculation1.getChannelsByBlocks();
                        this.channelsInUse = ongoingCalculation1.getChannelsInUse();
                        if (this.ongoingCalculation == ongoingCalculation1) {
                            this.ongoingCalculation = null;
                        }
                        break;
                    }
                }
            }

            ++this.bootingTicks;
            if (this.ongoingCalculation == null && grid.getPivot() != null) {
                this.achievementPost();
                this.booting = false;
                this.setChannelPowerUsage((double)this.channelsByBlocks / 128.0);
                this.grid.getPivot().beginVisit(new ChannelFinalizer());
                this.postBootingStatusChange();
            } else if (this.bootingTicks == 2000) {
                AELog.warn("Booting has still not completed after %d ticks for %s", new Object[]{this.bootingTicks, this.grid});
            }
        }

    }

}
