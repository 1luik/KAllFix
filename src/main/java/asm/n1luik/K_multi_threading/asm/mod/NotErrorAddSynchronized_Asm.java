package asm.n1luik.K_multi_threading.asm.mod;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
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
public class NotErrorAddSynchronized_Asm implements ITransformer<ClassNode> {
    public static final List<String[]> stringsList = new ArrayList<>(List.of(
            //机械动力
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/kinetics/KineticNetwork.addSilently(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;FF)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/kinetics/KineticNetwork.updateCapacityFor(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;F)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/kinetics/KineticNetwork.updateStressFor(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;F)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/kinetics/KineticNetwork.remove(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/kinetics/KineticNetwork.calculateCapacity()F"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/kinetics/KineticNetwork.calculateStress()F"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/kinetics/KineticNetwork.sync()V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/kinetics/KineticNetwork.add(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/kinetics/base/KineticBlockEntity.validateKinetics()V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/kinetics/base/KineticBlockEntity.clearKineticInformation()V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/kinetics/base/KineticBlockEntity.removeSource()V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/kinetics/base/KineticBlockEntity.setSpeed(F)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/trains/graph/TrackGraph.addPoint(Lcom/simibubi/create/content/trains/graph/EdgePointType;Lcom/simibubi/create/content/trains/signal/TrackEdgePoint;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/trains/graph/TrackGraph.removePoint(Lcom/simibubi/create/content/trains/graph/EdgePointType;Ljava/util/UUID;)Lcom/simibubi/create/content/trains/signal/TrackEdgePoint;"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/trains/graph/TrackGraph.addNode(Lcom/simibubi/create/content/trains/graph/TrackNode;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/trains/graph/TrackGraph.addNodeIfAbsent(Lcom/simibubi/create/content/trains/graph/TrackNode;)Z"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/trains/graph/TrackGraph.removeNode(Lnet/minecraft/world/level/LevelAccessor;Lcom/simibubi/create/content/trains/graph/TrackNodeLocation;)Z"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/trains/graph/TrackGraph.transferAll(Lcom/simibubi/create/content/trains/graph/TrackGraph;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/trains/graph/TrackGraph.findDisconnectedGraphs(Lnet/minecraft/world/level/LevelAccessor;Ljava/util/Map;)Ljava/util/Set;"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/trains/graph/TrackGraph.getChecksum()I"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/trains/graph/TrackGraph.transfer(Lnet/minecraft/world/level/LevelAccessor;Lcom/simibubi/create/content/trains/graph/TrackNode;Lcom/simibubi/create/content/trains/graph/TrackGraph;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/trains/graph/TrackGraph.distanceToLocationSqr(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;)F"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/trains/graph/TrackGraph.getBounds(Lnet/minecraft/world/level/Level;)Lcom/simibubi/create/content/trains/graph/TrackGraphBounds;"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/trains/graph/TrackGraph.invalidateBounds()V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/trains/graph/TrackGraph.connectNodes(Lnet/minecraft/world/level/LevelAccessor;Lcom/simibubi/create/content/trains/graph/TrackNodeLocation$DiscoveredLocation;Lcom/simibubi/create/content/trains/graph/TrackNodeLocation$DiscoveredLocation;Lcom/simibubi/create/content/trains/track/BezierConnection;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/trains/graph/TrackGraph.locateNode(Lcom/simibubi/create/content/trains/graph/TrackNodeLocation;)Lcom/simibubi/create/content/trains/graph/TrackNode;"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/trains/graph/TrackGraph.createNodeIfAbsent(Lcom/simibubi/create/content/trains/graph/TrackNodeLocation$DiscoveredLocation;)Z"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/foundation/utility/WorldAttached.invalidateWorld(Lnet/minecraft/world/level/LevelAccessor;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/foundation/utility/WorldAttached.get(Lnet/minecraft/world/level/LevelAccessor;)Ljava/lang/Object;"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/foundation/utility/WorldAttached.replace(Lnet/minecraft/world/level/LevelAccessor;)Ljava/lang/Object;"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/foundation/utility/WorldAttached.replace(Lnet/minecraft/world/level/LevelAccessor;Ljava/util/function/Consumer;)Ljava/lang/Object;"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/foundation/utility/WorldAttached.put(Lnet/minecraft/world/level/LevelAccessor;Ljava/lang/Object;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/foundation/utility/WorldAttached.empty(Ljava/util/function/BiConsumer;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/foundation/utility/WorldAttached.empty(Ljava/util/function/Consumer;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/logistics/depot/DepotBehaviour.applyToAllItems(FLjava/util/function/Function;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/logistics/depot/DepotBehaviour.read(Lnet/minecraft/nbt/CompoundTag;Z)V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/logistics/depot/DepotBehaviour.removeHeldItem()V"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/logistics/depot/DepotBehaviour.handleBeltFunnelOutput()Z"),
            ForgeAsm.minecraft_map.mapMethod("com/simibubi/create/content/logistics/depot/DepotBehaviour.tick()Z"),
            //瓦尔基里
            ForgeAsm.minecraft_map.mapMethod("org/valkyrienskies/core/impl/game/ships/ShipObjectServerWorld.clearNewUpdatedDeletedShipObjectsAndVoxelUpdates()V"),
            //brandonscore
            ForgeAsm.minecraft_map.mapMethod("com/brandon3055/brandonscore/handlers/contributor/ContributorFetcher.onTick(Lnet/minecraftforge/event/TickEvent;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/brandon3055/brandonscore/handlers/contributor/ContributorFetcher.queTask(Lcom/brandon3055/brandonscore/handlers/contributor/ContributorFetcher$ThreadedTask;)V"),
            //alexsmobs alex的动物
            ForgeAsm.minecraft_map.mapMethod("com/github/alexthe666/iceandfire/entity/util/MyrmexHive.repopulate()Z"),
            ForgeAsm.minecraft_map.mapMethod("com/github/alexthe666/iceandfire/entity/util/MyrmexHive.addRoomWithMessage(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lcom/github/alexthe666/iceandfire/world/gen/WorldGenMyrmexHive$RoomType;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/github/alexthe666/iceandfire/entity/util/MyrmexHive.addEnteranceWithMessage(Lnet/minecraft/world/entity/player/Player;ZLnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/github/alexthe666/iceandfire/entity/util/MyrmexHive.getRandomRoom(Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;"),
            ForgeAsm.minecraft_map.mapMethod("com/github/alexthe666/iceandfire/entity/util/MyrmexHive.getRandomRoom(Lcom/github/alexthe666/iceandfire/world/gen/WorldGenMyrmexHive$RoomType;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;"),
            ForgeAsm.minecraft_map.mapMethod("com/github/alexthe666/iceandfire/entity/util/MyrmexHive.getAllRooms()Ljava/util/List;"),
            ForgeAsm.minecraft_map.mapMethod("com/github/alexthe666/alexsmobs/entity/EntityFly$AnnoyZombieGoal.m_8037_()V"),
            ForgeAsm.minecraft_map.mapMethod("com/github/alexthe666/alexsmobs/entity/EntityFly$AnnoyZombieGoal.m_8036_()Z"),
            ForgeAsm.minecraft_map.mapMethod("com/github/alexthe666/alexsmobs/entity/EntityFly$AnnoyZombieGoal.m_8041_()V"),
            //气动工艺
            ForgeAsm.minecraft_map.mapMethod("me/desht/pneumaticcraft/common/drone/DroneClaimManager.getInstance(Lnet/minecraft/world/level/Level;)Lme/desht/pneumaticcraft/common/drone/DroneClaimManager;"),
            ForgeAsm.minecraft_map.mapMethod("me/desht/pneumaticcraft/common/amadron/AmadronOfferManager.compileActiveOffersList()V"),
            ForgeAsm.minecraft_map.mapMethod("me/desht/pneumaticcraft/common/amadron/AmadronOfferManager.addPlayerOffer(Lme/desht/pneumaticcraft/common/recipes/amadron/AmadronPlayerOffer;)Z"),
            ForgeAsm.minecraft_map.mapMethod("me/desht/pneumaticcraft/common/amadron/AmadronOfferManager.removePlayerOffer(Lme/desht/pneumaticcraft/common/recipes/amadron/AmadronPlayerOffer;)Z"),
            ForgeAsm.minecraft_map.mapMethod("me/desht/pneumaticcraft/common/amadron/AmadronOfferManager.syncOffers(Ljava/util/Collection;Z)V"),
            ForgeAsm.minecraft_map.mapMethod("me/desht/pneumaticcraft/common/amadron/AmadronOfferManager.updateStock(Lnet/minecraft/resources/ResourceLocation;I)V"),
            ForgeAsm.minecraft_map.mapMethod("me/desht/pneumaticcraft/common/amadron/AmadronOfferManager.countPlayerOffers(Lava/util/UUID;I)V"),
            ForgeAsm.minecraft_map.mapMethod("me/desht/pneumaticcraft/common/amadron/AmadronOfferManager.addPlayerOffers()V"),
            ForgeAsm.minecraft_map.mapMethod("me/desht/pneumaticcraft/common/amadron/AmadronOfferManager.pickRandomPeriodicTrade(Ljava/util/Random;)Lme/desht/pneumaticcraft/api/crafting/recipe/AmadronRecipe;"),
            ForgeAsm.minecraft_map.mapMethod("me/desht/pneumaticcraft/common/amadron/AmadronOfferManager.pickRandomVillagerTrade(Lnet/minecraft/world/entity/npc/VillagerProfession;Ljava/util/Random;)Ljava/util/Optional;"),
            ForgeAsm.minecraft_map.mapMethod("me/desht/pneumaticcraft/common/amadron/AmadronOfferManager.setupVillagerTrades()V"),
            ForgeAsm.minecraft_map.mapMethod("me/desht/pneumaticcraft/common/amadron/AmadronOfferManager.checkForFullRebuild(Lnet/minecraft/world/level/Level;)V"),
            //瓦尔基里
            ForgeAsm.minecraft_map.mapMethod("org/valkyrienskies/core/impl/util/assertions/stages/TickStageEnforcerImpl.stage(Ljava/lang/Object;)V"),
            //gtceu
            ForgeAsm.minecraft_map.mapMethod("com/regtechceu/gtceu/api/machine/MetaMachine.serverTick()V"),
            ForgeAsm.minecraft_map.mapMethod("com/regtechceu/gtceu/api/machine/MetaMachine.executeTick()V"),
            ForgeAsm.minecraft_map.mapMethod("com/regtechceu/gtceu/api/machine/MetaMachine.onUnload()V"),
            ForgeAsm.minecraft_map.mapMethod("com/regtechceu/gtceu/api/machine/MetaMachine.subscribeServerTick(Ljava/lang/Runnable;)Lcom/gregtechceu/gtceu/api/machine/TickableSubscription;"),
            //ApothicAttributes
            ForgeAsm.minecraft_map.mapMethod("dev/shadowsoffire/attributeslib/mixin/AttributeMapMixin.apoth_attrModifiedEvent(Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V"),
            ForgeAsm.minecraft_map.mapMethod("dev/shadowsoffire/attributeslib/mixin/AttributeMapMixin.setAttributesUpdating(Z)V"),
            ForgeAsm.minecraft_map.mapMethod("dev/shadowsoffire/attributeslib/mixin/AttributeMapMixin.areAttributesUpdating()V"),
            //lithium
            ForgeAsm.minecraft_map.mapMethod("me/jellysquid/mods/lithium/mixin/world/tick_scheduler/ChunkTickSchedulerMixin.m_183393_(Lnet/minecraft/world/ticks/ScheduledTick;)V"),
            ForgeAsm.minecraft_map.mapMethod("me/jellysquid/mods/lithium/mixin/world/tick_scheduler/ChunkTickSchedulerMixin.queueTick(Lnet/minecraft/world/ticks/ScheduledTick;)V"),
            ForgeAsm.minecraft_map.mapMethod("me/jellysquid/mods/lithium/mixin/world/tick_scheduler/ChunkTickSchedulerMixin.m_193189_()Lnet/minecraft/world/ticks/ScheduledTick;"),
            ForgeAsm.minecraft_map.mapMethod("me/jellysquid/mods/lithium/mixin/world/tick_scheduler/ChunkTickSchedulerMixin.m_193195_()Lnet/minecraft/world/ticks/ScheduledTick;"),
            ForgeAsm.minecraft_map.mapMethod("me/jellysquid/mods/lithium/mixin/world/tick_scheduler/ChunkTickSchedulerMixin.m_183582_(Lnet/minecraft/core/BlockPos;Ljava/lang/Object;)Z"),
            ForgeAsm.minecraft_map.mapMethod("me/jellysquid/mods/lithium/mixin/world/tick_scheduler/ChunkTickSchedulerMixin.m_193183_(Ljava/util/function/Predicate;)V"),
            ForgeAsm.minecraft_map.mapMethod("me/jellysquid/mods/lithium/mixin/world/tick_scheduler/ChunkTickSchedulerMixin.m_193196_()Ljava/util/stream/Stream;"),
            ForgeAsm.minecraft_map.mapMethod("me/jellysquid/mods/lithium/mixin/world/tick_scheduler/ChunkTickSchedulerMixin.m_183574_()I"),
            ForgeAsm.minecraft_map.mapMethod("me/jellysquid/mods/lithium/mixin/world/tick_scheduler/ChunkTickSchedulerMixin.m_183237_(JLjava/util/function/Function;)Lnet/minecraft/nbt/ListTag;"),
            ForgeAsm.minecraft_map.mapMethod("me/jellysquid/mods/lithium/mixin/world/tick_scheduler/ChunkTickSchedulerMixin.m_193171_(J)V"),
            //Mowzie的生物
            ForgeAsm.minecraft_map.mapMethod("com/bobmowzie/mowziesmobs/server/capability/AbilityCapability$AbilityCapabilityImp.instanceAbilities(Lnet/minecraft/world/entity/LivingEntity;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/bobmowzie/mowziesmobs/server/capability/AbilityCapability$AbilityCapabilityImp.tick(Lnet/minecraft/world/entity/LivingEntity;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/bobmowzie/mowziesmobs/server/capability/AbilityCapability$AbilityCapabilityImp.activateAbility(Lnet/minecraft/world/entity/LivingEntity;Lcom/bobmowzie/mowziesmobs/server/ability/AbilityType;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/bobmowzie/mowziesmobs/server/capability/AbilityCapability$AbilityCapabilityImp.getAbilityFromType(Lcom/bobmowzie/mowziesmobs/server/ability/AbilityType;)Lcom/bobmowzie/mowziesmobs/server/ability/Ability;"),
            ForgeAsm.minecraft_map.mapMethod("com/bobmowzie/mowziesmobs/server/capability/AbilityCapability$AbilityCapabilityImp.setActiveAbility(Lcom/bobmowzie/mowziesmobs/server/ability/Ability;)V"),
            //ae2
            //ForgeAsm.minecraft_map.mapMethod("appeng/me/service/PathingService.onServerEndTick()V"),
            //ForgeAsm.minecraft_map.mapMethod("appeng/me/service/PathingService.repath()V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/menu/me/common/MEStorageMenu.m_38946_()V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/hooks/ticking/TickHandler.addCallable(Lnet/minecraft/world/level/LevelAccessor;Lappeng/util/ILevelRunnable;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/storage/NetworkStorage.isQueuedForRemoval(Lappeng/api/storage/MEStorage;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/storage/NetworkStorage.getAvailableStacks(Lappeng/api/stacks/KeyCounter;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/storage/NetworkStorage.extract(Lappeng/api/stacks/AEKey;JLappeng/api/config/Actionable;Lappeng/api/networking/security/IActionSource;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/storage/NetworkStorage.flushQueuedOperations()V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/storage/NetworkStorage.mount(ILappeng/api/storage/MEStorage;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/storage/NetworkStorage.unmount(Lappeng/api/storage/MEStorage;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/storage/NetworkStorage.insert(Lappeng/api/stacks/AEKey;JLappeng/api/config/Actionable;Lappeng/api/networking/security/IActionSource;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/StorageService.postWatcherUpdate(Lappeng/api/stacks/AEKey;J)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/StorageService.addNode(Lappeng/api/networking/IGridNode;Lnet/minecraft/nbt/CompoundTag;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/StorageService.removeNode(Lappeng/api/networking/IGridNode;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/StorageService.updateCachedStacks()V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/StorageService.onServerEndTick()V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/StorageService.refreshGlobalStorageProvider(Lappeng/api/storage/IStorageProvider;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/StorageService.addGlobalStorageProvider(Lappeng/api/storage/IStorageProvider;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/StorageService.removeGlobalStorageProvider(Lappeng/api/storage/IStorageProvider;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/TickManagerService.tickLevelQueue(Lnet/minecraft/world/level/Level;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/TickManagerService.getQueue(Lnet/minecraft/world/level/Level;)Ljava/util/riorityQueue;"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/TickManagerService.removeFromQueue(Lappeng/api/networking/IGridNode;Lappeng/me/service/helpers/TickTracker;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/TickManagerService.getStatus(Lappeng/api/networking/IGridNode;)Lappeng/me/service/TickManagerService$NodeStatus;"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/TickManagerService.alertDevice(Lappeng/api/networking/IGridNode;)Z"),
            ForgeAsm.minecraft_map.mapMethod("appeng/parts/CableBusContainer.updateAfterPartChange(Lnet/minecraft/core/Direction;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/Grid.size()I"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/Grid.remove(Lappeng/me/GridNode;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/Grid.add(Lappeng/me/GridNode;Lnet/minecraft/nbt/CompoundTag;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/Grid.getMachines(Ljava/lang/Class;)Ljava/util/Set;"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/Grid.getActiveMachines(Ljava/lang/Class;)Ljava/util/Set;"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/Grid.notifyAllNodes(Lappeng/api/networking/IGridNodeListener$State;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/EnergyService.injectProviderPower(DLappeng/api/config/Actionable;)D"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/EnergyService.addRequester(Lappeng/api/networking/energy/IAEPowerStorage;)V"),
            ForgeAsm.minecraft_map.mapMethod("appeng/me/service/EnergyService.removeRequester(Lappeng/api/networking/energy/IAEPowerStorage;)V"),
            //永恒枪械工坊
            ForgeAsm.minecraft_map.mapMethod("com/tacz/guns/entity/sync/core/SyncedEntityData.hasSyncedDataKey(Ljava/lang/Class;)Z"),
            ForgeAsm.minecraft_map.mapMethod("com/tacz/guns/entity/sync/core/SyncedEntityData.set(Lnet/minecraft/world/entity/Entity;Lcom/tacz/guns/entity/sync/core/SyncedDataKey;Ljava/lang/Object;)V"),
            ForgeAsm.minecraft_map.mapMethod("com/tacz/guns/entity/sync/core/SyncedEntityData.get(Lnet/minecraft/world/entity/Entity;Lcom/tacz/guns/entity/sync/core/SyncedDataKey;)Ljava/lang/Object;"),
            //mek
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/acceptor/NetworkAcceptorCache.hasAcceptor()Z"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/acceptor/NetworkAcceptorCache.getAcceptorCount()I"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/acceptor/NetworkAcceptorCache.deregister()V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/acceptor/NetworkAcceptorCache.commit()V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/acceptor/NetworkAcceptorCache.acceptorChanged(Lmekanism/common/content/network/transmitter/Transmitter;Lnet/minecraft/core/Direction;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/acceptor/NetworkAcceptorCache.updateTransmitterOnSide(Lmekanism/common/content/network/transmitter/Transmitter;Lnet/minecraft/core/Direction;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/acceptor/NetworkAcceptorCache.adoptAcceptors(Lmekanism/common/lib/transmitter/acceptor/NetworkAcceptorCache;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/TransmitterNetworkRegistry.registerChangedNetwork(Lmekanism/common/lib/transmitter/DynamicNetwork;)V"),
            //ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/TransmitterNetworkRegistry.onTick(Lnet/minecraftforge/event/TickEvent$ServerTickEvent;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/TransmitterNetworkRegistry.removeNetwork(Lmekanism/common/lib/transmitter/DynamicNetwork;)V"),
            //ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/TransmitterNetworkRegistry.onTicketLevelChange(Lnet/minecraftforge/event/level/ChunkTicketLevelUpdatedEvent;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/TransmitterNetworkRegistry.registerNetwork(Lmekanism/common/lib/transmitter/DynamicNetwork;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/TransmitterNetworkRegistry.toComponents()[Lnet/minecraft/network/chat/Component;"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/TransmitterNetworkRegistry.removeInvalidTransmitters()V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/TransmitterNetworkRegistry.handleChangedChunks()V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/TransmitterNetworkRegistry.assignOrphans()V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/TransmitterNetworkRegistry.commitChanges()V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/TransmitterNetworkRegistry.reset()V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/TransmitterNetworkRegistry.trackTransmitter(Lmekanism/common/content/network/transmitter/Transmitter;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/TransmitterNetworkRegistry.untrackTransmitter(Lmekanism/common/content/network/transmitter/Transmitter;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/TransmitterNetworkRegistry.invalidateTransmitter(Lmekanism/common/content/network/transmitter/Transmitter;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/TransmitterNetworkRegistry.registerOrphanTransmitter(Lmekanism/common/content/network/transmitter/Transmitter;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/DynamicNetwork.commit()V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/DynamicNetwork.addNewTransmitters(Ljava/util/Collection;Lmekanism/common/lib/transmitter/CompatibleTransmitterValidator;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/DynamicNetwork.addTransmitterFromCommit(Lmekanism/common/content/network/transmitter/Transmitter;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/DynamicNetwork.invalidate(Lmekanism/common/content/network/transmitter/Transmitter;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/DynamicNetwork.removeInvalid(Lmekanism/common/content/network/transmitter/Transmitter;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/DynamicNetwork.removeTransmitter(Lmekanism/common/content/network/transmitter/Transmitter;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/DynamicNetwork.addTransmitter(Lmekanism/common/content/network/transmitter/Transmitter;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/DynamicNetwork.adoptTransmittersAndAcceptorsFrom(Lmekanism/common/lib/transmitter/DynamicNetwork;)Ljava/util/List;"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/transmitter/DynamicNetwork.deregister()V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/content/blocktype/BlockType.add([Lmekanism/common/block/attribute/Attribute;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/content/blocktype/BlockType.setFrom(Lmekanism/common/content/blocktype/BlockTypeTile;Ljava/lang/Class;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/content/blocktype/BlockType.remove([Ljava/lang/Class;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/multiblock/Structure.add(Lmekanism/common/lib/multiblock/Structure;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/math/voxel/VoxelPlane.merge(Lmekanism/common/lib/math/voxel/VoxelPlane;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/multiblock/Structure.getMinorAxisMap(Lmekanism/common/lib/multiblock/Structure$Axis;)Ljava/util/NavigableMap;"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/multiblock/Structure.getMajorAxisMap(Lmekanism/common/lib/multiblock/Structure$Axis;)Ljava/util/NavigableMap;"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/multiblock/Structure.markForUpdate(Lnet/minecraft/world/level/Level;Z)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/multiblock/Structure.tick(Lnet/minecraft/world/level/block/entity/BlockEntity;Z)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/multiblock/Structure.setMultiblockData(Lmekanism/common/lib/multiblock/MultiblockData;)V"),
            ForgeAsm.minecraft_map.mapMethod("mekanism/common/lib/multiblock/Structure.removeMultiblock(Lnet/minecraft/world/level/Level;)V")
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
                        if ((input.access & Opcodes.ACC_STATIC) == 0) {
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
                    log.warn("Not mapping error: {}" , Arrays.toString(strings));
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

        File f = new File("config/K_multi_threading-sync-ModMethod-list.txt");
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
