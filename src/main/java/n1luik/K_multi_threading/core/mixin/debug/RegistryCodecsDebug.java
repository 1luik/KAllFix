package n1luik.K_multi_threading.core.mixin.debug;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Map;
@Deprecated
@Mixin(RegistryCodecs.class)
public class RegistryCodecsDebug {

    ///**
    // * @author
    // * @reason
    // */
    //@Overwrite
    //public static <E> Codec<Registry<E>> fullCodec(ResourceKey<? extends Registry<E>> p_248884_, Lifecycle p_251810_, Codec<E> p_250169_) {
    //    // FORGE: Fix MC-197860
    //    Codec<Map<ResourceKey<E>, E>> codec = new net.minecraftforge.common.LenientUnboundedMapCodec<>(ResourceKey.codec(p_248884_), p_250169_);
    //    return codec.xmap((p_258184_) -> {
    //        WritableRegistry<E> writableregistry = new MappedRegistry<>(p_248884_, p_251810_);
    //        p_258184_.forEach((p_258191_, p_258192_) -> {
    //            System.out.println("RegistryCodecsDebug debug1: " + p_258191_);
    //            writableregistry.register(p_258191_, p_258192_, p_251810_);
    //        });
    //        return writableregistry.freeze();
    //    }, (p_258193_) -> {
    //        return ImmutableMap.copyOf(p_258193_.entrySet());
    //    });
    //}
}
