package n1luik.K_multi_threading.core.mixin.debug;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import n1luik.K_multi_threading.core.util.LenientUnboundedMapCodec_debug;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;
import java.util.Map;
@Deprecated
@Mixin(LevelStorageSource.class)
public class LevelStorageSourceDebug {
    @Inject(method = "readWorldGenSettings", at = @At(value = "HEAD", target = "Lnet/minecraft/nbt/CompoundTag;getCompound(Ljava/lang/String;)Lnet/minecraft/nbt/CompoundTag;"))
    private static <T> void debug1(Dynamic<T> p_251661_, DataFixer p_251712_, int p_250368_, CallbackInfoReturnable<DataResult<WorldGenSettings>> cir){
        try {
            if (p_251661_.getValue() instanceof CompoundTag compoundtag)
                NbtIo.writeCompressed(compoundtag, new File("./out_debug1.nbt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static <E> Codec<Registry<E>> _fullCodec(ResourceKey<? extends Registry<E>> p_248884_, Lifecycle p_251810_, Codec<E> p_250169_) {
        // FORGE: Fix MC-197860
        Codec<Map<ResourceKey<E>, E>> codec = new LenientUnboundedMapCodec_debug<>(ResourceKey.codec(p_248884_), p_250169_);
        return codec.xmap((p_258184_) -> {
            WritableRegistry<E> writableregistry = new MappedRegistry<>(p_248884_, p_251810_);
            p_258184_.forEach((p_258191_, p_258192_) -> {
                System.out.println("RegistryCodecsDebug debug1: " + p_258191_);
                writableregistry.register(p_258191_, p_258192_, p_251810_);
            });
            return writableregistry.freeze();
        }, (p_258193_) -> {
            return ImmutableMap.copyOf(p_258193_.entrySet());
        });
    }
    @Redirect(method = "readWorldGenSettings", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/Dynamic;)Lcom/mojang/serialization/DataResult;", remap = false))
    private static <T> DataResult<WorldGenSettings> debug1(Codec<T> instance, Dynamic<T> dynamic){
        Dynamic<T> dimensions = dynamic.get("dimensions").get().get().left().get();
        DynamicOps<T> ops = dynamic.getOps();
        ops.getMap(dimensions.getValue()).get().left().get().entries().forEach(v -> {
            System.out.println("RegistryCodecsDebug debug2: " + ops.getStringValue(v.getFirst()));

        });
        MapCodec<WorldDimensions> CODEC2 = RecordCodecBuilder.mapCodec((p_258996_) -> {
            return p_258996_.group(_fullCodec(Registries.LEVEL_STEM, Lifecycle.stable(), LevelStem.CODEC).fieldOf("dimensions").forGetter(WorldDimensions::dimensions)).apply(p_258996_, p_258996_.stable(WorldDimensions::new));
        });

        Codec<WorldGenSettings> objectCodec = RecordCodecBuilder.create((p_248477_) -> {
            return p_248477_.group(WorldOptions.CODEC.forGetter(WorldGenSettings::options),
                    CODEC2.forGetter(WorldGenSettings::dimensions)).apply(p_248477_, p_248477_.stable(WorldGenSettings::new));
        });
        return objectCodec.parse(dynamic);
    }

}
