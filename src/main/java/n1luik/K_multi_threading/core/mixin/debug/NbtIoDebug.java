package n1luik.K_multi_threading.core.mixin.debug;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * 唯一一个常驻嘉宾艹
 * */
@Mixin(NbtIo.class)
public abstract class NbtIoDebug {
    //@Redirect(method = "readCompressed(Ljava/io/File;)Lnet/minecraft/nbt/CompoundTag;", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtIo;readCompressed(Ljava/io/InputStream;)Lnet/minecraft/nbt/CompoundTag;"))
    //private static CompoundTag readCompressedDebug(InputStream inputStream) throws IOException{
    //    try {
    //        return NbtIo.readCompressed(inputStream);
    //    } catch (IOException e) {
    //        throw new IOException("File ["]", e);
    //    }
    //}

    @Shadow public static CompoundTag readCompressed(InputStream p_128940_) throws IOException{return null;};

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static CompoundTag readCompressed(File p_128938_) throws IOException {
        try  {
            InputStream inputstream = new FileInputStream(p_128938_);
            return readCompressed(inputstream);
        }catch (Throwable e){
            throw new IOException("File [" + p_128938_ + "]", e);
        }
    }
}
