package n1luik.KAllFix.mixin.mixinfix;

import n1luik.KAllFix.data.fancyenchantments.UtilData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin1 extends Entity {
    static {
        UtilData.fancyenchantments_up_tag = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
    }
    public LivingEntityMixin1(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "defineSynchedData", at = @At("RETURN"))
    public void addStart(CallbackInfo ci){
        entityData.define(UtilData.fancyenchantments_up_tag, UtilData.startSize);

    }
    @Inject(method = "verifyEquippedItem", at = @At("HEAD"))
    public void addTag(ItemStack p_181123_, CallbackInfo ci){
        getEntityData().set(UtilData.fancyenchantments_up_tag, UtilData.startSize);
    }
}
