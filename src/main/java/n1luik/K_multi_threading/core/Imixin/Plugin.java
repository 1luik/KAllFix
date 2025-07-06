package n1luik.K_multi_threading.core.Imixin;

import n1luik.K_multi_threading.core.UnsafeEnable;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

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
        String s1 = "n1luik.K_multi_threading.core.mixin.minecraftfix.loginMultiThreading.";
        if (mixinClassName.startsWith(s1)) {
            return Boolean.getBoolean("KMT-LoginMultiThreading");
        }
        return switch (mixinClassName) {
            case "n1luik.K_multi_threading.core.mixin.minecraftfix.ChunkMapFix2" -> UnsafeEnable.INSTANCE.SafeUnloadChunk;
            case "n1luik.K_multi_threading.core.mixin.impl.MinecraftServerImpl2" -> UnsafeEnable.INSTANCE.IndependencePlayer;
            case "n1luik.K_multi_threading.core.mixin.minecraftfix.ServerChunkCacheFix2" -> !(isModLoaded("canary") || isModLoaded("radium") || isModLoaded("lithium"));
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