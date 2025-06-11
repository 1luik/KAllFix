package asm.n1luik.KAllFix.asm;

import asm.n1luik.KAllFix.asm.mod.createdieselgenerators.EntityMixinAsm;
import asm.n1luik.KAllFix.asm.mod.petrolpark.ITeamBoundItemAsm;
import asm.n1luik.KAllFix.asm.mod.petrolpark.PetrolparkAsm;
import asm.n1luik.KAllFix.asm.mod.petrolpark.ShopMenuItemAsm;
import asm.n1luik.KAllFix.asm.util.NoModuleReadClassAsm;
import asm.n1luik.KAllFix.asm.util.ReadClassAsm;
import asm.n1luik.KAllFix.asm.mod.jei.JEI_AddMapConcurrent_ASM;
import asm.n1luik.KAllFix.asm.mod.jei.JEI_NotErrorAddSynchronized_Asm;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class KAllFixAsm implements ITransformationService {
    public static BiFunction<TransformingClassLoader, String, byte[]> getclass;
    static {
        try {
            Method buildTransformedClassNodeFor = TransformingClassLoader.class.getDeclaredMethod("buildTransformedClassNodeFor", String.class, String.class);
            buildTransformedClassNodeFor.setAccessible(true);
            getclass = (transformingClassLoader, s) ->
            {
                try {
                    return (byte[]) buildTransformedClassNodeFor.invoke(transformingClassLoader, s, s);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public @NotNull String name() {
        return "KAllFixAsm";
    }

    @Override
    public void initialize(IEnvironment environment) {

    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {

    }

    @Override
    public @NotNull List<ITransformer> transformers() {
        List<ITransformer> transformers = new ArrayList<>(List.of(
                new EntityMixinAsm()
        ));
        if (Boolean.getBoolean("KAF-Fix_fabric-object-builder-api.jar")){
            transformers.add(new ReadClassAsm("fabric-object-builder-api", "v1-11.1.3", "TradeOffersTypeAwareBuyForOneEmeraldFactoryMixin", Set.of(
                    ITransformer.Target.targetClass("net.fabricmc.fabric.mixin.object.builder.TradeOffersTypeAwareBuyForOneEmeraldFactoryMixin")
            )));
        }
        if (Boolean.getBoolean("KAF-UnsafeCinderscapesFix1")){
            transformers.add(new ReadClassAsm("cinderscapes", "all", "MixinServerWorld", Set.of(
                    ITransformer.Target.targetClass("com.terraformersmc.cinderscapes.mixin.MixinServerWorld")
            )));
        }
        if (Boolean.getBoolean("KAF-MultiThreadingJEICommon")){
            transformers.add(new JEI_NotErrorAddSynchronized_Asm());
            transformers.add(new JEI_AddMapConcurrent_ASM());
        }
        if (Boolean.getBoolean("KAF-PlainTextSearchTreeMultiThreading")) {
            transformers.add(new ReadClassAsm("quark", "1.20.1+all", "PotionUtilsMixin", Set.of(
                    ITransformer.Target.targetClass("org.violetmoon.quark.mixin.mixins.PotionUtilsMixin")
            )));
        }

        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER && !Boolean.getBoolean("DisablePetrolpark")) {//用不了，直接不加载了
            //transformers.add(new PetrolparkAsm());
            transformers.add(new ShopMenuItemAsm());
            transformers.add(new ITeamBoundItemAsm());
        }
        return transformers;
    }
}
