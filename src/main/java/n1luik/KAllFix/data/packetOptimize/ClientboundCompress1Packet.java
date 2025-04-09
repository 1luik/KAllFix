package n1luik.KAllFix.data.packetOptimize;

import io.netty.buffer.ByteBufInputStream;
import n1luik.KAllFix.forge.LoginProtectionMod.packet.ClientboundRemoveLoginProtectionPacket;
import n1luik.KAllFix.util.lib.ZstdLib;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class ClientboundCompress1Packet {
    public static Consumer<ClientboundCompress1Packet> clientCall;
    @Nullable
    public byte[] data;
    public final int dataWriteLength;
    public final int length;
    private byte[] buf;

    public ClientboundCompress1Packet(byte @org.jetbrains.annotations.Nullable [] data, int length){
        this(data, length, data == null ? 0 : data.length);
    }
    public ClientboundCompress1Packet(byte @org.jetbrains.annotations.Nullable [] data, int length, int dataWriteLength){
        this.data = data;
        this.length = length;
        this.dataWriteLength = dataWriteLength;
    }

    public ClientboundCompress1Packet(FriendlyByteBuf pack){
        dataWriteLength = -1;
        length = pack.readInt();
        if (pack.readByte() != 1) {
            data = null;
        }else {
            buf = new byte[pack.readInt()];
            pack.readBytes(buf);
        }
    }

    public void write(FriendlyByteBuf pack) {
        pack.writeInt(length);
        if (data == null) {
            pack.writeByte(0);
        }else{
            pack.writeByte(1);
            pack.writeInt(dataWriteLength);
            pack.writeBytes(data, 0, dataWriteLength);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        try {
            data = ZstdLib.in.apply(new ByteArrayInputStream(buf)).readNBytes(length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clientCall.accept(this);
        contextSupplier.get().setPacketHandled(true);

    }
}

