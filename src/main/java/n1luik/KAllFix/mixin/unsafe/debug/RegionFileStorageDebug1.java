package n1luik.KAllFix.mixin.unsafe.debug;

import com.mojang.logging.LogUtils;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ConcurrentModificationException;

@Mixin(RegionFileStorage.class)
public class RegionFileStorageDebug1 {
    @Unique
    private static final Logger RegionFileStorageDebug1_LOGGER = LoggerFactory.getLogger("[RegionFileStorageDebug1-write]");
    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtIo;write(Lnet/minecraft/nbt/CompoundTag;Ljava/io/DataOutput;)V"))
    public void write(CompoundTag tag, DataOutput output) {
        try {
            NbtIo.write(tag, output);
        }catch (IOException | ConcurrentModificationException e) {
            RegionFileStorageDebug1_LOGGER.warn("Failed to write chunk ", e);
            try {
                NbtIo.write(tag, output);
            }catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        }
    }
}
