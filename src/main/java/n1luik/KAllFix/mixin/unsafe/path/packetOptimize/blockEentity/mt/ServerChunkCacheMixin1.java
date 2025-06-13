package n1luik.KAllFix.mixin.unsafe.path.packetOptimize.blockEentity.mt;

import n1luik.KAllFix.Imixin.IOptimizeBlockEntityPacket;
import n1luik.KAllFix.data.packetOptimize.ClientboundCompress1Packet;
import n1luik.KAllFix.forge.LoginProtectionMod.packet.ClientboundRemoveLoginProtectionPacket;
import n1luik.KAllFix.forge.PacketOptimizeAll;
import n1luik.KAllFix.util.ServerPlayerKey;
import n1luik.KAllFix.util.UnsafeByteArrayOutputStream;
import n1luik.KAllFix.util.lib.ZstdLib;
import n1luik.KAllFix.util.ob.OBbyteArrayInt;
import n1luik.K_multi_threading.core.base.CalculateTask;
import n1luik.K_multi_threading.core.util.OB2;
import net.minecraft.Util;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.network.PacketDistributor;
import org.checkerframework.checker.units.qual.C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin1 {
    @Unique
    private final Object KAllFix$broadcastLock = new Object();
    @Redirect(method = "tickChunks", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"))
    private void impl1(List<ServerChunkCache.ChunkAndHolder>instance, Consumer<ServerChunkCache.ChunkAndHolder> consumer) {
        synchronized (KAllFix$broadcastLock) {
            instance.forEach(consumer);
        }
        Util.backgroundExecutor().execute(()->{
            synchronized (KAllFix$broadcastLock) {
                Function<ServerPlayerKey, List<ChunkPos>> serverPlayerKeyListFunction = v -> new ArrayList<>();
                Map<ServerPlayerKey, List<ChunkPos>> map = new HashMap<>();
                Map<ChunkPos, OBbyteArrayInt> map2 = new HashMap<>();
                for (ServerChunkCache.ChunkAndHolder chunkAndHolder : instance) {
                    if (chunkAndHolder.holder() instanceof IOptimizeBlockEntityPacket ip) {
                        if (ip.KAllFix$writePlayer() == null){
                            //压根不需要发送更新
                            continue;
                        }
                        ChunkPos pos = chunkAndHolder.holder().getPos();
                        if (!ip.KAllFix$writePlayer().isEmpty()) {
                            UnsafeByteArrayOutputStream unsafeByteArrayOutputStream = ip.KAllFix$PacketData();
                            map2.put(pos, new OBbyteArrayInt(unsafeByteArrayOutputStream.getByteArray(), unsafeByteArrayOutputStream.arrayMax()));
                        }
                        for (ServerPlayer serverPlayer : ip.KAllFix$writePlayer()) {
                            map.computeIfAbsent(new ServerPlayerKey(serverPlayer), serverPlayerKeyListFunction).add(pos);
                        }
                    }

                }
                Map.Entry<ServerPlayerKey, List<ChunkPos>>[] array = map.entrySet().toArray(Map.Entry[]::new);
                int length = array.length;
                (new CalculateTask(()->"packetOptimize.blockEentity", 0, length, (i) -> {
                    try {
                        if (i < array.length) {
                            UnsafeByteArrayOutputStream byteArrayOutputStream = new UnsafeByteArrayOutputStream();

                            OutputStream apply = ZstdLib.out.apply(byteArrayOutputStream);
                            int srcLen = 1;
                            for (ChunkPos chunkPos : array[i].getValue()) {
                                OBbyteArrayInt integerOB2 = map2.get(chunkPos);
                                apply.write(integerOB2.t1, 0 , integerOB2.t2);
                                srcLen += integerOB2.t2;
                            }
                            apply.write(3);
                            apply.flush();
                            apply.close();
                            byte[] byteArray = byteArrayOutputStream.getByteArray();
                            ServerPlayer player = array[i].getKey().player();
                            PacketOptimizeAll.PACKET_INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundCompress1Packet(
                                    byteArray,
                                    srcLen,
                                    byteArrayOutputStream.arrayMax()
                            ));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })).callAuto();

                for (ServerChunkCache.ChunkAndHolder chunkAndHolder : instance) {
                    if (chunkAndHolder.holder() instanceof IOptimizeBlockEntityPacket ip) {
                        ip.KAllFix$PacketDataRemove();
                    }

                }
            }
        });
    }

}
