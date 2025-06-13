package n1luik.KAllFix.mixin.ex.debug;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.tacz.guns.resource.manager.AttachmentsTagManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.util.List;

@Mixin(value = AttachmentsTagManager.class, remap = false)
public abstract class AttachmentsTagManagerMixin {
    @Shadow(remap = false) protected abstract List<String> parseJson(JsonElement element);

    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/manager/AttachmentsTagManager;parseJson(Lcom/google/gson/JsonElement;)Ljava/util/List;", remap = false), remap = false)
    private List<String> impl1(AttachmentsTagManager instance, com.google.gson.JsonElement jsonElement) {
        try {
            return parseJson(jsonElement);
        } catch (Throwable e) {
            if (e instanceof JsonParseException) throw (JsonParseException) e;
            throw new JsonParseException(e);
        }
    }
}
