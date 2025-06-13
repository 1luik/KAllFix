package n1luik.KAllFix.data.packetOptimize;

import lombok.Getter;
import n1luik.K_multi_threading.core.util.ReflectionUtil;
import n1luik.K_multi_threading.core.util.Unsafe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static n1luik.KAllFix.data.packetOptimize.ClientCompresAll.listener;

public class ClientCompresPacketLoader {
    //public static BiConsumer<ClientboundSectionBlocksUpdatePacket, SectionPos> SectionBlocksUp_sectionPos;
    //public static BiConsumer<ClientboundSectionBlocksUpdatePacket, short[]> SectionBlocksUp_positions;
    //public static BiConsumer<ClientboundSectionBlocksUpdatePacket, BlockState[]> SectionBlocksUp_states;
    static final CompoundTag EmptyTag = new CompoundTag();

    //static {
    //    SectionBlocksUp_sectionPos = ReflectionUtil.setter(ClientboundSectionBlocksUpdatePacket.class, SectionPos.class);
    //    SectionBlocksUp_positions = ReflectionUtil.setter(ClientboundSectionBlocksUpdatePacket.class, short[].class);
    //    SectionBlocksUp_states = ReflectionUtil.setter(ClientboundSectionBlocksUpdatePacket.class, BlockState[].class);
    //}

    public final DataInput in;
    public final List<Packet<ClientGamePacketListener>> out = new CopyOnWriteArrayList<>();//用于异步解压缩减少主线程卡顿
    @Getter
    public boolean stop = false;


    public ClientCompresPacketLoader(DataInput in) {
        this.in = in;
    }

    public void start() throws IOException, InstantiationException {
        try{
            while (true) {
                switch (in.readByte()) {
                    case 1 -> {
                        switch (in.readByte()) {
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
        out.add(new ClientboundBlockUpdatePacket(
                new BlockPos(in.readInt(), in.readInt(), in.readInt()),
                Block.BLOCK_STATE_REGISTRY.byId(in.readInt())
        ));
    }

    protected void readMoreBlockUp() throws IOException, InstantiationException {
        ClientboundSectionBlocksUpdatePacket packet = (ClientboundSectionBlocksUpdatePacket) Unsafe.unsafe.allocateInstance(ClientboundSectionBlocksUpdatePacket.class);
        SectionPos sectionPos = SectionPos.of(in.readLong());
        int len = in.readInt();
        short[] positions = new short[len];
        BlockState[] states = new BlockState[len];
        for (int i = 0; i < len; i++) {
            positions[i] = in.readShort();
        }
        for (int i = 0; i < len; i++) {
            states[i] = Block.stateById(in.readInt());
        }
        packet.sectionPos = sectionPos;
        packet.positions = positions;
        packet.states = states;
        //SectionBlocksUp_sectionPos.accept(packet, sectionPos);
        //SectionBlocksUp_positions.accept(packet, positions);
        //SectionBlocksUp_states.accept(packet, states);
        out.add(packet);

    }

    protected void readMoreBlockEntityUp() throws IOException, InstantiationException {
        int i = in.readShort();
        while (i-- > 0) {
            readBlockEntityUp();
        }
    }
    protected void readBlockEntityUp() throws IOException {
        BlockPos pos = new BlockPos(in.readInt(), in.readInt(), in.readInt());
        BlockEntityType<?> type = BuiltInRegistries.BLOCK_ENTITY_TYPE.byId(in.readInt());
        CompoundTag tag;
        if (in.readByte() != 0) {
            tag = NbtIo.read(in);
        }else {
            tag = EmptyTag;
        }
        out.add(new ClientboundBlockEntityDataPacket(pos, type, tag));
    }
}
