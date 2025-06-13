package n1luik.KAllFix.mixin.unsafe.path.InjectDatapack.mod.alloyforgery;

import n1luik.KAllFix.Imixin.ITagLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wraith.alloyforgery.data.DelayedTagGroupLoader;
import wraith.alloyforgery.data.RecipeTagLoader;

@Mixin(value = RecipeTagLoader.class, remap = false)
@Deprecated
public class RecipeTagLoaderMixin {

    @Shadow(remap = false) @Final private DelayedTagGroupLoader<Recipe<?>> tagGroupLoader;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    public void fix1(CallbackInfo ci){
        ((ITagLoader)tagGroupLoader).KAllFix$path(new ResourceLocation("minecraft", "recipes"));
    }

}
