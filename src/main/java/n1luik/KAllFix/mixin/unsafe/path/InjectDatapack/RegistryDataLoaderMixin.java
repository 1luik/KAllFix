package n1luik.KAllFix.mixin.unsafe.path.InjectDatapack;

import com.mojang.serialization.Decoder;
import n1luik.KAllFix.Imixin.IFileToIdConverter;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {
    @Inject(method = "loadRegistryContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/RegistryOps;create(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/resources/RegistryOps$RegistryInfoLookup;)Lnet/minecraft/resources/RegistryOps;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static <E> void impl1(RegistryOps.RegistryInfoLookup p_256369_, ResourceManager p_256349_, ResourceKey<? extends Registry<E>> p_255792_, WritableRegistry<E> p_256211_, Decoder<E> p_256232_, Map<ResourceKey<?>, Exception> p_255884_, CallbackInfo ci, String s, FileToIdConverter filetoidconverter){
        ((IFileToIdConverter)filetoidconverter).KAllFix$setLoadEx(p_255792_.location());
    }
}
