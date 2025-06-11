package n1luik.KAllFix.mixin.unsafe.path.packetOptimize.entity.attributes;

import n1luik.KAllFix.data.packetOptimize.AttributesPacket;
import n1luik.KAllFix.util.UtilKAF;
import n1luik.K_multi_threading.core.Imixin.IPacketOptimize;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = LivingEntity.class, priority = Integer.MAX_VALUE-2)
public abstract class LivingEntityMixin2 implements IPacketOptimize {
    @Shadow public abstract AttributeMap getAttributes();

    @Unique
    private int KAllFix$size_Attributes = 0;
    @Unique
    private long KAllFix$sizeHash_Attributes = 0;
    @Unique
    private long KAllFix$hash_Attributes = 0;
    @Unique
    private long KAllFix$hash_AttributesRemove = 0;
    @Unique
    private volatile boolean KAllFix$upAttributesPacketV = true;

    @Inject(method = "tick", at = @At("RETURN"))
    public void tick(CallbackInfo ci){
        if (KAllFix$hash_AttributesRemove != AttributesPacket.AttributesRemove.get()) {
            KAllFix$hash_AttributesRemove = AttributesPacket.AttributesRemove.get();
            //KAllFix$upAttributesPacketV = true;//这样不会在发一次
            KAllFix$size_Attributes = 0;
            KAllFix$sizeHash_Attributes = 0;
            KAllFix$hash_Attributes = 0;
        }
        if (!KAllFix$upAttributesPacketV) {
            AttributeMap attributes = getAttributes();
            synchronized (attributes){
                Set<AttributeInstance> dirtyAttributes = attributes.getDirtyAttributes();
                if (KAllFix$size_Attributes == dirtyAttributes.size()) {
                    return;
                }
                if (KAllFix$sizeHash_Attributes == UtilKAF.hashAttributeInstanceSize(dirtyAttributes)) {
                    return;
                }
                if (KAllFix$hash_Attributes == UtilKAF.hashAttributeInstanceList(dirtyAttributes)) {
                    return;
                }
                KAllFix$upAttributesPacketV = true;
                KAllFix$size_Attributes = dirtyAttributes.size();
                KAllFix$sizeHash_Attributes = UtilKAF.hashAttributeInstanceSize(dirtyAttributes);
                KAllFix$hash_Attributes = UtilKAF.hashAttributeInstanceList(dirtyAttributes);
            }
        }
    }

    @Override
    public boolean KAllFix$upAttributesPacket() {
        if (KAllFix$upAttributesPacketV) {
            KAllFix$upAttributesPacketV = false;
            return true;
        }
        return false;
    }
}
