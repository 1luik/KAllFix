package n1luik.K_multi_threading.core.mixin.fix;

import n1luik.K_multi_threading.core.Imixin.IFixGetter;
import n1luik.K_multi_threading.fix.FixGetterRoot;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = MinecraftServer.class, priority = Integer.MIN_VALUE+1000)
public class MinecraftServerMixin implements IFixGetter<Object> {
    List<Object> objectList;

    @Inject(method = "runServer", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
        objectList = new ArrayList<>();
        MinecraftServer minecraftServer = (MinecraftServer) (Object) this;
        for (FixGetterRoot<?> root : FixGetterRoot.roots) {
            objectList.add(root.serverNew.apply(minecraftServer));
        }
    }

    @Override
    public Object K_multi_threading$getFix(int id) {
        return objectList.get(id);
    }
}
