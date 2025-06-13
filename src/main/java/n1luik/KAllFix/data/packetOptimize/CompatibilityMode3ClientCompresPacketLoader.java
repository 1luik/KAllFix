package n1luik.KAllFix.data.packetOptimize;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static n1luik.KAllFix.data.packetOptimize.ClientCompresPacketLoader.EmptyTag;

public class CompatibilityMode3ClientCompresPacketLoader {

    public final FriendlyByteBuf pack;
    //public final DataInput in;
    public final List<Packet<ClientGamePacketListener>> out = new CopyOnWriteArrayList<>();//用于异步解压缩减少主线程卡顿
    @Getter
    public boolean stop = false;


    public CompatibilityMode3ClientCompresPacketLoader(byte[] in) {
        ByteBuf pack1 = Unpooled.wrappedBuffer(in);
        pack = new FriendlyByteBuf(pack1);
        //this.in = new DataInputStream(new ByteBufInputStream(pack1));
    }

    public void start() throws IOException, InstantiationException {
        try{
            while (true) {
                switch (pack.readByte()) {
                    case 1 -> {
                        switch (pack.readByte()) {
                            case 1 -> readMoreBlockUp();
                            case 2 -> readBlockUp();
                            default -> throw new RuntimeException();
                        }
                    }
                    case 2 -> readMoreBlockEntityUp();
                    case 3 -> {
                        stop = true;
                        return;
                    }
                    default -> throw new RuntimeException();
                }
            }
        }finally {
            stop = true;
        }
    }

    protected void readBlockUp() throws IOException {
        out.add(new ClientboundBlockUpdatePacket(pack));
    }

    protected void readMoreBlockUp() throws InstantiationException {
        out.add(new ClientboundSectionBlocksUpdatePacket(pack));

    }

    protected void readMoreBlockEntityUp() throws IOException, InstantiationException {
        int i = pack.readShortLE();
        for (int i1 = 0; i1 < i; i1++) {
            readBlockEntityUp();
        }
    }
    protected void readBlockEntityUp() throws IOException {
        out.add(new ClientboundBlockEntityDataPacket(pack));
    }
}
