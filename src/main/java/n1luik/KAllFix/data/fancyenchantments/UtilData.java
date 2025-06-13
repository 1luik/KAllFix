package n1luik.KAllFix.data.fancyenchantments;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;

public class UtilData {
    public static EntityDataAccessor<Integer> fancyenchantments_up_tag;// = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
    public static int startSize = 1;
}
