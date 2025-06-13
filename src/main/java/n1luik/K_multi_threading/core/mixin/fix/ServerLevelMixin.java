package n1luik.K_multi_threading.core.mixin.fix;

import n1luik.K_multi_threading.core.Imixin.IFixGetter;
import n1luik.K_multi_threading.fix.FixGetterRoot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ServerLevel.class, priority = Integer.MIN_VALUE+1000)
public class ServerLevelMixin implements IFixGetter<Object> {
    List<Object> objectList;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        objectList = new ArrayList<>();
        ServerLevel serverLevel = (ServerLevel) (Object) this;
        for (FixGetterRoot<?> root : FixGetterRoot.roots) {
            objectList.add(root.worldNew.apply(serverLevel));
        }
    }

    @Override
    public Object K_multi_threading$getFix(int id) {
        return objectList.get(id);
    }
}
