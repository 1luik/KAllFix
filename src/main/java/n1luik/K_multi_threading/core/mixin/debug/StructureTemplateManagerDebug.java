package n1luik.K_multi_threading.core.mixin.debug;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.function.Function;

@Deprecated
@Mixin(StructureTemplateManager.class)
public class StructureTemplateManagerDebug {
    private static final Logger LOGGER = LogUtils.getLogger();
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;add(Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList$Builder;", ordinal = 1, remap = false))
    public <E extends StructureTemplateManager.Source> ImmutableList.Builder<StructureTemplateManager.Source> debug1(ImmutableList.Builder<StructureTemplateManager.Source> instance, Object element){
        StructureTemplateManager.Source element1 = (StructureTemplateManager.Source) element;
        Function<ResourceLocation, Optional<StructureTemplate>> loader = element1.loader();
        return instance.add(new StructureTemplateManager.Source(v->{
            Optional<StructureTemplate> apply = loader.apply(v);
            if (apply.isEmpty()) {
                LOGGER.error("Failed to load structure template {}", v);
            }
            return apply;
        }, element1.lister()));
    }

}
