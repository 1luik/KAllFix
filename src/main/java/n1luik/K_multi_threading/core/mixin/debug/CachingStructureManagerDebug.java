package n1luik.K_multi_threading.core.mixin.debug;

import com.mojang.datafixers.DataFixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.embeddedt.modernfix.structure.CachingStructureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Deprecated
@Mixin(value = CachingStructureManager.class, priority = 1100)
public abstract class CachingStructureManagerDebug {
    @Shadow(remap = false) public static CompoundTag readStructureTag(ResourceLocation location, DataFixer datafixer, InputStream stream) throws IOException{throw  new RuntimeException();};

    @Redirect(method = "readStructure", at = @At(value = "INVOKE", target = "Lorg/embeddedt/modernfix/structure/CachingStructureManager;readStructureTag(Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/datafixers/DataFixer;Ljava/io/InputStream;)Lnet/minecraft/nbt/CompoundTag;"), remap = false)
    private static CompoundTag debug1(ResourceLocation hasher, DataFixer hash, InputStream cachedUpgraded) {
        try  {
            return readStructureTag(hasher, hash, cachedUpgraded);
        }catch (Throwable e){
            throw new RuntimeException("File [" + hasher + "]", e);
        }
    }
}
