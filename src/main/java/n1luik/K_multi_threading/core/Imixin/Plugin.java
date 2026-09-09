package n1luik.K_multi_threading.core.Imixin;

import asm.n1luik.K_multi_threading.asm.util.AsmApi;
import asm.n1luik.K_multi_threading.asm.util.AsmApi2;
import n1luik.K_multi_threading.core.UnsafeEnable;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Plugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return AsmApi2.bootType != AsmApi2.BootType.NEO_FORGE ? "mixins.K_multi_threading.refmap.json" : null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return test(targetClassName, mixinClassName) && !Boolean.getBoolean("KAF-RemoveMixin:"+mixinClassName);
    }
    public boolean test(String targetClassName, String mixinClassName) {
        String s1 = "n1luik.K_multi_threading.core.mixin.minecraftfix.loginMultiThreading.";
        if (mixinClassName.startsWith(s1)) {
            return Boolean.getBoolean("KMT-LoginMultiThreading");
        }
        return switch (mixinClassName) {
            case "n1luik.K_multi_threading.core.mixin.minecraftfix.ChunkMapFix2" -> UnsafeEnable.INSTANCE.SafeUnloadChunk;
            case "n1luik.K_multi_threading.core.mixin.impl.MinecraftServerImpl2" -> UnsafeEnable.INSTANCE.IndependencePlayer;
            case "n1luik.K_multi_threading.core.mixin.minecraftfix.ServerChunkCacheFix2" -> (!(isModLoaded("harium") || isModLoaded("canary") || isModLoaded("radium") || isModLoaded("lithium"))) || Boolean.getBoolean("KMT-OpenVanillaServerChunkCache") || AsmApi.isModLoaded("sable");
            case "n1luik.K_multi_threading.core.mixin.minecraftfix.LegacyRandomSourceFix2" -> !isModLoaded("structureessentials");
            case "n1luik.K_multi_threading.core.mixin.minecraftfix.ServerWatchdogFix1" -> !isModLoaded("fullstackwatchdog");
            case "n1luik.K_multi_threading.neoforge.mixin.fix.sable.LevelChunkMixin" -> isModLoaded("sable");
            case "n1luik.K_multi_threading.core.mixin.impl.LevelImpl2" -> isModLoaded("observable");
            case "n1luik.K_multi_threading.core.mixin.impl.LevelImpl1" -> !isModLoaded("observable");
            case "n1luik.K_multi_threading.core.mixin.minecraftfix.FlowingFluidFix1" -> AsmApi.mcVersion.startsWith("1.19") || AsmApi.mcVersion.startsWith("1.20");
            case "n1luik.K_multi_threading.core.mixin.impl.MinecraftServerImpl1",
                 //"n1luik.K_multi_threading.core.mixin.fix.mek.BasicFluidTankFix1",
                 "n1luik.K_multi_threading.core.mixin.minecraftfix.ChunkMapFix1"
                    -> AsmApi2.bootType != AsmApi2.BootType.NEO_FORGE;
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
        return AsmApi.isModLoaded(modId);
        //if (ModList.get() == null) {
        //    return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
        //}
        //return ModList.get().isLoaded(modId);
    }
}