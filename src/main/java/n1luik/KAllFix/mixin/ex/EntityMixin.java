package n1luik.KAllFix.mixin.ex;

import com.google.common.collect.ImmutableList;
import n1luik.KAllFix.Imixin.IEntityEx;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

@Mixin(Entity.class)
public class EntityMixin implements IEntityEx{


    //@Shadow private ImmutableList<IEntityEx> passengers;

    @Unique
    private static int KAllFix$getSelfAndPassengers_Impl_Size(Entity entity){
        int add = 1;
        for (Entity passenger : entity.getPassengers()) {
            add += KAllFix$getSelfAndPassengers_Impl_Size(passenger);
        }
        return add;
    }

    @Unique
    private static int KAllFix$getSelfAndPassengers_Impl(Entity entity, int size, Entity[] out){
        int add = 1;
        out[size] = entity;
        for (Entity passenger : entity.getPassengers()) {
            add += KAllFix$getSelfAndPassengers_Impl(passenger, size + add, out);
        }
        return add;
    }

    @Override
    public Entity[] KAllFix$getSelfAndPassengers() {
        Entity[] entities = new Entity[KAllFix$getSelfAndPassengers_Impl_Size((Entity) (Object) this)];
        KAllFix$getSelfAndPassengers_Impl((Entity) (Object) this, 0, entities);
        return entities;
    }
}
