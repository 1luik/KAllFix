package n1luik.KAllFix.mixin.unsafe.path.InjectDatapack;

import n1luik.KAllFix.Imixin.ITagLoader;
import net.minecraft.commands.CommandFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(TagManager.class)
public class TagManagerMixin {

    @Inject(method = "createLoader", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", remap = false), locals = LocalCapture.CAPTURE_FAILHARD)
    public <T> void fix1(ResourceManager p_203908_, Executor p_203909_, RegistryAccess.RegistryEntry<T> p_203910_, CallbackInfoReturnable<CompletableFuture<TagManager.LoadResult<T>>> cir, ResourceKey resourcekey, Registry registry, TagLoader tagloader){
        ((ITagLoader)tagloader).KAllFix$path(resourcekey.location());
    }

}
