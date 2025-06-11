package n1luik.KAllFix.mixin.unsafe.path.InjectDatapack;

import n1luik.KAllFix.Imixin.IFileToIdConverter;
import n1luik.KAllFix.Imixin.ITagLoader;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mixin(TagLoader.class)
public class TagLoaderMixin implements ITagLoader {
    @Unique
    private static final Map<String, ResourceLocation> KAllFix$edirectoryMap = new HashMap<>();
    static {
        KAllFix$edirectoryMap.put("tags/functions", new ResourceLocation("minecraft", "functions"));
        KAllFix$edirectoryMap.put("tags/recipes", new ResourceLocation("minecraft", "recipes"));
        KAllFix$edirectoryMap.put("tinkering/tags/modifiers", new ResourceLocation("tinkering", "modifiers"));
    }

    @Shadow @Final private String directory;
    @Shadow @Final private static Logger LOGGER;
    @Unique
    private ResourceLocation KAllFix$path = null;

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/FileToIdConverter;listMatchingResourceStacks(Lnet/minecraft/server/packs/resources/ResourceManager;)Ljava/util/Map;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void impl1(ResourceManager p_144496_, CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cir, Map map, FileToIdConverter filetoidconverter) {
        if (KAllFix$path == null){
            synchronized (this) {
                if (KAllFix$path == null){
                    KAllFix$path = KAllFix$edirectoryMap.get(directory);
                    if (KAllFix$path == null){
                        String[] split = directory.split("/", 2);
                        if (split.length == 2){
                            if (split[0].equals("tags")) {
                                //模组名是tags
                                String[] split2 = split[1].split("/", 2);
                                if (split2[0].equals("tags") && split2.length == 2){
                                    KAllFix$path = new ResourceLocation(split[0], split2[1]);
                                }

                            }else {
                                String[] split2 = split[1].split("/", 2);
                                if (split2[0].equals("tags") && split2.length == 2){
                                    KAllFix$path = new ResourceLocation(split[0], split2[1]);
                                }

                            }
                        }
                        LOGGER.error("Failed to find a valid directory for tag loader {}", directory);
                    }
                }
            }
        }
        ((IFileToIdConverter)filetoidconverter).KAllFix$setLoadTagEx(Objects.requireNonNull(KAllFix$path, directory));
    }

    @Override
    public void KAllFix$path(ResourceLocation path) {
        KAllFix$path = path;
    }
}
