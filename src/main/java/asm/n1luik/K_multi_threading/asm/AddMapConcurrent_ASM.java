package asm.n1luik.K_multi_threading.asm;

import asm.n1luik.K_multi_threading.asm.mod.valkyrienskies.AddMapConcurrent;
import asm.n1luik.K_multi_threading.asm.util.AsmApi;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import cpw.mods.modlauncher.TransformingClassLoader;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import lombok.extern.slf4j.Slf4j;
import n1luik.K_multi_threading.core.util.NodeMap.Int2ObjectNodeHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@Slf4j
public class AddMapConcurrent_ASM extends ITransformer2 {
    /*public static BiFunction<TransformingClassLoader, String, byte[]> getclass;

    static {
        try {
            Method buildTransformedClassNodeFor = TransformingClassLoader.class.getDeclaredMethod("buildTransformedClassNodeFor", String.class, String.class);
            buildTransformedClassNodeFor.setAccessible(true);
            getclass = (transformingClassLoader, s) ->
            {
                try {
                    return (byte[]) buildTransformedClassNodeFor.invoke(transformingClassLoader, s, s);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }*/
    private final static AsmTarget Empty = new AsmTarget("", false);
    public final List<AsmTarget> stringsList = new ArrayList<>(List.of(
            //new AsmTarget("net.minecraft.world.level.storage.DimensionDataStorage", true),
            new AsmTarget("me.wesley1808.servercore.mixin.optimizations.ticking.chunk.broadcast.ServerChunkCacheMixin", false
            //        , new String[]{"servercore$requiresBroadcast"}
            //        ,new MethodInfo[]{
            //        new MethodInfo("servercore$broadcastChanges", null, true, true),
            //        new MethodInfo("servercore$requiresBroadcast", null, true, true)
            //}
            ),
            new AsmTarget("com.abdelaziz.canary.mixin.collections.entity_filtering.ClassInstanceMultiMapMixin", false
                    , new String[]{}
                    ,new MethodInfo[]{
                    new MethodInfo("createAllOfType", null, true, true)
            }
            ),
            new AsmTarget("com.abdelaziz.canary.mixin.entity.inactive_navigations.ServerLevelMixin", false
                    , new String[]{"activeNavigations"}
                    ,new MethodInfo[]{
                    new MethodInfo("init", null, true, true),
                    new MethodInfo("isConsistent", null, true, true),
                    new MethodInfo("updateActiveListeners", null, true, true),
                    new MethodInfo("setNavigationInactive", null, true, true),
                    new MethodInfo("setNavigationActive", null, true, true)
            }
            ),
            new AsmTarget("me.jellysquid.mods.lithium.mixin.collections.entity_filtering.TypeFilterableListMixin", false
                    , new String[]{}
                    ,new MethodInfo[]{
                    new MethodInfo("createAllOfType", null, true, true)
            }
            ),
            new AsmTarget("me.jellysquid.mods.lithium.mixin.entity.inactive_navigations.ServerWorldMixin", false
                    , new String[]{"activeNavigations"}
                    ,new MethodInfo[]{
                    new MethodInfo("init", null, true, true),
                    new MethodInfo("isConsistent", null, true, true),
                    new MethodInfo("updateActiveListeners", null, true, true),
                    new MethodInfo("setNavigationInactive", null, true, true),
                    new MethodInfo("setNavigationActive", null, true, true)
            }
            ),
            //new AsmTarget("net.minecraft.world.level.gameevent.EuclideanGameEventListenerRegistry", false),
            //
            //Caused by: java.lang.UnsupportedOperationException
            //	at java.util.concurrent.CopyOnWriteArrayList$COWIterator.remove(CopyOnWriteArrayList.java:1208) ~[?:?]
            //	at net.minecraft.world.level.gameevent.EuclideanGameEventListenerRegistry.m_245521_(EuclideanGameEventListenerRegistry.java:71) ~[server-1.20.1-20230612.114412-srg.jar%23113!/:?]
            //
            new AsmTarget("dev.ryanhcode.sable.physics.chunk.VoxelNeighborhoodState$1", false, new String[]{"cache"}, new MethodInfo[]{
                    new MethodInfo(null, null, true, true)
            }),
            new AsmTarget("net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage", false),
            new AsmTarget("mcjty.incontrol.rules.support.RuleCache", false),
            new AsmTarget("com.petrolpark.contamination.IntrinsicContaminants", false),
            new AsmTarget("net.minecraft.server.level.PlayerMap", false),
            new AsmTarget("net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.ServerStorageSoundHandler", false),
            new AsmTarget("mekanism.common.recipe.lookup.cache.type.BaseInputCache", false),
            new AsmTarget("mekanism.common.lib.transmitter.TransmitterNetworkRegistry", false),
            new AsmTarget("mekanism.common.content.network.transmitter.LogisticalTransporterBase", false),
            new AsmTarget("net.minecraft.world.level.chunk.ChunkAccess", false),
            new AsmTarget("net.minecraft.util.ClassInstanceMultiMap", false),
            new AsmTarget("dev.ryanhcode.sable.physics.chunk.VoxelNeighborhoodState$2", false, new String[]{"cache"}, new MethodInfo[]{
                    new MethodInfo(null, null, true, true)
            }),
            new AsmTarget("com.enderio.enderio.content.conduits.network.ConduitNetworkImpl", false, new String[0], new MethodInfo[]{
                    new MethodInfo("lambda$addTickingNode$8", null, false, false),
                    new MethodInfo("lambda$addTickingNode$9", null, false, false),
                    new MethodInfo("lambda$addTickingNode$10", null, false, false),
                    new MethodInfo("lambda$addTickingNode$11", null, false, false),
                    new MethodInfo("lambda$addTickingNode$12", null, false, false),
                    new MethodInfo("lambda$addTickingNode$13", null, false, false),
                    new MethodInfo("lambda$addTickingNode$14", null, false, false),
                    new MethodInfo("lambda$addTickingNode$15", null, false, false),
                    new MethodInfo("<init>", null, false, false),
                    new MethodInfo("<clinit>", null, false, false)
            }),
            new AsmTarget("it.hurts.sskirillss.relics.utils.ServerScheduler", false),
            new AsmTarget("dev.xkmc.l2damagetracker.contents.attack.DamageAccumulator", false),
            new AsmTarget("net.minecraft.world.level.entity.EntitySectionStorage", false),
            new AsmTarget("net.minecraft.world.level.entity.EntityLookup", false),
            new AsmTarget("appeng.hooks.ticking.TickHandler", true),
            new AsmTarget("appeng.me.service.PathingService", false),
            new AsmTarget("appeng.me.cells.BasicCellInventory", false, new String[]{},
                    new MethodInfo[]{
                            new MethodInfo("getCellItems", null, true, false)
                    }),
            new AsmTarget("dev.ftb.mods.ftbquests.util.DeferredInventoryDetection", false, new String[]{"playerMap"},
                    new MethodInfo[]{
                            new MethodInfo(null, null, true, true)
                    }),
            new AsmTarget("net.minecraft.world.entity.ai.village.poi.PoiManager", false),
            new AsmTarget("me.desht.pneumaticcraft.common.drone.DroneClaimManager", false),
            new AsmTarget("com.github.alexthe666.iceandfire.entity.util.MyrmexHive", false),
            new AsmTarget("baguchan.tofucraft.CommonEvents", false),
            new AsmTarget("com.github.alexthe666.alexsmobs.event.ServerEvents", false),
            new AsmTarget("com.teammoeg.caupona.CPCommonBootStrap", false),
            new AsmTarget("me.jellysquid.mods.lithium.mixin.collections.entity_by_type.TypeFilterableListMixin", false),
            new AsmTarget("net.caffeinemc.mods.lithium.mixin.ai.poi.SectionStorageMixin", false),
            new AsmTarget("com.abdelaziz.canary.mixin.collections.entity_by_type.ClassInstanceMultiMapMixin", false),
            new AsmTarget("net.minecraft.world.level.levelgen.structure.StructureCheck", false),
            new AsmTarget("net.minecraft.world.level.block.ComposterBlock", false),
            new AsmTarget("net.minecraft.world.entity.ai.attributes.AttributeInstance", false),
            new AsmTarget("mods.railcraft.charge.ChargeProviderImpl", false, new String[]{"networks"}, AsmTarget.EMPTY_METHODS5),
            new AsmTarget("reliquary.item.MobCharmItem$CharmInventoryHandler", false),
            new AsmTarget("mcjty.lostcities.worldgen.GlobalTodo", false),
            new AsmTarget("mcjty.lostcities.worldgen.lost.cityassets.AssetRegistries", false),
            new AsmTarget("net.minecraft.util.profiling.ActiveProfiler", false),
            new AsmTarget("com.github.alexthe668.domesticationinnovation.server.CommonProxy", false),
            new AsmTarget("com.chunksending.mixin.ServerPlayerChunkSending", false),
            new AsmTarget("ydmsama.hundred_years_war.main.entity.utils.PathingTaskManagerRegistry", false),
            new AsmTarget("harmonised.pmmo.registry.PerkRegistry", false),
            new AsmTarget("dev.architectury.impl.NetworkAggregator", false),
            new AsmTarget("com.st0x0ef.stellaris.common.oxygen.GlobalOxygenManager", false),
            new AsmTarget("me.desht.pneumaticcraft.common.event.PneumaticArmorHandler", false),
            new AsmTarget("com.cozary.nameless_trinkets.utils.MaxHealthFix", false),
            new AsmTarget("top.ribs.scguns.entity.raid.RaidManager", false),
            new AsmTarget("forge.net.mca.server.world.data.FamilyTree", false),
            new AsmTarget("com.wintercogs.beyonddimensions.api.storage.handler.impl.StackHandler", false),
            new AsmTarget("net.minecraft.client.sounds.SoundBufferLibrary", false),
            new AsmTarget("rearth.oritech.api.networking.NetworkManager", false),
            new AsmTarget("com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity", false, new String[0],
                    new MethodInfo[]{
                            new MethodInfo("<init>", null, true, false),
                            new MethodInfo("<clinit", null, true, false),
                            new MethodInfo("lambda$addTravellingPackage$0", null, true, true)
                    }),
            new AsmTarget("net.caffeinemc.mods.lithium.mixin.collections.entity_filtering.ClassInstanceMultiMapMixin", false, new String[0],
                    new MethodInfo[]{
                            new MethodInfo("<init>", null, true, false),
                            new MethodInfo("<clinit", null, true, false),
                            new MethodInfo("createAllOfType", null, true, true)
                    }),
            new AsmTarget("net.minecraft.world.level.block.RedstoneTorchBlock", false, new String[0],
                    new MethodInfo[]{
                            new MethodInfo(null, null, false, false)
                    }),
            new AsmTarget("com.simibubi.create.content.kinetics.belt.BeltHelper", false),
            new AsmTarget("com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorRoutingTable", false),
            new AsmTarget("com.simibubi.create.content.processing.basin.BasinBlockEntity.BasinBlockEntity", false),
            new AsmTarget("com.simibubi.create.foundation.sound.SoundScapes", false),
            new AsmTarget("com.simibubi.create.content.kinetics.TorquePropagator", false),
            new AsmTarget("com.simibubi.create.content.logistics.depot.DepotBehaviour", false),
            new AsmTarget("com.simibubi.create.content.fluids.FluidTransportBehaviour", false, new String[0],
                    new MethodInfo[]{
                            new MethodInfo(null, null, false, false)
                    }),
            new AsmTarget("com.simibubi.create.content.kinetics.belt.BeltBlockEntity", false, new String[0],
                    new MethodInfo[]{
                            new MethodInfo("tick", null, true, true)
                    }),
            new AsmTarget("com.simibubi.create.foundation.advancement.CriterionTriggerBase", true),
            new AsmTarget("com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler", false, new String[0],
                    new MethodInfo[]{new MethodInfo("onLoadWorld", null, true, true)}),
            new AsmTarget("com.railwayteam.railways.registry.advancement.CriterionTriggerBase", true),
            new AsmTarget("com.lightning.northstar.advancements.CriterionTriggerBase2ElectricBoogaloo", true),
            new AsmTarget("com.simibubi.create.content.fluids.FluidNetwork", true),
            new AsmTarget("blusunrize.immersiveengineering.common.util.WorldMap", false, new String[0], AsmTarget.EMPTY_METHODS4),
            new AsmTarget("com.hlysine.create_connected.datagen.advancements.CriterionTriggerBase", false),
            new AsmTarget("plus.dragons.createenchantmentindustry.dragonLibLegacy.advancement.critereon.AbstractTrigger", false),
            new AsmTarget("dev.ithundxr.createnumismatics.registry.advancement.CriterionTriggerBase", false),
            new AsmTarget("org.cyclops.integrateddynamics.core.network.PositionedAddonsNetwork", false),
            new AsmTarget("com.teamabnormals.blueprint.common.advancement.EmptyTrigger", false),
            new AsmTarget("net.mehvahdjukaar.supplementaries.common.misc.CooperativePistonData", false),
            new AsmTarget("com.refinedmods.refinedstorage.common.support.resource.list.FuzzyResourceListImpl", false),
            new AsmTarget("de.devin.pipesnphysics.engine.EngineTickHandler", false),
            new AsmTarget("appeng.me.service.CraftingService", false),
            new AsmTarget("appeng.api.stacks.KeyCounter", true)
    ));
    public final Map<String, AsmTarget> nameMap = new HashMap<>();
    public final Map<String, List<String>> fieldMappings = new HashMap<>();
    //public final Map<String, List<String>> compatible = new HashMap<>();
    public final Map<String, String> typeMapping = new HashMap<>();
    {
        if (AsmApi.mcVersion.startsWith("1.21")){
            stringsList.add(new AsmTarget("com.tacz.guns.entity.sync.core.SyncedEntityData", false));
            stringsList.add(new AsmTarget("net.minecraft.server.PlayerAdvancements", false));
            stringsList.add(new AsmTarget("net.minecraft.world.entity.ai.attributes.AttributeMap", false));
        }
        typeMapping.put("java/util/HashMap", "java/util/concurrent/ConcurrentHashMap");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/objects/ReferenceOpenHashSet", "n1luik/K_multi_threading/core/util/concurrent/FalseReferenceOpenHashSet");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/objects/Reference2ObjectOpenHashMap", "n1luik/K_multi_threading/core/util/concurrent/FalseReference2ObjectOpenHashMap");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/objects/Object2ObjectArrayMap", "java/util/concurrent/ConcurrentHashMap");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/objects/Reference2ReferenceArrayMap", "n1luik/K_multi_threading/core/util/concurrent/FalseReference2ReferenceConcurrentHashMap2");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/objects/Reference2ReferenceOpenHashMap", "n1luik/K_multi_threading/core/util/concurrent/FalseReference2ReferenceConcurrentHashMap2");
        //////////////////////////////////
        typeMapping.put("java/util/HashSet", "java/util/concurrent/ConcurrentHashMap$KeySetView");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/objects/ObjectArraySet", "java/util/concurrent/ConcurrentHashMap$KeySetView");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/ints/Int2ObjectLinkedOpenHashMap", "n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2ObjectLinkedOpenHashMap");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap", "n1luik/K_multi_threading/core/util/concurrent/ConcurrentLong2ObjectOpenHashMap");
        //////////////////////////////////
        typeMapping.put("java/util/ArrayList", "java/util/concurrent/CopyOnWriteArrayList");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/longs/LongOpenHashSet", "n1luik/K_multi_threading/core/util/concurrent/LongConcurrentHashSet");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/objects/ObjectOpenHashSet", "it/unimi/dsi/fastutil/objects/ObjectSortedSet");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/objects/ReferenceLinkedOpenHashSet", "n1luik/K_multi_threading/core/util/concurrent/FalseReferenceLinkedOpenHashSet");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/objects/Object2BooleanOpenHashMap", "n1luik/K_multi_threading/core/util/concurrent/Object2BooleanConcurrentHashMap");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/longs/Long2ByteOpenHashMap", "n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2IntMap");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/ints/Int2IntOpenHashMap", "n1luik/K_multi_threading/core/util/concurrent/ConcurrentLong2ByteMap");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/ints/Int2ObjectOpenHashMap", "n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2ObjectMap");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/ints/Int2BooleanOpenHashMap", "n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2BooleanMap");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/objects/Object2LongOpenHashMap", "n1luik/K_multi_threading/core/util/concurrent/ConcurrentObject2LongMap");

        /*compatible.put("java/util/concurrent/ConcurrentHashMap", Stream.of(
                "java/util/concurrent/ConcurrentHashMap",
                "java.util.AbstractMap",
                "java.util.concurrent.ConcurrentMap",
                "java.io.Serializable",
                "java.util.Map",
                "java.lang.Object"
        ).map(string -> string.replace(".", "/")).toList());
        compatible.put("java/util/concurrent/ConcurrentHashMap$KeySetView", Stream.of(
                "java/util/concurrent/ConcurrentHashMap$KeySetView",
                "java.util.concurrent.ConcurrentHashMap$CollectionView",
                "java.util.Set",
                "java.io.Serializable",
                "java.util.Collection",
                "java.lang.Iterable",
                "java.lang.Object"
        ).map(string -> string.replace(".", "/")).toList());
        compatible.put("n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2ObjectLinkedOpenHashMap", Stream.of(
                "n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2ObjectLinkedOpenHashMap",
                "it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap",
                "java.io.Serializable",
                "java.lang.Cloneable",
                "it.unimi.dsi.fastutil.Hash",
                "it.unimi.dsi.fastutil.ints.AbstractInt2ObjectSortedMap",
                "it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap",
                "it.unimi.dsi.fastutil.ints.Int2ObjectMap",
                "it.unimi.dsi.fastutil.ints.Int2ObjectFunction",
                "java.util.Map",
                "java.util.SortedMap",
                "java.lang.Object"
        ).map(string -> string.replace(".", "/")).toList());
        compatible.put("n1luik/K_multi_threading/core/util/concurrent/ConcurrentLong2ObjectOpenHashMap", Stream.of(
                "n1luik/K_multi_threading/core/util/concurrent/ConcurrentLong2ObjectOpenHashMap",
                "it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap",
                "java.io.Serializable",
                "java.lang.Cloneable",
                "it.unimi.dsi.fastutil.Hash",
                "it.unimi.dsi.fastutil.longs.AbstractLong2ObjectMap",
                "it.unimi.dsi.fastutil.longs.Long2ObjectMap",
                "java.util.Map",
                "it.unimi.dsi.fastutil.longs.Long2ObjectFunction",
                "it.unimi.dsi.fastutil.Function",
                "it.unimi.dsi.fastutil.longs.AbstractLong2ObjectFunction",
                "java.lang.Object"
        ).map(string -> string.replace(".", "/")).toList());
        compatible.put("java/util/concurrent/CopyOnWriteArrayList", Stream.of(
                "java/util/concurrent/CopyOnWriteArrayList",
                "java.lang.Cloneable",
                "java.util.RandomAccess",
                "java.io.Serializable",
                "java.util.List",
                "java.util.Collection",
                "java.lang.Iterable",
                "java.lang.Object"
        ).map(string -> string.replace(".", "/")).toList());
        compatible.put("it/unimi/dsi/fastutil/longs/LongSortedSet", Stream.of(
                "it/unimi/dsi/fastutil/longs/LongSortedSet",
                "it.unimi.dsi.fastutil.longs.LongIterable",
                "it.unimi.dsi.fastutil.longs.LongSet",
                "it.unimi.dsi.fastutil.longs.LongCollection",
                "it.unimi.dsi.fastutil.longs.LongIterable",
                "java.util.SortedSet",
                "it.unimi.dsi.fastutil.longs.LongBidirectionalIterable",
                "java.util.Set",
                "java.util.Collection",
                "java.lang.Iterable",
                "java.lang.Object"
        ).map(string -> string.replace(".", "/")).toList());
        compatible.put("it/unimi/dsi/fastutil/objects/ObjectSortedSet", Stream.of(
                "it/unimi/dsi/fastutil/objects/ObjectSortedSet",
                "it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable",
                "it.unimi.dsi.fastutil.objects.ObjectIterable",
                "java.lang.Iterable",
                "java.util.SortedSet",
                "java.util.Set",
                "java.util.Collection",
                "it.unimi.dsi.fastutil.objects.ObjectSet",
                "it.unimi.dsi.fastutil.objects.ObjectCollection",
                "it.unimi.dsi.fastutil.objects.ObjectIterable",

                "java.lang.Object"
        ).map(string -> string.replace(".", "/")).toList());*/
        AddMapConcurrent.init(this);

    }
    public String descMap(String desc){
        int array = 0;
        while (desc.charAt(array) == '['){
            array++;
        }
        if (desc.charAt(array) == 'L') {
            String desc1 = typeMapping.get(desc.substring(array + 1, desc.length() - 1));
            if (desc1 != null){
                return  "[".repeat(array) + "L" + desc1 + ";";
            }
        }
        return desc;
    }


    @Override
    public @NotNull ClassNode transform(ClassNode input) {
        log.info("[{}]", input.name);
        AsmTarget orDefault = nameMap.getOrDefault(input.name, Empty);

        if (orDefault.mappingFields.length > 0) {
            for (FieldNode field : input.fields) {
                for (String s : orDefault.mappingFields) {
                    if (field.name.equals(s)) {
                        field.desc = descMap(field.desc);
                    }
                }
            }
            for (MethodNode methodNode : input.methods) {
                for (AbstractInsnNode instruction : methodNode.instructions) {
                    if (instruction instanceof FieldInsnNode fieldInsnNode) {
                        List<String> strings = fieldMappings.get(fieldInsnNode.owner);
                        if (strings != null) {
                            if (strings.contains(fieldInsnNode.name)){
                                fieldInsnNode.desc = descMap(fieldInsnNode.desc);
                            }
                        }
                    }
                }

            }

        }
        for (MethodNode method : input.methods) {
            for (MethodInfo methodInfo : orDefault.methods) {
                if (methodInfo.test(method)) {
                    if (methodInfo.mappingLocal){
                        for (LocalVariableNode localVariableNode : method.localVariables) {
                            localVariableNode.desc = descMap(localVariableNode.desc);
                        }
                    }
                    AbstractInsnNode[] abstractInsnNodes = method.instructions.toArray();
                    method.instructions.clear();
                    boolean add = false;
                    for (AbstractInsnNode instruction : abstractInsnNodes) {
                        if (instruction.getOpcode() == Opcodes.INVOKESTATIC && instruction instanceof MethodInsnNode methodInsnNode) {
                            switch (methodInsnNode.owner) {
                                case "com/google/common/collect/Maps":
                                    if (methodInsnNode.name.equals("newHashMap")) {
                                        add = true;
                                        if (orDefault.fixNull) {
                                            method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/FixNullConcurrentHashMap"));
                                            method.instructions.add(new InsnNode(Opcodes.DUP));
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/FixNullConcurrentHashMap", "<init>", "()V", false));
                                        } else {
                                            method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/ConcurrentHashMap"));
                                            method.instructions.add(new InsnNode(Opcodes.DUP));
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/ConcurrentHashMap", "<init>", "()V", false));
                                        }
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "com/google/common/collect/Sets":
                                    if (methodInsnNode.name.equals("newHashSet")) {
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/concurrent/ConcurrentHashMap", "newKeySet", "()Ljava/util/concurrent/ConcurrentHashMap$KeySetView;", false));
                                        //add = true;
                                        //method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/CopyOnWriteArraySet"));
                                        //method.instructions.add(new InsnNode(Opcodes.DUP));
                                        //method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/CopyOnWriteArraySet", "<init>", "()V", false));
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "com/google/common/collect/Lists":
                                    if (methodInsnNode.name.equals("newArrayList")) {
                                        add = true;
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/CopyOnWriteArrayList"));
                                        method.instructions.add(new InsnNode(Opcodes.DUP));
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/CopyOnWriteArrayList", "<init>", "()V", false));
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                default:
                                    method.instructions.add(instruction);

                                    break;
                            }
                        } else if (instruction.getOpcode() == Opcodes.INVOKESPECIAL && instruction instanceof MethodInsnNode methodInsnNode) {
                            switch (methodInsnNode.owner) {
                                case "it/unimi/dsi/fastutil/ints/Int2ObjectLinkedOpenHashMap":
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2ObjectLinkedOpenHashMap", "<init>", "()V", false));
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "it/unimi/dsi/fastutil/objects/Reference2ReferenceArrayMap":
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        if (orDefault.fixNull) {
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/FalseReference2ReferenceConcurrentHashMap2$FixNull", "<init>", "()V", false));
                                        } else {
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/FalseReference2ReferenceConcurrentHashMap2", "<init>", "()V", false));
                                        }
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "it/unimi/dsi/fastutil/objects/Reference2ReferenceOpenHashMap":
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        if (orDefault.fixNull) {
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/FalseReference2ReferenceConcurrentHashMap2$FixNull", "<init>", "()V", false));
                                        } else {
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/FalseReference2ReferenceConcurrentHashMap2", "<init>", "()V", false));
                                        }
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "it/unimi/dsi/fastutil/objects/Object2ObjectArrayMap":
                                case "java/util/HashMap":
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        if (orDefault.fixNull) {
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/FixNullConcurrentHashMap", "<init>", "()V", false));
                                        } else {
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/ConcurrentHashMap", "<init>", "()V", false));
                                        }
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "it/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap":
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentLong2ObjectOpenHashMap", "<init>", "()V", false));
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "java/util/ArrayList":
                                    if (methodInsnNode.name.equals("<init>")) {
                                        if (methodInsnNode.desc.equals("()V")) {
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/CopyOnWriteArrayList", "<init>", "()V", false));
                                        } else if (methodInsnNode.desc.equals("(I)V")) {
                                            method.instructions.add(new InsnNode(Opcodes.POP));
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/CopyOnWriteArrayList", "<init>", "()V", false));
                                        }else {
                                            method.instructions.add(instruction);
                                        }
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "it/unimi/dsi/fastutil/objects/ReferenceLinkedOpenHashSet":
                                    if (methodInsnNode.name.equals("<init>")) {
                                        if (methodInsnNode.desc.equals("()V")){
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/FalseReferenceLinkedOpenHashSet", "<init>", "()V", false));
                                        }else if (methodInsnNode.desc.equals("(I)V")){
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/FalseReferenceLinkedOpenHashSet", "<init>", "(I)V", false));
                                        }else {
                                            method.instructions.add(instruction);
                                        }
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "it/unimi/dsi/fastutil/objects/ReferenceOpenHashSet":
                                    if (methodInsnNode.name.equals("<init>")) {
                                        if (methodInsnNode.desc.equals("()V")){
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/FalseReferenceOpenHashSet", "<init>", "()V", false));
                                        }else if (methodInsnNode.desc.equals("(Ljava/util/Collection;)V")){
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/FalseReferenceOpenHashSet", "<init>", "(Ljava/util/Collection;)V", false));
                                        }else {
                                            method.instructions.add(instruction);
                                        }
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "it/unimi/dsi/fastutil/objects/Object2BooleanOpenHashMap":
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/Object2BooleanConcurrentHashMap", "<init>", "()V", false));
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "it/unimi/dsi/fastutil/longs/Long2ByteOpenHashMap":
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentLong2ByteMap", "<init>", "()V", false));
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "it/unimi/dsi/fastutil/ints/Int2IntOpenHashMap":
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2IntMap", "<init>", "()V", false));
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "it/unimi/dsi/fastutil/ints/Int2ObjectOpenHashMap":
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2ObjectMap", "<init>", "()V", false));
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "it/unimi/dsi/fastutil/ints/Int2BooleanOpenHashMap":
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2BooleanMap", "<init>", "()V", false));
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "it/unimi/dsi/fastutil/objects/Object2LongOpenHashMap":
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentObject2LongMap", "<init>", "()V", false));
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "it/unimi/dsi/fastutil/longs/LongOpenHashSet":
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/LongConcurrentHashSet", "<init>", "()V", false));
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "java/util/HashSet":
                                case "it/unimi/dsi/fastutil/objects/ObjectArraySet":
                                case "it/unimi/dsi/fastutil/objects/ObjectOpenHashSet":
                                    if (methodInsnNode.name.equals("<init>")) {
                                        if (methodInsnNode.desc.equals("()V")) {
                                            method.instructions.add(new InsnNode(Opcodes.POP));
                                        } else if(methodInsnNode.desc.equals("(Ljava/util/Collection;)V")){
                                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, methodInsnNode.owner, "addAll", methodInsnNode.desc));
                                        } else {
                                            method.instructions.add(instruction);
                                        }
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                default:
                                    method.instructions.add(instruction);

                                    break;
                            }
                        } else if (instruction.getOpcode() == Opcodes.NEW && instruction instanceof TypeInsnNode typeInsnNode) {
                            switch (typeInsnNode.desc) {
                                case "it/unimi/dsi/fastutil/ints/Int2ObjectLinkedOpenHashMap" ->
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2ObjectLinkedOpenHashMap"));
                                case "java/util/HashMap",
                                     "it/unimi/dsi/fastutil/objects/Object2ObjectArrayMap"-> {
                                    if (orDefault.fixNull) {
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/FixNullConcurrentHashMap"));
                                    } else {
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/ConcurrentHashMap"));
                                    }
                                }
                                case "it/unimi/dsi/fastutil/objects/Reference2ReferenceArrayMap",
                                     "it/unimi/dsi/fastutil/objects/Reference2ReferenceOpenHashMap" -> {
                                    if (orDefault.fixNull) {
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/FalseReference2ReferenceConcurrentHashMap2$FixNull"));
                                    } else {
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/FalseReference2ReferenceConcurrentHashMap2"));
                                    }
                                }
                                case "it/unimi/dsi/fastutil/longs/Long2ObjectOpenHashMap" ->
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentLong2ObjectOpenHashMap"));
                                case "it/unimi/dsi/fastutil/objects/ReferenceLinkedOpenHashSet" ->
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/FalseReferenceLinkedOpenHashSet"));
                                case "it/unimi/dsi/fastutil/objects/ReferenceOpenHashSet" ->
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/FalseReferenceOpenHashSet"));
                                case "it/unimi/dsi/fastutil/objects/Object2BooleanOpenHashMap" ->
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/Object2BooleanConcurrentHashMap"));
                                case "it/unimi/dsi/fastutil/longs/Long2ByteOpenHashMap" ->
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentLong2ByteMap"));
                                case "it/unimi/dsi/fastutil/ints/Int2IntOpenHashMap" ->
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2IntMap"));
                                case "it/unimi/dsi/fastutil/ints/Int2BooleanOpenHashMap" ->
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2BooleanMap"));
                                case "it/unimi/dsi/fastutil/ints/Int2ObjectOpenHashMap" ->
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2ObjectMap"));
                                case "it/unimi/dsi/fastutil/objects/Object2LongOpenHashMap" ->
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/ConcurrentObject2LongMap"));
                                case "java/util/ArrayList" ->
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/CopyOnWriteArrayList"));
                                case "it/unimi/dsi/fastutil/objects/ObjectOpenHashSet" ->
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                                "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
                                                "concurrentObjectSet",
                                                "()Lit/unimi/dsi/fastutil/objects/ObjectSortedSet;"));
                                case "it/unimi/dsi/fastutil/longs/LongOpenHashSet" ->
                                        //method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                        //        "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
                                        //        "concurrentLongSet",
                                        //        "()Lit/unimi/dsi/fastutil/longs/LongSortedSet;"));
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/concurrent/LongConcurrentHashSet"));
                                case "java/util/HashSet",
                                     "it/unimi/dsi/fastutil/objects/ObjectArraySet" ->
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                                "java/util/concurrent/ConcurrentHashMap",
                                                "newKeySet",
                                                "()Ljava/util/concurrent/ConcurrentHashMap$KeySetView;",
                                                false));
                                default -> method.instructions.add(instruction);
                            }
                        } else {
                            method.instructions.add(instruction);
                        }
                    }
                    if (methodInfo.mappingAll) {
                        for (AbstractInsnNode instruction : abstractInsnNodes) {
                            switch (instruction.getOpcode()) {
                                case Opcodes.PUTFIELD, Opcodes.GETFIELD -> {
                                    if (instruction instanceof FieldInsnNode fieldInsnNode){
                                        if (fieldInsnNode.desc.startsWith("L")) {
                                            String substring = fieldInsnNode.desc.substring(1, fieldInsnNode.desc.length() - 1);
                                            fieldInsnNode.desc = "L"+typeMapping.getOrDefault(substring, substring)+";";
                                        }
                                    }
                                }
                                case Opcodes.INVOKEINTERFACE, Opcodes.INVOKEVIRTUAL, Opcodes.INVOKESTATIC, Opcodes.INVOKESPECIAL -> {
                                    if (instruction instanceof MethodInsnNode methodInsnNode){
                                        methodInsnNode.owner = typeMapping.getOrDefault(methodInsnNode.owner, methodInsnNode.owner);
                                    }
                                }
                                case Opcodes.INVOKEDYNAMIC -> {
                                    if (instruction instanceof InvokeDynamicInsnNode node){
                                        for (int i = 0; i < node.bsmArgs.length; i++) {
                                            Object bsmArg = node.bsmArgs[i];
                                            if (bsmArg instanceof Handle handle) {
                                                node.bsmArgs[i] = new Handle(handle.getTag(), typeMapping.getOrDefault(handle.getOwner(), handle.getOwner()), handle.getName(), handle.getDesc(), handle.isInterface());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (add) {
                        method.maxStack++;
                    }
                }
            }
        }
        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {

        File f = new File("config/K_multi_threading-AddMapConcurrent-list.txt");
        if (f.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(f))) {
                r.lines().filter(s -> !(s.startsWith("#") || s.startsWith("//") || s.isEmpty()))
                        .map(n->{
                            String[] split = n.split("\\|", 4);
                            if (split.length < 2) throw new RuntimeException();
                            MethodInfo[] methods;
                            String[] blacklistField;
                            if (split.length > 2) {
                                if (split.length > 3) {
                                    String[] split1 = split[3].split(":");
                                    methods = new MethodInfo[split1.length];
                                    for (int i = 0; i < split1.length; i++) {
                                        String s = split1[i];
                                        if (s.isEmpty()) throw new RuntimeException();
                                        String[] split2 = s.split(",", 3);
                                        boolean mappingLocal;
                                        boolean mappingAll;
                                        if (split2.length > 1) {
                                            mappingLocal = Boolean.parseBoolean(split2[1]);
                                            if (split2.length > 2) {
                                                mappingAll = Boolean.parseBoolean(split2[2]);
                                            }else {
                                                mappingAll = false;
                                            }
                                        } else {
                                            mappingLocal = false;
                                            mappingAll = false;
                                        }
                                        String s1 = split2[0];
                                        if (s1.isEmpty()) {
                                            methods = mappingLocal ? AsmTarget.EMPTY_METHODS3 : AsmTarget.EMPTY_METHODS2;
                                            break;
                                        }
                                        //可以直接判断是不是只有desc
                                        if (s1.charAt(0) == '(') {
                                            methods[i] = new MethodInfo(null, s1, mappingLocal, mappingAll);
                                        } else {
                                            if (s1.contains("(")) {
                                                String[] split3 = s1.split("(?=\\()", 2);
                                                methods[i] = new MethodInfo(split3[0], split3[1], mappingLocal, mappingAll);
                                            } else {
                                                methods[i] = new MethodInfo(s1, null, mappingLocal, mappingAll);
                                            }
                                        }
                                    }
                                }else {
                                    methods = AsmTarget.EMPTY_METHODS;
                                }
                                if (split[2].isEmpty()){
                                    blacklistField = AsmTarget.EMPTY_STRING_ARRAY;
                                }else {
                                    blacklistField = split[2].split(":");
                                }

                            }else {
                                blacklistField = AsmTarget.EMPTY_STRING_ARRAY;
                                methods = AsmTarget.EMPTY_METHODS;
                            }
                            return new AsmTarget(split[0], Boolean.parseBoolean(split[1]), blacklistField, methods);
                        })
                        .forEach(stringsList::add);
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
                        #类名|是否修复null|变量映射列表|函数名字(可以没有desc),映射局部变量默认false,映射全部的匹配类型默认false:下一个函数名
                        #函数名字如果是,[false, true]就会直接处理所有的函数
                        
                        // 如何使用:
                        //net/minecraft/server/level/ServerChunkCache|false
                        //net/minecraft/server/level/ServerChunkCache|false||
                        //net/minecraft/server/level/ServerChunkCache|false||init,true
                        //net/minecraft/server/level/ServerChunkCache|false||init()V,true:
                        //net/minecraft/server/level/ServerChunkCache|false||init()V,true,false:
                        //net/minecraft/server/level/ServerChunkCache|false||()V:
                        //net/minecraft/server/level/ServerChunkCache|false|A:b|,true
                        //net/minecraft/server/level/ServerChunkCache|false|A:b|,true,false
                        
                        """);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (AsmTarget asmTarget : stringsList) {
            nameMap.put(asmTarget.className.replace(".", "/"), asmTarget);
            nameMap.put(asmTarget.className, asmTarget);
        }
        for (AsmTarget asmTarget : stringsList) {
            if (asmTarget.mappingFields.length < 1) continue;
            List<String> stringStringMap = fieldMappings.computeIfAbsent(asmTarget.className.replace(".", "/"), k -> new ArrayList<>(asmTarget.mappingFields.length));
            stringStringMap.addAll(Arrays.asList(asmTarget.mappingFields));

        }
        return Set.of(stringsList.stream().map(AsmTarget::className).toArray(String[]::new));
    }

    public static record MethodInfo(@Nullable String name, @Nullable String desc, boolean mappingLocal, boolean mappingAll) {
        public boolean test(MethodNode node){
            return (name == null || name.equals(node.name)) && (desc == null || desc.equals(node.desc));
        }
    }
    public static record AsmTarget(String className, boolean fixNull, String[] mappingFields, MethodInfo[] methods) {
        private static final MethodInfo[] EMPTY_METHODS =  new MethodInfo[]{new MethodInfo("<init>", null, false, false), new MethodInfo("<clinit>", null, false, false)};
        private static final MethodInfo[] EMPTY_METHODS2 =  new MethodInfo[]{new MethodInfo(null, null, false, false)};
        private static final MethodInfo[] EMPTY_METHODS3 =  new MethodInfo[]{new MethodInfo(null, null, true, false)};
        private static final MethodInfo[] EMPTY_METHODS4 =  new MethodInfo[]{new MethodInfo(null, null, false, true)};
        private static final MethodInfo[] EMPTY_METHODS5 =  new MethodInfo[]{new MethodInfo(null, null, true, true)};
        private static final String[] EMPTY_STRING_ARRAY =  new String[0];
        public AsmTarget(String className, boolean fixNull) {
            this(className, fixNull, EMPTY_STRING_ARRAY, EMPTY_METHODS);
        }
    }

}
