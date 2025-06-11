package n1luik.KAllFix.mixin.unsafe.path.packetOptimize.blockEentity;

import n1luik.KAllFix.data.packetOptimize.ClientCompresAll;
import n1luik.KAllFix.data.packetOptimize.ClientCompresPacketLoader;
import n1luik.KAllFix.data.packetOptimize.ClientboundCompress1Packet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(GameConfig p_91084_, CallbackInfo ci) {
        ClientCompresAll.setImpl();
    }
}
