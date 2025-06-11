package n1luik.KAllFix.mixin.unsafe.path.InjectDatapack;

import n1luik.KAllFix.Imixin.ITagLoader;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerFunctionLibrary.class)
@Deprecated
public class ServerFunctionLibraryMixin {
    @Shadow @Final private TagLoader<CommandFunction> tagsLoader;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void fix1(CallbackInfo ci){
        ((ITagLoader)tagsLoader).KAllFix$path(new ResourceLocation("minecraft", "functions"));
    }

}
