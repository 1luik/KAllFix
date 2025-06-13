package n1luik.KAllFix.mixin.unsafe.path.packetOptimize.blockEentity;

import com.mojang.authlib.GameProfile;
import n1luik.KAllFix.data.packetOptimize.ClientCompresAll;
import n1luik.KAllFix.data.packetOptimize.ClientCompresPacketLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin1 implements ClientGamePacketListener {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void run1(Minecraft p_253924_, Screen p_254239_, Connection p_253614_, ServerData p_254072_, GameProfile p_254079_, WorldSessionTelemetryManager p_262115_, CallbackInfo ci){
        ClientCompresAll.listener = this;
    }
}
