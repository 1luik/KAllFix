package n1luik.KAllFix.mixin.unsafe.path.InjectDatapack;

import n1luik.KAllFix.Imixin.IFileToIdConverter;
import n1luik.KAllFix.impl.InjectDatapackLoader;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@Mixin(FileToIdConverter.class)
public abstract class FileToIdConverterMixin  implements IFileToIdConverter {
    @Unique
    private final static Function<ResourceLocation, List<Resource>> KAllFix$resourceLocationListFunction = k -> new ArrayList<>();

    //@Unique
    //private static final AtomicLong KAllFix$name = new AtomicLong(0);
    @Shadow public abstract ResourceLocation idToFile(ResourceLocation p_251878_);

    @Shadow @Final private String prefix;
    @Shadow @Final private String extension;
    @Unique
    private ResourceLocation KAllFix$setLoadEx = null;
    @Unique
    private ResourceLocation KAllFix$setLoadTagEx = null;
    @Unique
    @Override
    public void KAllFix$setLoadEx(ResourceLocation name) {
        KAllFix$setLoadEx = name;
    }
    @Unique
    @Override
    public void KAllFix$setLoadTagEx(ResourceLocation name) {
        KAllFix$setLoadEx = name;
    }
    @Inject(method = "listMatchingResources", at = @At(value = "RETURN", ordinal = 0))
    public void impl1(ResourceManager p_252045_, CallbackInfoReturnable<Map<ResourceLocation, Resource>> cir) {
        Map<ResourceLocation, Resource> returnValue = cir.getReturnValue();
        if (KAllFix$setLoadEx != null) {
            InjectDatapackLoader instance = InjectDatapackLoader.INSTANCE;
            instance.lock.getAndAdd(1);
            for (Map.Entry<ResourceLocation, byte[]> resourceLocationStringEntry : instance.injectDatapacks.getOrDefault(KAllFix$setLoadEx, Map.of()).entrySet()) {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(resourceLocationStringEntry.getValue());
                returnValue.put(idToFile(resourceLocationStringEntry.getKey()), new Resource(
                        instance,
                        () -> byteArrayInputStream
                ));
            }
            instance.lock.getAndAdd(-1);
        }
    }
    @Inject(method = "listMatchingResourceStacks", at = @At(value = "RETURN", ordinal = 0))
    public void impl2(ResourceManager p_249881_, CallbackInfoReturnable<Map<ResourceLocation, List<Resource>>> cir){
        Map<ResourceLocation, List<Resource>> returnValue = cir.getReturnValue();
        if (KAllFix$setLoadTagEx != null) {
            InjectDatapackLoader instance = InjectDatapackLoader.INSTANCE;
            for (Map.Entry<ResourceLocation, List<byte[]>> resourceLocationListEntry : instance.injectTag.getOrDefault(KAllFix$setLoadTagEx, Map.of()).entrySet()) {
                //ResourceLocation key = resourceLocationListEntry.getKey();
                List<Resource> resources = returnValue.computeIfAbsent(new ResourceLocation("k_all_fix_inject_datapack", "k_all_fix_inject_datapack"), KAllFix$resourceLocationListFunction);
                for (byte[] bytes : resourceLocationListEntry.getValue()) {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                    //new ResourceLocation("k_all_fix_inject_datapack_"+KAllFix$name.getAndAdd(1), this.prefix + "/" + KAllFix$setLoadEx + this.extension)
                    resources.add(new Resource(
                            instance,
                            ()->byteArrayInputStream
                    ));
                }
            }

        }
    }
}
