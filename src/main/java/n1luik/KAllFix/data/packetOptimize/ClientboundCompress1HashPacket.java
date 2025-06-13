package n1luik.KAllFix.data.packetOptimize;

import n1luik.KAllFix.util.lib.ZstdLib;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class ClientboundCompress1HashPacket extends ClientboundCompress1Packet {
    public final int hash;
    public ClientboundCompress1HashPacket(int hash, byte @org.jetbrains.annotations.Nullable [] data, int length, int dataWriteLength) {
        super(data, length, dataWriteLength);
        this.hash = hash;
    }

    public ClientboundCompress1HashPacket(FriendlyByteBuf pack){
        super(pack);
        if (length > 0){
            hash = pack.readInt();
        }else {
            hash = 0;
        }
    }

    @Override
    public void write(FriendlyByteBuf pack) {
        super.write(pack);
        if (length > 0){
            pack.writeInt(hash);
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        try {
            if (length > 0){
                decompress();
                if (data != null && data.length == length) {
                    if (hash == Arrays.hashCode(data)){
                        clientCall.accept(this);
                        contextSupplier.get().setPacketHandled(true);
                    }else {
                        contextSupplier.get().setPacketHandled(true);
                        throw new RuntimeException("Hash is not equal to the expected hash");
                    }
                } else {
                    contextSupplier.get().setPacketHandled(true);
                    throw new RuntimeException("Decompressed data length is not equal to the expected length");
                }
            }else {
                clientCall.accept(this);
                contextSupplier.get().setPacketHandled(true);
            }
        } catch (IOException e) {
            contextSupplier.get().setPacketHandled(true);
            throw new RuntimeException(e);
        }
        ;
    }
}

