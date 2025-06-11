package n1luik.K_multi_threading.core.mixin.debug;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Deprecated
@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceDebug {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/MapCodec;codec()Lcom/mojang/serialization/Codec;", remap = false))
    private static Codec<?> fix1(MapCodec<MultiNoiseBiomeSource> instance) {
        Codec<MultiNoiseBiomeSource> codec = instance.codec();
        return new Codec<MultiNoiseBiomeSource>(){
            @Override
            public <T> DataResult<T> encode(MultiNoiseBiomeSource input, DynamicOps<T> ops, T prefix) {
                return codec.encode(input, ops, prefix);
            }

            @Override
            public <T> DataResult<Pair<MultiNoiseBiomeSource, T>> decode(DynamicOps<T> ops, T input) {
                DataResult<Pair<MultiNoiseBiomeSource, T>> decode = codec.decode(ops, input);
                decode.get().map(v -> decode, v -> {
                    System.out.println("MultiNoiseBiomeSourceDebug debug1" + v.message());
                    System.out.println("MultiNoiseBiomeSourceDebug debug2" + input);

                    return decode;//throw new RuntimeException("Failed to decode MultiNoiseBiomeSource: "+v.message());
                });
                return decode;
            }
        };
    }

}
