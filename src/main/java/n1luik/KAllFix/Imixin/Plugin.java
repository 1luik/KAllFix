package n1luik.KAllFix.Imixin;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

@Slf4j
public class Plugin implements IMixinConfigPlugin {
    static {
        System.setProperty("KAF-ChunkBreedingControlSizeEnable", getInt("KAF-ChunkBreedingControlSize") != null ? "true" : "false");
    }
    public static Integer getInt(String key) {
        try {
            return Integer.getInteger(key);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    private volatile Integer biolithFixVersion = null;
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
        if (biolithFixVersion == null) {
            synchronized (this) {
                if (biolithFixVersion == null) {
                    if (!isModLoaded("biolith")) {
                        biolithFixVersion = 0;
                    } else {
                        InputStream biolith = getJarFile("biolith", "/com/terraformersmc/biolith/impl/biome/InterfaceBiomeSource.class").get();
                        try {
                            byte[] bytes = biolith.readAllBytes();
                            ClassNode classNode = new ClassNode();
                            new ClassReader(bytes).accept(classNode, 0);
                            boolean debug_1 = false;
                            for (var method : classNode.methods) {

                                if (method.name.equals("biolith$getDimensionType")) {
                                    debug_1 = true;
                                    if (method.desc.equals("()Lnet/minecraft/resources/ResourceKey;")) {
                                        biolithFixVersion = 1;
                                    }else {
                                        biolithFixVersion = 2;
                                    }

                                    break;
                                }
                            }
                            if(!debug_1){
                                throw new RuntimeException("biolith 当前版本未适配");
                            }
                            biolith.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    log.info("biolithFix: {}", biolithFixVersion);
                }
            }
        }
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
        String s9 = "n1luik.KAllFix.mixin.mixinfix.path.";
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
        if (mixinClassName.startsWith(s9)) {
             return isModLoaded(mixinClassName.substring(s9.length()).split("\\.", 2)[0]);
        }
        //KAF-NbtAZ
        return switch (mixinClassName) {
            case "n1luik.KAllFix.mixin.mixinfix.farm_and_charm.This" -> isModLoaded("farm_and_charm");
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MinecraftServerMixin" -> biolithFixVersion != 0;
            //case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSourceMixin" -> isModLoaded("biolith");
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSourceMixin" -> biolithFixVersion != 0;
            case "n1luik.KAllFix.mixin.mixinfix.biolith.mod.TerramityModBiomesMixin" -> biolithFixVersion != 0;
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSource2" -> biolithFixVersion == 2;
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSource2Forge" -> biolithFixVersion == 1;
            case "n1luik.KAllFix.mixin.mixinfix.biolith.MultiNoiseBiomeSourceMixin2" -> biolithFixVersion != 0;
            case "n1luik.KAllFix.mixin.mixinfix.biolith.terrablender.InitializationHandlerMixin" ->
                    biolithFixVersion != 0 && isModLoaded("terrablender");
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
    private static Optional<InputStream> getJarFile(String modId, String file) {
        List<ModFileInfo> list = LoadingModList.get().getModFiles().stream().filter(anObject -> {
            for (IModInfo modInfo2 : anObject.getMods()) {
                if (modId.equals(modInfo2.getModId())) {
                    return true;
                }
            }
            return false;
        }).toList();
        if (list.isEmpty()) {
            throw new RuntimeException("Mod not found: " + modId);
        }
        ModFileInfo modInfo = list.get(0);
        return modInfo.getFile().getSecureJar().moduleDataProvider().open(file);
    }
}