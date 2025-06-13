package n1luik.KAllFix.Imixin;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import n1luik.KAllFix.util.UnsafeByteArrayOutputStream;
import net.minecraft.server.level.ServerPlayer;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface IOptimizeBlockEntityPacket {
    //boolean KAllFix$lockBlockUpPacketData();
    //boolean KAllFix$lockBlockEntityUpPacketData();
    UnsafeByteArrayOutputStream KAllFix$PacketData();
    void KAllFix$PacketDataRemove();
    List<ServerPlayer> KAllFix$writePlayer();

}
