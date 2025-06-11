package n1luik.KAllFix.mixin.unsafe.path.packetOptimize.entity.attributes;

import n1luik.K_multi_threading.core.Imixin.IPacketOptimize;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Shadow @Final private Entity entity;
    @Redirect(method = "sendDirtyEntityData", at = @At(value = "INVOKE", target = "Ljava/util/Set;isEmpty()Z"))
    public boolean fix1(Set<AttributeInstance> instance){
        if (instance.isEmpty()) return true;
        return !((IPacketOptimize)entity).KAllFix$upAttributesPacket();
    }
}
