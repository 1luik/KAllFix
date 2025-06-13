package n1luik.K_multi_threading.core.mixin.minecraftfix;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import n1luik.K_multi_threading.core.util.concurrent.ConcurrentInt2ObjectOpenHashMap;
import n1luik.K_multi_threading.core.util.concurrent.FalseObjectLinkedOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.entity.PartEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public abstract class ServerLevelFix1 {
    @Unique
    private final Object K_multi_threading$lockBlockEvents = new Object();

    //@Unique
    //ParaServerChunkProvider paraServerChunkProvider;

    //@Mutable
    //@Shadow @Final private List<ServerPlayer> players;

    //@Mutable
    //@Shadow @Final private Set<Mob> navigatingMobs;

    @Shadow private volatile boolean isUpdatingNavigations;

    //@Redirect(method = "<init>",at = @At(value = "NEW", target = "it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap", ordinal = 0))
    //public Int2ObjectOpenHashMap init(){
    //    return new ConcurrentInt2ObjectOpenHashMap();
    //}

    //@Mutable
    //@Shadow @Final private Int2ObjectMap<PartEntity<?>> dragonParts;

    //@Mutable
    @Shadow @Final private ObjectLinkedOpenHashSet<BlockEventData> blockEvents;

    @Shadow protected abstract void runBlockEvents();

    //@Inject(method = "<init>",at = @At("RETURN"))
    //public void fix1(MinecraftServer p_214999_, Executor p_215000_, LevelStorageSource.LevelStorageAccess p_215001_, ServerLevelData p_215002_, ResourceKey p_215003_, LevelStem p_215004_, ChunkProgressListener p_215005_, boolean p_215006_, long p_215007_, List p_215008_, boolean p_215009_, RandomSequences p_288977_, CallbackInfo ci){
    //    //players = new CopyOnWriteArrayList();
    //    //navigatingMobs = ConcurrentHashMap.newKeySet();
    //    //paraServerChunkProvider = (ParaServerChunkProvider) chunkSource;
    //    //dragonParts = new ConcurrentInt2ObjectOpenHashMap<>();
    //    //ConcurrentHashMap.KeySetView<BlockEventData, Boolean> blockEventData = ConcurrentHashMap.newKeySet();
    //    //blockEventData.addAll(blockEvents);
    //    //blockEvents = new FalseObjectLinkedOpenHashSet<>(blockEventData);
    //}
    @Redirect(method = "sendBlockUpdated",at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;logAndPauseIfInIde(Ljava/lang/String;Ljava/lang/Throwable;)V"))
    public void fix2(String p_200891_, Throwable p_200892_){
        while (this.isUpdatingNavigations) Thread.onSpinWait();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void blockEvent(BlockPos p_8746_, Block p_8747_, int p_8748_, int p_8749_) {
        synchronized (K_multi_threading$lockBlockEvents){
            this.blockEvents.add(new BlockEventData(p_8746_, p_8747_, p_8748_, p_8749_));
        }
    }

    @Redirect(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;runBlockEvents()V"))
    public void fix4(ServerLevel instance){
        synchronized (K_multi_threading$lockBlockEvents) {
            runBlockEvents();
        }
    }
}
