package n1luik.K_multi_threading.core.mixin.debug;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Deprecated
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerDebug extends Player {

    public ServerPlayerDebug(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @Shadow public abstract boolean isSpectator();

    @Shadow public abstract boolean isCreative();

    @Inject(method = "tick",at = @At(value = "INVOKE",target = "Lnet/minecraft/server/level/ServerPlayer;closeContainer()V"))
    public void debug1(CallbackInfo ci){
        System.out.printf("closeContainer1 %s %n",!this.level().isClientSide);
    }

    //@Inject(method = "tick",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
    //public void debug2(CallbackInfo ci){
    //    System.out.println("closeContainer2");
    //}
}
