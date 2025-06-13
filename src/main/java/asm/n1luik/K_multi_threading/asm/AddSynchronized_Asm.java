package asm.n1luik.K_multi_threading.asm;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
@Slf4j
public class AddSynchronized_Asm implements ITransformer<ClassNode> {
    public static final List<String[]> stringsList = new ArrayList<>(List.of(
            ////////sb forge
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/Level.getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/Level.getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;Ljava/util/List;I)V"),
            ////////
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/PersistentEntitySectionManager.updateChunkStatus(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/entity/Visibility;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/PersistentEntitySectionManager.processUnloads()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/behavior/LongJumpToRandomPos.getJumpCandidate(Lnet/minecraft/server/level/ServerLevel;)Ljava/util/Optional;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/behavior/LongJumpToRandomPos.pickCandidate(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;J)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/behavior/LongJumpToRandomPos.start(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Mob;J)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/behavior/ShufflingList.iterator()Ljava/util/Iterator;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/behavior/ShufflingList.stream()Ljava/util/stream/Stream;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/behavior/ShufflingList.add(Ljava/lang/Object;I)Lnet/minecraft/world/entity/ai/behavior/ShufflingList;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/behavior/ShufflingList.shuffle()Lnet/minecraft/world/entity/ai/behavior/ShufflingList;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/attributes/AttributeMap.getSyncableAttributes()Ljava/util/Collection;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/attributes/AttributeMap.onAttributeModified(Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/attributes/AttributeMap.save()Lnet/minecraft/nbt/ListTag;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/attributes/AttributeMap.getInstance(Lnet/minecraft/world/entity/ai/attributes/Attribute;)Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/Level.tickBlockEntities()V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/Level.neighborShapeChanged(Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;II)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/Level.markAndNotifyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;II)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/lighting/LeveledPriorityQueue.removeFirstLong()J"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/lighting/LeveledPriorityQueue.dequeue(JII)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/lighting/LeveledPriorityQueue.enqueue(JI)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/lighting/LeveledPriorityQueue.checkFirstQueuedLevel(I)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunk.getBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/chunk/LevelChunk$EntityCreationType;)Lnet/minecraft/world/level/block/entity/BlockEntity;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunk.addAndRegisterBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunk.clearAllBlockEntities()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunk.removeBlockEntityTicker(Lnet/minecraft/core/BlockPos;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunk.updateBlockEntityTicker(Lnet/minecraft/world/level/block/entity/BlockEntity;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunk.registerAllBlockEntitiesAfterLevelLoad()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunk.setBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunk.removeBlockEntity(Lnet/minecraft/core/BlockPos;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunk.clearAllBlockEntities()V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunk.postProcessGeneration()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunk.setBlockState(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunk.getListenerRegistry(I)Lnet/minecraft/world/level/gameevent/GameEventListenerRegistry;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunk.removeGameEventListenerRegistry(I)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunk.unpackTicks(J)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkHolder.setTicketLevel(I)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkHolder.blockChanged(Lnet/minecraft/core/BlockPos;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkHolder.replaceProtoChunk(Lnet/minecraft/world/level/chunk/ImposterProtoChunk;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkHolder.broadcastChanges(Lnet/minecraft/world/level/chunk/LevelChunk;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunkSection.setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunkSection.read(Lnet/minecraft/network/FriendlyByteBuf;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/LevelChunkSection.write(Lnet/minecraft/network/FriendlyByteBuf;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/util/thread/BlockableEventLoop.managedBlock(Ljava/util/function/BooleanSupplier;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.saveAllChunks(Z)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.updateChunkScheduling(JILnet/minecraft/server/level/ChunkHolder;I)Lnet/minecraft/server/level/ChunkHolder;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.playerLoadedChunk(Lnet/minecraft/server/level/ServerPlayer;Lorg/apache/commons/lang3/mutable/MutableObject;Lnet/minecraft/world/level/chunk/LevelChunk;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.addEntity(Lnet/minecraft/world/entity/Entity;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.markPositionReplaceable(Lnet/minecraft/world/level/ChunkPos;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.markPosition(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/ChunkStatus$ChunkType;)B"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.tick()V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.promoteChunkMap()Z"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.isExistingChunkFull(Lnet/minecraft/world/level/ChunkPos;)Z"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.removeEntity(Lnet/minecraft/world/entity/Entity;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.lambda$protoChunkToFullChunk$34(Lnet/minecraft/server/level/ChunkHolder;Lnet/minecraft/world/level/chunk/ChunkAccess;)Lnet/minecraft/world/level/chunk/ChunkAccess;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.lambda$getChunkRangeFuture$4(IIILjava/util/List;)Lcom/mojang/datafixers/util/Either;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.schedule(Lnet/minecraft/server/level/ChunkHolder;Lnet/minecraft/world/level/chunk/ChunkStatus;)Ljava/util/concurrent/CompletableFuture;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/RandomSequences.get(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/util/RandomSource;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/util/ThreadingDetector.checkAndLock()V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/util/ThreadingDetector.checkAndUnlock()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/TickingTracker.addTicket(JLnet/minecraft/server/level/Ticket;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/TickingTracker.removeTicket(JLnet/minecraft/server/level/Ticket;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/TickingTracker.getTickets(J)Lnet/minecraft/util/SortedArraySet;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerChunkCache.runDistanceManagerUpdates()Z"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerChunkCache.getChunkFutureMainThread(IILnet/minecraft/world/level/chunk/ChunkStatus;Z)Ljava/util/concurrent/CompletableFuture;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerChunkCache.getChunkNow(II)Lnet/minecraft/world/level/chunk/LevelChunk;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerChunkCache.removeEntity(Lnet/minecraft/world/entity/Entity;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerChunkCache.getChunk(IILnet/minecraft/world/level/chunk/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntitySection.add(Lnet/minecraft/world/level/entity/EntityAccess;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntitySection.remove(Lnet/minecraft/world/level/entity/EntityAccess;)Z"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntitySection.getEntities()Ljava/util/stream/Stream;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntitySection.getEntities(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)Lnet/minecraft/util/AbortableIterationConsumer$Continuation;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntitySection.getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)Lnet/minecraft/util/AbortableIterationConsumer$Continuation;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/util/thread/BlockableEventLoop.tell(Ljava/lang/Runnable;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/util/thread/BlockableEventLoop.pollTask()Z"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/util/thread/BlockableEventLoop.dropAllTasks()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/levelgen/NoiseBasedChunkGenerator.fillFromNoise(Ljava/util/concurrent/Executor;Lnet/minecraft/world/level/levelgen/blending/Blender;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/chunk/ChunkAccess;)Ljava/util/concurrent/CompletableFuture;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/storage/DimensionDataStorage.computeIfAbsent(Ljava/util/function/Function;Ljava/util/function/Supplier;Ljava/lang/String;)Lnet/minecraft/world/level/saveddata/SavedData;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/storage/DimensionDataStorage.get(Ljava/util/function/Function;Ljava/lang/String;)Lnet/minecraft/world/level/saveddata/SavedData;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/storage/DimensionDataStorage.save()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/storage/DimensionDataStorage.set(Ljava/lang/String;Lnet/minecraft/world/level/saveddata/SavedData;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/navigation/PathNavigation.recomputePath()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/navigation/PathNavigation.tick()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/navigation/PathNavigation.moveTo(Lnet/minecraft/world/level/pathfinder/Path;D)Z"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/navigation/PathNavigation.followThePath()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/navigation/PathNavigation.trimPath()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/navigation/PathNavigation.stop()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/navigation/PathNavigation.doStuckDetection(Lnet/minecraft/world/phys/Vec3;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/navigation/PathNavigation.shouldTargetNextNodeInDirection(Lnet/minecraft/world/phys/Vec3;)Z"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/navigation/PathNavigation.shouldRecomputePath(Lnet/minecraft/core/BlockPos;)Z"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager$PlayerTicketTracker.onLevelChange(JIZZ)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager$PlayerTicketTracker.onLevelChange(JII)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager$PlayerTicketTracker.runAllUpdates()V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager$PlayerTicketTracker.updateViewDistance(I)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.runAllUpdates(Lnet/minecraft/server/level/ChunkMap;)Z"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.addTicket(JLnet/minecraft/server/level/Ticket;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.removeTicket(JLnet/minecraft/server/level/Ticket;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.removeTicketsOnClosing()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.purgeStaleTickets()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.shouldForceTicks(J)Z"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.getTickets(J)Lnet/minecraft/util/SortedArraySet;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.getTicketDebugString(J)Ljava/lang/String;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.dumpTickets(Ljava/lang/String;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.addPlayer(Lnet/minecraft/core/SectionPos;Lnet/minecraft/server/level/ServerPlayer;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.removePlayer(Lnet/minecraft/core/SectionPos;Lnet/minecraft/server/level/ServerPlayer;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.getNaturalSpawnChunkCount()I"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.hasPlayersNearby(J)Z"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/DistanceManager.hasPlayersNearby(J)Z"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/util/ClassInstanceMultiMap.find(Ljava/lang/Class;)Ljava/util/Collection;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/util/ClassInstanceMultiMap.add(Ljava/lang/Object;)Z"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/util/ClassInstanceMultiMap.remove(Ljava/lang/Object;)Z"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/util/ClassInstanceMultiMap.contains(Ljava/lang/Object;)Z"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/util/ClassInstanceMultiMap.getAllInstances()Ljava/util/List;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ThreadedLevelLightEngine.runUpdate()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/lighting/DynamicGraphMinFixedPoint.checkEdge(JJIIIZ)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/lighting/DynamicGraphMinFixedPoint.removeIf(Ljava/util/function/LongPredicate;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/lighting/DynamicGraphMinFixedPoint.removeFromQueue(J)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/lighting/DynamicGraphMinFixedPoint.runUpdates(I)I"),
            //ForgeAsm.min_map.mapMethod("net/minecraft/server/level/ChunkTracker.update(JIZ)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/biome/BiomeSource.findBiomeHorizontal(IIIIILjava/util/function/Predicate;Lnet/minecraft/util/RandomSource;ZLnet/minecraft/world/level/biome/Climate$Sampler;)Lcom/mojang/datafixers/util/Pair;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntityLookup.add(Lnet/minecraft/world/level/entity/EntityAccess;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntityLookup.remove(Lnet/minecraft/world/level/entity/EntityAccess;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntitySectionStorage.getEntities(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntitySectionStorage.getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntitySectionStorage.forEachAccessibleNonEmptySection(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/util/AbortableIterationConsumer;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntitySectionStorage.getOrCreateSection(J)Lnet/minecraft/world/level/entity/EntitySection;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntitySectionStorage.count()I"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntitySectionStorage.remove(J)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntitySectionStorage.createSection(J)Lnet/minecraft/world/level/entity/EntitySection;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntitySectionStorage.getChunkSections(II)Lit/unimi/dsi/fastutil/longs/LongSortedSet;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntityTickList.add(Lnet/minecraft/world/entity/Entity;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntityTickList.remove(Lnet/minecraft/world/entity/Entity;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/EntityTickList.ensureActiveIsNotIterated()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/pathfinder/WalkNodeEvaluator.getCachedBlockType(Lnet/minecraft/world/entity/Mob;III)Lnet/minecraft/world/level/pathfinder/BlockPathTypes;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/pathfinder/WalkNodeEvaluator.done()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/pathfinder/FlyNodeEvaluator.done()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/pathfinder/FlyNodeEvaluator.getCachedBlockPathType(III)Lnet/minecraft/world/level/pathfinder/BlockPathTypes;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/pathfinder/FlyNodeEvaluator.prepare(Lnet/minecraft/world/level/PathNavigationRegion;Lnet/minecraft/world/entity/Mob;)V"),
            //ForgeAsm.min_map.mapMethod("net/minecraft/world/level/pathfinder/WalkNodeEvaluator.prepare(Lnet/minecraft/world/level/PathNavigationRegion;Lnet/minecraft/world/entity/Mob;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/pathfinder/PathFinder.findPath(Lnet/minecraft/world/level/PathNavigationRegion;Lnet/minecraft/world/entity/Mob;Ljava/util/Set;FIF)Lnet/minecraft/world/level/pathfinder/Path;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/storage/SectionStorage.tick(Ljava/util/function/BooleanSupplier;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/storage/SectionStorage.getOrLoad(J)Ljava/util/Optional;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/storage/SectionStorage.get(J)Ljava/util/Optional;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/storage/SectionStorage.getOrCreate(J)Ljava/lang/Object;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/storage/SectionStorage.readColumn(Lnet/minecraft/world/level/ChunkPos;Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/navigation/PathNavigation.recomputePath()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/navigation/PathNavigation.createPath(Ljava/util/Set;IZIF)Lnet/minecraft/world/level/pathfinder/Path;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/navigation/PathNavigation.moveTo(Lnet/minecraft/world/level/pathfinder/Path;D)Z"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/entity/TransientEntitySectionManager$Callback.onRemove(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelTicks.tick(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/ticks/LevelChunkTicks;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelTicks.addContainer(Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/ticks/LevelChunkTicks;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelTicks.clearArea(Lnet/minecraft/world/level/levelgen/structure/BoundingBox;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelTicks.updateContainerScheduling(Lnet/minecraft/world/ticks/ScheduledTick;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelTicks.removeContainer(Lnet/minecraft/world/level/ChunkPos;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelTicks.sortContainersToTick(J)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelTicks.cleanupAfterTick()V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelTicks.unpack(J)V"),// unpackTicks(J)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/Level.getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/Level.removeBlockEntity(Lnet/minecraft/core/BlockPos;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/Level.setBlockEntity(Lnet/minecraft/world/level/block/entity/BlockEntity;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/network/Connection.send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/network/Connection.sendPacket(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/network/ServerGamePacketListenerImpl.send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerPlayer.openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerPlayer.sendMerchantOffers(ILnet/minecraft/world/item/trading/MerchantOffers;IIZZ)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerPlayer.openHorseInventory(Lnet/minecraft/world/entity/animal/horse/AbstractHorse;Lnet/minecraft/world/Container;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerPlayer.openItemGui(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerPlayer.closeContainer()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerPlayer.doCloseContainer()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerPlayer.initInventoryMenu()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerPlayer.drop(Z)Z"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelChunkTicks.unpack(J)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelChunkTicks.save(JLjava/util/function/Function;)Lnet/minecraft/nbt/ListTag;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelChunkTicks.getAll()Ljava/util/stream/Stream;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelChunkTicks.removeIf(Ljava/util/function/Predicate;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelChunkTicks.peek()Lnet/minecraft/world/ticks/ScheduledTick;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelChunkTicks.poll()Lnet/minecraft/world/ticks/ScheduledTick;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelChunkTicks.schedule(Lnet/minecraft/world/ticks/ScheduledTick;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/ticks/LevelChunkTicks.scheduleUnchecked(Lnet/minecraft/world/ticks/ScheduledTick;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/AvoidEntityGoal.canUse()Z"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/AvoidEntityGoal.stop()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/AvoidEntityGoal.start()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/AvoidEntityGoal.tick()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/LivingEntity.dropAllDeathLoot(Lnet/minecraft/world/damagesource/DamageSource;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/Entity.updateInWaterStateAndDoFluidPushing()Z"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/Entity.shouldBeSaved()Z"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/Entity.saveAsPassenger(Lnet/minecraft/nbt/CompoundTag;)Z"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/Entity.unsetRemoved()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/Entity.baseTick()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/Entity.getFeetBlockState()Lnet/minecraft/world/level/block/state/BlockState;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/Entity.distanceToSqr(DDD)D"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/Entity.setRemoved(Lnet/minecraft/world/entity/Entity$RemovalReason;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/ChunkGenerator.getMobsAt(Lnet/minecraft/core/Holder;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/util/random/WeightedRandomList;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/PalettedContainer.onResize(ILjava/lang/Object;)I"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/PalettedContainer.read(Lnet/minecraft/network/FriendlyByteBuf;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/PalettedContainer.write(Lnet/minecraft/network/FriendlyByteBuf;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/PalettedContainer.acquire()V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/PalettedContainer.release()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/PalettedContainer.set(ILjava/lang/Object;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/PalettedContainer.lambda$codec$5(Lnet/minecraft/core/IdMap;Lnet/minecraft/world/level/chunk/PalettedContainer$Strategy;Lnet/minecraft/world/level/chunk/PalettedContainerRO;)Lnet/minecraft/world/level/chunk/PalettedContainerRO$PackedData;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/PalettedContainer.pack(Lnet/minecraft/core/IdMap;Lnet/minecraft/world/level/chunk/PalettedContainer$Strategy;)Lnet/minecraft/world/level/chunk/PalettedContainerRO$PackedData;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/PalettedContainer.unpack(Lnet/minecraft/core/IdMap;Lnet/minecraft/world/level/chunk/PalettedContainer$Strategy;Lnet/minecraft/world/level/chunk/PalettedContainerRO$PackedData;)Lcom/mojang/serialization/DataResult;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/WrappedGoal.canUse()Z"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/WrappedGoal.canContinueToUse()Z"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/WrappedGoal.isInterruptable()Z"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/WrappedGoal.start()V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/WrappedGoal.stop()V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/WrappedGoal.requiresUpdateEveryTick()Z"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/WrappedGoal.adjustedTickDelay(I)I"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/WrappedGoal.tick()V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/WrappedGoal.(Ljava/util/EnumSet;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/GoalSelector.addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/GoalSelector.removeAllGoals(Ljava/util/function/Predicate;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/ai/goal/GoalSelector.removeGoal(Lnet/minecraft/world/entity/ai/goal/Goal)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.runBlockEvents()V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.startTickingChunk(Lnet/minecraft/world/level/chunk/LevelChunk;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.blockEvent(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;II)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.neighborChanged(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;Z)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.neighborChanged(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.updateNeighborsAtExceptFromFacing(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/Direction;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.updateNeighborsAt(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.addEntity(Lnet/minecraft/world/entity/Entity;)Z"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.sendBlockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;I)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/redstone/CollectingNeighborUpdater.addAndRun(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/redstone/CollectingNeighborUpdater$NeighborUpdates;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/redstone/CollectingNeighborUpdater.runUpdates()V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/storage/SectionStorage.readColumn(Lnet/minecraft/world/level/ChunkPos;Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/storage/SectionStorage.writeColumn(Lnet/minecraft/world/level/ChunkPos;Lcom/mojang/serialization/DynamicOps;)Lcom/mojang/serialization/Dynamic;"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/storage/SectionStorage.setDirty(J)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/levelgen/LegacyRandomSource.setSeed(J)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/levelgen/LegacyRandomSource.nextGaussian()D"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/levelgen/LegacyRandomSource.next(I)I"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/levelgen/BitRandomSource.nextDouble()D"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/levelgen/BitRandomSource.nextFloat()F"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/levelgen/BitRandomSource.nextBoolean()Z"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/levelgen/BitRandomSource.nextLong()J"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/levelgen/BitRandomSource.nextInt()I"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/levelgen/BitRandomSource.nextInt(I)I"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/levelgen/ThreadSafeLegacyRandomSource.next(I)I"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkHolder.getOrScheduleFuture(Lnet/minecraft/world/level/chunk/ChunkStatus;Lnet/minecraft/server/level/ChunkMap;)Ljava/util/concurrent/CompletableFuture;")//,
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkHolder.getAllFutures()Ljava/util/List;"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel$EntityCallbacks.onTrackingStart(Lnet/minecraft/world/entity/Entity;)V"),
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel$EntityCallbacks.onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V")//,
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V")
    ));

    int posfilter = Opcodes.ACC_PUBLIC;
    int negfilter = /*Opcodes.ACC_STATIC |*/ Opcodes.ACC_SYNTHETIC/* | Opcodes.ACC_NATIVE */| Opcodes.ACC_ABSTRACT
            /*| Opcodes.ACC_ABSTRACT*/ | Opcodes.ACC_BRIDGE;


    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {

        for (String[] strings : stringsList) {
            if (input.name.equals(strings[0])){
                boolean debug_add1 = false;
                for (MethodNode method : input.methods) {
                    if ((method.name.equals(strings[1]) && method.desc.equals(strings[2]))) {
                        debug_add1 = true;
                        if ((input.access & Opcodes.ACC_STATIC) == 0){
                            if ((input.access & Opcodes.ACC_INTERFACE) == 0) {
                                log.info("add {} synchronized", Arrays.toString(strings));

                                if (!input.name.contains("$")) {
                                    method.access |= Opcodes.ACC_SYNCHRONIZED;
                                } else {
                                    String parent = null;
                                    String map = null;
                                    for (FieldNode fn : input.fields) {
                                        if (fn.name.equals("this$0") || ForgeAsm.srg$Forge$_map.mapField(input.name + "." + fn.name)[1].equals("this$0")) {

                                            map = fn.name;
                                            parent = fn.desc;
                                        }
                                    }
                                    if (parent == null || map == null) {
                                        method.access |= Opcodes.ACC_SYNCHRONIZED;
                                        log.error("Inner class faliure; parent not found " + (parent == null ? "null" : parent) + " " + (map == null ? "null" : map) + " " + Arrays.toString(strings));
                                        return input;
                                    }
                                    InsnList start;
                                    InsnList end;
                                    if ((method.access & negfilter) == 0 && !method.name.equals("<init>")) {
                                        start = new InsnList();
                                        start.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                        start.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, map, parent));
                                        start.add(new InsnNode(Opcodes.MONITORENTER));
                                        end = new InsnList();
                                        end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                        end.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, map, parent));
                                        end.add(new InsnNode(Opcodes.MONITOREXIT));
                                        InsnList il = method.instructions;
                                        AbstractInsnNode ain = il.getFirst();
                                        while (ain != null) {
                                            if (ain.getOpcode() == Opcodes.RETURN || ain.getOpcode() == Opcodes.ARETURN
                                                    || ain.getOpcode() == Opcodes.DRETURN || ain.getOpcode() == Opcodes.FRETURN
                                                    || ain.getOpcode() == Opcodes.IRETURN || ain.getOpcode() == Opcodes.LRETURN) {
                                                il.insertBefore(ain, end);
                                                end = new InsnList();
                                                end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                                end.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, map, parent));
                                                end.add(new InsnNode(Opcodes.MONITOREXIT));
                                            }
                                            ain = ain.getNext();
                                        }
                                        il.insertBefore(il.getFirst(), start);
                                    }
                                    log.info("sync_fu " + input.name + " InnerClass Transformer Complete");
                                }
                            } else {
                                log.info("add {} synchronized", Arrays.toString(strings));
                                InsnList start;
                                InsnList end;
                                if ((method.access & negfilter) == 0 && !method.name.equals("<init>")) {
                                    start = new InsnList();
                                    start.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                    start.add(new InsnNode(Opcodes.MONITORENTER));
                                    end = new InsnList();
                                    end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                    end.add(new InsnNode(Opcodes.MONITOREXIT));
                                    InsnList il = method.instructions;
                                    AbstractInsnNode ain = il.getFirst();
                                    while (ain != null) {
                                        if (ain.getOpcode() == Opcodes.RETURN || ain.getOpcode() == Opcodes.ARETURN
                                                || ain.getOpcode() == Opcodes.DRETURN || ain.getOpcode() == Opcodes.FRETURN
                                                || ain.getOpcode() == Opcodes.IRETURN || ain.getOpcode() == Opcodes.LRETURN) {
                                            il.insertBefore(ain, end);
                                            end = new InsnList();
                                            end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                            end.add(new InsnNode(Opcodes.MONITOREXIT));
                                        }
                                        ain = ain.getNext();
                                    }
                                    il.insertBefore(il.getFirst(), start);
                                }
                            }
                        } else {
                            input.access &= Opcodes.ACC_SYNTHETIC;
                        }
                    }
                }

                if (!debug_add1){
                    throw new RuntimeException("Not mapping error: " + Arrays.toString(strings));
                }
            }
        }


        return input;
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {

        File f = new File("config/K_multi_threading-sync-Method-list.txt");
        if (f.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(f))) {
                r.lines().filter(s -> !(s.startsWith("#") || s.startsWith("//") || s.equals("")))
                        .map(ForgeAsm.minecraft_map::mapMethod).forEach(stringsList::add);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();
                FileWriter fw = new FileWriter(f);
                fw.write("""
                        // 使用//或#屏蔽
                        // 这个文件是用于对单独的函数添加synchronized
                        
                        // 如何使用:
                        //net/minecraft/server/level/ServerChunkCache.removeEntity(Lnet/minecraft/world/entity/Entity;)V
                        //跟映射表格式一样simple
                        
                        """);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayList<String> list = new ArrayList<>();
        for (String[] strings : stringsList) {
            if (!list.contains(strings[0])) {
                list.add(strings[0]);
            }
        }

        return Set.of(list.stream().map(Target::targetClass).toArray(Target[]::new));
    }
}
