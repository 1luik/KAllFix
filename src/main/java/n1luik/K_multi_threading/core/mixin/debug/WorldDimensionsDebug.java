package n1luik.K_multi_threading.core.mixin.debug;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
@Deprecated
@Mixin(WorldDimensions.class)
public class WorldDimensionsDebug {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;get(Lnet/minecraft/resources/ResourceKey;)Ljava/lang/Object;"))
    private <T> Object debug1(Registry instance, ResourceKey<T> tResourceKey){
        System.out.println("WorldDimensionsDebug debug1: " + tResourceKey);
        for (Object o : instance.keySet()) {
            System.out.println("WorldDimensionsDebug debug2: " + o);
        }
        return instance.get(tResourceKey);
    }

    //@Redirect(method = "lambda$static$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/RegistryCodecs;fullCodec(Lnet/minecraft/resources/ResourceKey;Lcom/mojang/serialization/Lifecycle;Lcom/mojang/serialization/Codec;)Lcom/mojang/serialization/Codec;"))
    //private static <E extends LevelStem> Codec<Registry<E>> debug2(ResourceKey<? extends Registry<E>> p_248884_, Lifecycle p_251810_, Codec<E> p_250169_){
    //        // FORGE: Fix MC-197860
    //        Codec<Map<ResourceKey<E>, E>> codec = new net.minecraftforge.common.LenientUnboundedMapCodec<>(ResourceKey.codec(p_248884_), p_250169_);
    //    return codec.xmap((p_258184_) -> {
    //        WritableRegistry<E> writableregistry = new MappedRegistry<>(p_248884_, p_251810_);
    //        p_258184_.forEach((p_258191_, p_258192_) -> {
    //            System.out.println("WorldDimensionsDebug debug3: " + p_258191_);
//
    //            writableregistry.register(p_258191_, p_258192_, p_251810_);
    //        });
    //        return writableregistry.freeze();
    //    }, (p_258193_) -> {
    //        return ImmutableMap.copyOf(p_258193_.entrySet());
    //    });
    //}

}
