package n1luik.KAllFix.mixin.mixinfix.biolith;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.terraformersmc.biolith.impl.biome.InterfaceBiomeSource;
import n1luik.KAllFix.util.ModTags;
import n1luik.KAllFix.util.TagPair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.LevelStem;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MultiNoiseBiomeSource2 implements InterfaceBiomeSource {
    // 定义一个唯一的Logger对象
    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();
    // 定义一个volatile的boolean变量，用于标记是否已经初始化
    @Unique
    private volatile boolean KAllFix$initialized = false;

    // 定义一个可变的Shadow Final变量，用于存储MultiNoiseBiomeSource的parameters
    @Mutable
    @Shadow @Final private Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters;

    // 重定向MultiNoiseBiomeSource的构造函数中的parameters字段
    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/biome/MultiNoiseBiomeSource;parameters:Lcom/mojang/datafixers/util/Either;", opcode = Opcodes.PUTFIELD))
    private void fix1(MultiNoiseBiomeSource instance, Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> value) {
        // 如果ModTags.StartServerTag为false
        if (!ModTags.StartServerTag) {
            // 将parameters设置为value
            parameters = value.map(v -> {
                // 遍历v的values
                for (Pair<Climate.ParameterPoint, Holder<Biome>> parameterPointHolderPair : v.values()) {
                    // 如果parameterPointHolderPair是TagPair类型
                    if (parameterPointHolderPair instanceof TagPair<Climate.ParameterPoint, Holder<Biome>>) {
                        // 将v的values中不是TagPair类型的元素放入list
                        List<Pair<Climate.ParameterPoint, Holder<Biome>>> list = v.values().stream().filter(v2 -> !(v2 instanceof TagPair<Climate.ParameterPoint, Holder<Biome>>)).toList();
                        // 如果list为空，则返回v
                        if (list.isEmpty()) return Either.left(v);
                        // 否则返回一个新的Climate.ParameterList
                        return Either.left(new Climate.ParameterList<>(list));
                    }
                }
                // 将v的values中是TagPair类型的元素放入list
                List<Pair<Climate.ParameterPoint, Holder<Biome>>> list = v.values().stream().<Pair<Climate.ParameterPoint, Holder<Biome>>>map(TagPair::new).toList();
                // 如果list为空，则返回v
                if (list.isEmpty()) return Either.left(v);
                // 否则返回一个新的Climate.ParameterList
                return Either.left(new Climate.ParameterList<>(list));
            }, v -> value);
        }else {
            // 否则将parameters设置为value
            parameters = value;
        }
    }
    // 注入MultiNoiseBiomeSource的parameters方法
    @Inject(method = "parameters", at = @At("HEAD"), cancellable = true)
    private void fix2(CallbackInfoReturnable<Climate.ParameterList<Holder<Biome>>> cir) {
        // 如果ModTags.StartServerTag为false
        if (!ModTags.StartServerTag) {
            // 如果biolith$getDimensionType()为null
            if (biolith$getDimensionType() == null) {
                // 打印警告信息
                LOGGER.warn("BiomeSource is not initialized yet, skipping biolith");
                // 设置返回值为this.parameters.map的返回值
                cir.setReturnValue(this.parameters.map((p_275171_) -> {
                    return p_275171_;
                }, (p_275172_) -> {
                    return p_275172_.value().parameters();
                }));

            }
        }
    }
    // 注入MultiNoiseBiomeSource的parameters方法
    @Inject(method = "parameters", at = @At("RETURN"))
    private void fix3(CallbackInfoReturnable<Climate.ParameterList<Holder<Biome>>> cir) {
        // 如果KAllFix$initialized为false
        if (!KAllFix$initialized) {
            // 同步this
            synchronized (this){
                // 如果KAllFix$initialized为false
                if (!KAllFix$initialized) {
                    // 如果biolith$getDimensionType()是OVERWORLD或NETHER
                    if (biolith$getDimensionType().is(BuiltinDimensionTypes.OVERWORLD) || biolith$getDimensionType().is(BuiltinDimensionTypes.NETHER)) {
                        // 将parameters.map的返回值中不是TagPair类型的元素放入map1
                        List<ResourceKey<Biome>> map1 = parameters.map(v -> v.values().stream().filter(v3 -> !(v3 instanceof TagPair<Climate.ParameterPoint, Holder<Biome>>)).map(v3 -> v3.getSecond().unwrapKey().get()).toList(), v -> v.get().parameters().values().stream().filter(v3 -> !(v3 instanceof TagPair<Climate.ParameterPoint, Holder<Biome>>)).map(v3 -> v3.getSecond().unwrapKey().get()).toList());
                        // 将cir.getReturnValue().values中包含map1的元素替换为TagPair
                        cir.getReturnValue().values = cir.getReturnValue().values.stream().map(v2 -> map1.contains(v2.getSecond().unwrapKey().get()) ? v2 : new TagPair<>(v2)).toList();
                    }
                    // 将KAllFix$initialized设置为true
                    KAllFix$initialized = true;
                }
            }
        }
    }
}