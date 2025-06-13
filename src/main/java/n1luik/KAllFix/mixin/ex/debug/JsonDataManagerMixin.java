package n1luik.KAllFix.mixin.ex.debug;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.tacz.guns.resource.manager.JsonDataManager;
import net.minecraft.ResourceLocationException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = JsonDataManager.class, remap = false)
public abstract class JsonDataManagerMixin<T> {

    @Shadow protected abstract T parseJson(JsonElement element);

    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/manager/JsonDataManager;parseJson(Lcom/google/gson/JsonElement;)Ljava/lang/Object;", remap = false), remap = false)
    private T impl1(JsonDataManager<T> instance, JsonElement element) {
        try {
            return parseJson(element);
        } catch (Throwable e) {
            if (e instanceof JsonParseException) throw (JsonParseException) e;
            throw new JsonParseException(e);
        }
    }
}
