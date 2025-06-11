package n1luik.KAllFix.mixin.unsafe;

import net.minecraft.nbt.NbtIo;
import net.minecraft.util.FastBufferedInputStream;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

@Mixin(NbtIo.class)
public class NbtIoMixin_NotGZip {
    private static final ArchiveStreamFactory KAllFix$ArchiveStreamFactory = new ArchiveStreamFactory();

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static DataInputStream createDecompressorStream(InputStream p_202494_) throws IOException {
        byte[] buf = p_202494_.readAllBytes();
        try {
            return new DataInputStream(new FastBufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(buf))));
        }catch (ZipException e) {
            try {
                ArchiveInputStream archiveInputStream = KAllFix$ArchiveStreamFactory.createArchiveInputStream(new ByteArrayInputStream(buf));
                archiveInputStream.getNextEntry();
                return new DataInputStream(new FastBufferedInputStream(archiveInputStream));
            } catch (ArchiveException e2) {
                throw new IOException(e2);
            }
        }
    }
}
