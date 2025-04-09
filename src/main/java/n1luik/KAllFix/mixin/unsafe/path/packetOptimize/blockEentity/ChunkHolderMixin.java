package n1luik.KAllFix.mixin.unsafe.path.packetOptimize.blockEentity;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import n1luik.KAllFix.Imixin.IOptimizeBlockEntityPacket;
import n1luik.KAllFix.util.UnsafeByteArrayOutputStream;
import n1luik.KAllFix.util.lib.ZstdLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * 数据格式：
 * <p>
 * 1字节
 * <p>
 * --|-0： 数据错误
 * <p>
 * --|-1：
 * <p>
 * --|-|-1字节
 * <p>
 * --|-|-0： 数据错误
 * <p>
 * --|-|-1： ClientboundSectionBlocksUpdatePacket的数据或快速访问
 * <p>
 * --|-|-2： ClientboundBlockUpdatePacket的数据或快速访问
 * <p>
 * --|-2：
 * <p>
 * --|-|-2字节最大32767（数据大小）
 *
 *
 * */
@Mixin(ChunkHolder.class)
public abstract class ChunkHolderMixin implements IOptimizeBlockEntityPacket {
    @Shadow protected abstract void broadcast(List<ServerPlayer> p_288998_, Packet<?> p_289013_);

    @Unique
    private final static boolean KAllFix$CompatibilityMode_ClientboundBlockEntityDataPacket = Boolean.getBoolean("KAF-packetOptimize.CompatibilityMode.ClientboundBlockEntityDataPacket");
    @Unique
    private final static boolean KAllFix$CompatibilityMode_ClientboundSectionBlocksUpdatePacket = Boolean.getBoolean("KAF-packetOptimize.CompatibilityMode.ClientboundSectionBlocksUpdatePacket");
    //@Unique
    //private boolean KAllFix$lockBlockUpPacketData = false;
    //@Unique
    //private boolean KAllFix$lockBlockEntityUpPacketData = false;
    //@Unique
    //private int KAllFix$lockBlockUpPacketType = 0;
    //private int KAllFix$lockBlockUpPacketEndType = 0;
    //@Unique
    //private int KAllFix$lockBlockEntityUpPacketSize = 0;
    @Unique
    private UnsafeByteArrayOutputStream KAllFix$PacketData = null;
    @Unique
    private DataOutputStream KAllFix$PacketDataTo = null;
    @Unique
    private List<ServerPlayer> KAllFix$writePlayer = null;

    @Override
    public List<ServerPlayer> KAllFix$writePlayer() {
        return KAllFix$writePlayer;
    }

    @Override
    public void KAllFix$PacketDataRemove() {
        KAllFix$PacketData = null;
        KAllFix$PacketDataTo = null;
        KAllFix$writePlayer = null;
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void cinit(CallbackInfo ci){
        ZstdLib.cinit();
    }
    @Inject(method = "broadcastChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkHolder$PlayerProvider;getPlayers(Lnet/minecraft/world/level/ChunkPos;Z)Ljava/util/List;", ordinal = 1))
    public void impl5(LevelChunk p_140055_, CallbackInfo ci){
        //KAllFix$lockBlockUpPacketData = false;
        //KAllFix$lockBlockEntityUpPacketData = false;
        //KAllFix$lockBlockUpPacketType = 0;
        //KAllFix$lockBlockEntityUpPacketSize = 0;
        //KAllFix$lockBlockUpPacketEndType = 0;
        KAllFix$PacketData = new UnsafeByteArrayOutputStream(3072);
        KAllFix$PacketDataTo = new DataOutputStream(KAllFix$PacketData);
    }

    @Unique
    private Packet<ClientGamePacketListener> KAllFix$writeBlockEntity_Compatibility(FriendlyByteBuf friendlyByteBuf, Level leve, BlockPos pos) throws IOException {
        BlockEntity blockEntity = leve.getBlockEntity(pos);
        if (blockEntity != null) {
            Packet<ClientGamePacketListener> updatePacket = blockEntity.getUpdatePacket();
            if (updatePacket instanceof ClientboundBlockEntityDataPacket ep) {
                //KAllFix$lockBlockEntityUpPacketSize++;
                friendlyByteBuf.writerIndex(0);
                friendlyByteBuf.readerIndex(0);
                KAllFix$PacketDataTo.write(friendlyByteBuf.array(), 0, friendlyByteBuf.readableBytes());
            }else {
                return updatePacket;
            }
        }
        return null;

    }
    @Unique
    private Packet<ClientGamePacketListener> KAllFix$writeBlockEntity(Level leve, BlockPos pos) throws IOException {
        BlockEntity blockEntity = leve.getBlockEntity(pos);
        if (blockEntity != null) {
            Packet<ClientGamePacketListener> updatePacket = blockEntity.getUpdatePacket();
            if (updatePacket instanceof ClientboundBlockEntityDataPacket ep) {
                //KAllFix$lockBlockEntityUpPacketSize++;
                BlockPos pos2 = ep.pos;

                KAllFix$PacketDataTo.writeInt(pos2.getX());
                KAllFix$PacketDataTo.writeInt(pos2.getY());
                KAllFix$PacketDataTo.writeInt(pos2.getZ());
                KAllFix$PacketDataTo.writeInt(BuiltInRegistries.BLOCK_ENTITY_TYPE.getId(blockEntity.getType()));
                if (ep.tag != null) {
                    KAllFix$PacketDataTo.write(1);
                    NbtIo.write(ep.tag, KAllFix$PacketDataTo);
                }else {
                    KAllFix$PacketDataTo.write(0);
                    //out.add((byte) 0b0);
                    //out.add((byte) 0b0);
                    //out.add((byte) 0b0);
                }
            }else {
                return updatePacket;
            }
        }
        return null;
    }

    @Redirect(method = "broadcastChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkHolder;broadcastBlockEntityIfNeeded(Ljava/util/List;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    public void impl4(ChunkHolder instance, List<ServerPlayer> p_288982_, Level p_289011_, BlockPos p_288969_, BlockState p_288973_){
        try {
            KAllFix$writePlayer = p_288982_;
            KAllFix$PacketDataTo.write(2);
            KAllFix$PacketDataTo.writeShort(1);
            //KAllFix$lockBlockUpPacketEndType = 2;
            if (p_288973_.hasBlockEntity()) {
                FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer(1024));
                if (KAllFix$CompatibilityMode_ClientboundBlockEntityDataPacket) {
                    Packet<ClientGamePacketListener> clientGamePacketListenerPacket = KAllFix$writeBlockEntity_Compatibility(friendlyByteBuf, p_289011_, p_288969_);
                    if (clientGamePacketListenerPacket != null){
                        broadcast(p_288982_, clientGamePacketListenerPacket);
                    }
                } else {
                    Packet<ClientGamePacketListener> clientGamePacketListenerPacket = KAllFix$writeBlockEntity(p_289011_, p_288969_);
                    if (clientGamePacketListenerPacket != null){
                        broadcast(p_288982_, clientGamePacketListenerPacket);
                    }

                }
            }
            //KAllFix$lockBlockEntityUpPacketData = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Redirect(method = "broadcastChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundSectionBlocksUpdatePacket;runUpdates(Ljava/util/function/BiConsumer;)V"))
    public void impl1(ClientboundSectionBlocksUpdatePacket instance, BiConsumer<BlockPos, BlockState> i){

    }
    @Inject(method = "broadcastChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundSectionBlocksUpdatePacket;runUpdates(Ljava/util/function/BiConsumer;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void impl6(LevelChunk p_140055_, CallbackInfo ci, Level level, List<ServerPlayer> list1, int j, ShortSet shortset, int i, SectionPos sectionpos, LevelChunkSection levelchunksection, ClientboundSectionBlocksUpdatePacket instance){
        try {
            KAllFix$writePlayer = list1;
            KAllFix$PacketDataTo.write(2);
            //KAllFix$lockBlockUpPacketEndType = 2;
            SectionPos sectionPos = instance.sectionPos;
            short[] positions = instance.positions;
            BlockState[] states = instance.states;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            int l = positions.length;
            short s = 0;
            for (int i1 = 0; i1 < l; i1++) {
                if (states[i1].hasBlockEntity()) {
                    s++;
                }
            }
            KAllFix$PacketDataTo.writeShort(s);
            if (KAllFix$CompatibilityMode_ClientboundBlockEntityDataPacket) {
                FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer(1024));
                for (int i1 = 0; i1 < l; i1++) {
                    short position = positions[i1];
                    blockpos$mutableblockpos.set(sectionPos.relativeToBlockX(position), sectionPos.relativeToBlockY(position), sectionPos.relativeToBlockZ(position));
                    if (states[i1].hasBlockEntity()) {
                        Packet<ClientGamePacketListener> clientGamePacketListenerPacket = KAllFix$writeBlockEntity_Compatibility(friendlyByteBuf, level, blockpos$mutableblockpos);
                        if (clientGamePacketListenerPacket != null) {
                            broadcast(list1, clientGamePacketListenerPacket);
                        }
                    }
                }
            }else{
                for (int i1 = 0; i1 < l; i1++) {
                    short position = positions[i1];
                    blockpos$mutableblockpos.set(sectionPos.relativeToBlockX(position), sectionPos.relativeToBlockY(position), sectionPos.relativeToBlockZ(position));
                    if (states[i1].hasBlockEntity()) {
                        Packet<ClientGamePacketListener> clientGamePacketListenerPacket = KAllFix$writeBlockEntity(level, blockpos$mutableblockpos);
                        if (clientGamePacketListenerPacket != null) {
                            broadcast(list1, clientGamePacketListenerPacket);
                        }

                    }
                }
            }
            //KAllFix$lockBlockEntityUpPacketData = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Redirect(method = "broadcastChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkHolder;broadcast(Ljava/util/List;Lnet/minecraft/network/protocol/Packet;)V", ordinal = 1))
    public void impl2(ChunkHolder instance, List<ServerPlayer> p_288998_, Packet<?> p_289013_){
        try {
            KAllFix$writePlayer = p_288998_;
            DataOutputStream kAllFix$PacketData = KAllFix$PacketDataTo;
            //KAllFix$lockBlockUpPacketType = 2;
            KAllFix$PacketDataTo.write(1);
            KAllFix$PacketDataTo.write(2);
            //KAllFix$lockBlockUpPacketEndType = 1;
            if (KAllFix$CompatibilityMode_ClientboundSectionBlocksUpdatePacket) {
                FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer(96));
                //进行安全检查
                if (!(p_289013_ instanceof ClientboundBlockUpdatePacket)) throw new RuntimeException("Packet is not ClientboundBlockUpdatePacket");
                p_289013_.write(friendlyByteBuf);
                kAllFix$PacketData.write(friendlyByteBuf.array(), 0, friendlyByteBuf.readableBytes());
            }else {
                if (p_289013_ instanceof ClientboundBlockUpdatePacket packet) {
                    {
                        BlockPos pos = packet.pos;
                        int x = pos.getX();
                        int y = pos.getY();
                        int z = pos.getZ();

                        kAllFix$PacketData.writeInt(x);
                        kAllFix$PacketData.writeInt(y);
                        kAllFix$PacketData.writeInt(z);
                        int id = Block.getId(packet.blockState);
                        kAllFix$PacketData.writeInt(id);
                    }

                }else {
                    throw new RuntimeException("Packet is not ClientboundSectionBlocksUpdatePacket");
                }

            }
            //KAllFix$lockBlockUpPacketData = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Redirect(method = "broadcastChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkHolder;broadcast(Ljava/util/List;Lnet/minecraft/network/protocol/Packet;)V", ordinal = 2))
    public void impl3(ChunkHolder instance, List<ServerPlayer> p_288998_, Packet<?> p_289013_){
        try{
            KAllFix$writePlayer = p_288998_;
            DataOutputStream kAllFix$PacketData = KAllFix$PacketDataTo;
            //KAllFix$lockBlockUpPacketType = 1;
            KAllFix$PacketDataTo.write(1);
            KAllFix$PacketDataTo.write(1);
            //KAllFix$lockBlockUpPacketEndType = 1;
            if (KAllFix$CompatibilityMode_ClientboundSectionBlocksUpdatePacket) {
                FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer(1024));
                //进行安全检查
                if (!(p_289013_ instanceof ClientboundSectionBlocksUpdatePacket))
                    throw new RuntimeException("Packet is not ClientboundSectionBlocksUpdatePacket");
                p_289013_.write(friendlyByteBuf);
                kAllFix$PacketData.write(friendlyByteBuf.array(), 0, friendlyByteBuf.readableBytes());
            } else {
                if (p_289013_ instanceof ClientboundSectionBlocksUpdatePacket packet) {
                    kAllFix$PacketData.writeLong(packet.sectionPos.asLong());
                    short[] positions = packet.positions;
                    BlockState[] states = packet.states;
                    int max = positions.length;
                    kAllFix$PacketData.writeInt(max);
                    for (short position : positions) {
                        kAllFix$PacketData.writeShort(position);
                    }
                    for (int i = 0; i < max; i++) {
                        int id = Block.getId(states[i]);
                        kAllFix$PacketData.writeInt(id);
                    }

                } else {
                    throw new RuntimeException("Packet is not ClientboundSectionBlocksUpdatePacket");
                }

            }
            //KAllFix$lockBlockUpPacketData = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //@Override
    //public boolean KAllFix$lockBlockUpPacketData() {
    //    return KAllFix$lockBlockUpPacketData;
    //}
//
    //@Override
    //public boolean KAllFix$lockBlockEntityUpPacketData() {
    //    return KAllFix$lockBlockEntityUpPacketData;
    //}

    @Override
    public UnsafeByteArrayOutputStream KAllFix$PacketData() {
        return KAllFix$PacketData;
    }
}
