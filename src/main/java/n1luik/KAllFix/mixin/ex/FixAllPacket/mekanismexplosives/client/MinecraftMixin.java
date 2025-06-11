package n1luik.KAllFix.mixin.ex.FixAllPacket.mekanismexplosives.client;

import net.mcreator.mekanismexplosives.network.MekanismexplosivesModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Unique
    private static final Field KAllFix$MekanismexplosivesModVariables$MapVariables$clientSide;
    static {

        try {
            KAllFix$MekanismexplosivesModVariables$MapVariables$clientSide = MekanismexplosivesModVariables.MapVariables.class.getDeclaredField("clientSide");
            KAllFix$MekanismexplosivesModVariables$MapVariables$clientSide.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ForgeEventFactory;onPreClientTick()V", remap = false))
    public void init(CallbackInfo ci){
        try {
            MekanismexplosivesModVariables.MapVariables mapVariables = (MekanismexplosivesModVariables.MapVariables) KAllFix$MekanismexplosivesModVariables$MapVariables$clientSide.get(MekanismexplosivesModVariables.MapVariables.class);
            mapVariables.GlobalTimer += 0.05;
            if (mapVariables.GlobalTimer >= 1.1){
                mapVariables.GlobalTimer = 0.0;
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}