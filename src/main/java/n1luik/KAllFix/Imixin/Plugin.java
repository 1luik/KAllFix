package n1luik.KAllFix.Imixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class Plugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return test(targetClassName, mixinClassName) && !Boolean.getBoolean("KAF-RemoveMixin:"+mixinClassName);
    }
    public boolean test(String targetClassName, String mixinClassName) {
        String s = "n1luik.KAllFix.mixin.unsafe.";
        String s8 = "n1luik.KAllFix.mixin.unsafe.path.";
        if (mixinClassName.startsWith(s8)){
            if(Boolean.getBoolean("KAF-"+mixinClassName.substring(s8.length()).split("\\.", 2)[0])){
                return switch (mixinClassName) {
                    case "n1luik.KAllFix.mixin.unsafe.path.packetOptimize.blockEentity.ServerChunkCacheMixin2" -> Boolean.getBoolean("KMT_D");
                    case "n1luik.KAllFix.mixin.unsafe.path.packetOptimize.blockEentity.mt.ServerChunkCacheMixin1" -> !Boolean.getBoolean("KMT_D");
                    default -> true;
                };
            }
        }
        if (mixinClassName.startsWith(s)){
            return Boolean.getBoolean("KAF-"+mixinClassName.substring(s.length()));
        }
        //final boolean biolith = isModLoaded("biolith");
        //boolean terrablender = isModLoaded("terrablender");
        //wc这玩意默认双开的
        //String s2 = "n1luik.KAllFix.mixin.mixinfix.biolith.test_else.";
        //String s3 = "n1luik.KAllFix.mixin.mixinfix.biolith.test_all.";
        String s4 = "n1luik.KAllFix.mixin.mixinfix.fancyenchantments.";
        String s5 = "n1luik.KAllFix.mixin.FixConfigAll.";
        //String s6 = "n1luik.KAllFix.mixin.ex.CancelNio";
        String s7 = "n1luik.KAllFix.mixin.ex.FixAllPacket.";
        //boolean fixBiolithBugMode2 = Boolean.getBoolean("FixBiolithBugMode2");
        //if (!biolith && mixinClassName.startsWith(s2) && fixBiolithBugMode2) {
        //    return false;
        //}
        //if (!biolith && mixinClassName.startsWith(s3) && !fixBiolithBugMode2) {
        //    return false;
        //}
        if (mixinClassName.startsWith(s4)) {
            return isModLoaded("fancyenchantments");
        }
        if (!Boolean.getBoolean("KAF-FixConfigAuto") && mixinClassName.startsWith(s5)) {
            return false;
        }
        //if (mixinClassName.startsWith(s6)) {
        //    return Boolean.getBoolean("KAF-CancelNio");
        //}
        if (mixinClassName.startsWith(s7)) {
            if (Boolean.getBoolean("KAF-FixAllPacket")) {
                 return isModLoaded(mixinClassName.substring(s7.length()).split("\\.", 2)[0]);
            }
            return false;
        }
        //KAF-NbtAZ
        return switch (mixinClassName) {
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MinecraftServerMixin" -> isModLoaded("biolith");
            //case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSourceMixin" -> isModLoaded("biolith");
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSourceMixin" -> isModLoaded("biolith");
            case "n1luik.KAllFix.mixin.mixinfix.biolith.mod.TerramityModBiomesMixin" -> isModLoaded("biolith");
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSource2" -> isModLoaded("biolith");
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSourceMixin2" -> isModLoaded("biolith");
            case "n1luik.KAllFix.mixin.mixinfix.biolith.terrablender.InitializationHandlerMixin" ->
                    isModLoaded("biolith") && isModLoaded("terrablender");
            default -> true;
        };
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static boolean isModLoaded(String modId) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
        }
        return ModList.get().isLoaded(modId);
    }
}