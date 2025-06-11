package asm.n1luik.KAllFix.asm.mod.jei;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.*;

@Slf4j
public class JEI_AddMapConcurrent_ASM implements ITransformer<ClassNode> {
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
            new AsmTarget("mezz.jei.library.plugins.vanilla.brewing.BrewingRecipeUtil", false),
            new AsmTarget("net.silentchaos512.gear.api.util.PartGearKey", false),
            new AsmTarget("net.silentchaos512.gear.gear.part.FakePartData", false),
            new AsmTarget("net.silentchaos512.gear.api.util.StatGearKey", false),
            new AsmTarget("net.silentchaos512.gear.api.part.PartType", false)
    ));
    public final Map<String, AsmTarget> nameMap = new HashMap<>();
    public final Map<String, List<String>> fieldMappings = new HashMap<>();
    //public final Map<String, List<String>> compatible = new HashMap<>();
    public final Map<String, String> typeMapping = new HashMap<>();
    {
        typeMapping.put("java/util/HashMap", "java/util/concurrent/ConcurrentHashMap");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/objects/Reference2ObjectOpenHashMap", "n1luik/K_multi_threading/core/util/concurrent/FalseReference2ObjectOpenHashMap");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/objects/Object2ObjectArrayMap", "java/util/concurrent/ConcurrentHashMap");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/objects/Reference2ReferenceArrayMap", "n1luik/K_multi_threading/core/util/concurrent/FalseReference2ReferenceConcurrentHashMap2");
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
        typeMapping.put("it/unimi/dsi/fastutil/longs/LongOpenHashSet", "it/unimi/dsi/fastutil/longs/LongSortedSet");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/objects/ObjectOpenHashSet", "it/unimi/dsi/fastutil/objects/ObjectSortedSet");
        //////////////////////////////////
        typeMapping.put("it/unimi/dsi/fastutil/objects/ReferenceLinkedOpenHashSet", "n1luik/K_multi_threading/core/util/concurrent/FalseReferenceLinkedOpenHashSet");

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
    }


    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        log.info("[{}]", input.name);
        AsmTarget orDefault = nameMap.getOrDefault(input.name, Empty);

        if (orDefault.mappingFields.length > 0) {
            for (FieldNode field : input.fields) {
                for (String s : orDefault.mappingFields) {
                    if (field.name.equals(s)) {
                        field.desc = typeMapping.getOrDefault(field.desc, field.desc);
                    }
                }
            }
        }
        for (MethodNode method : input.methods) {
            for (MethodInfo methodInfo : orDefault.methods) {
                if (methodInfo.test(method)) {
                    if (methodInfo.mappingLocal){
                        for (LocalVariableNode localVariableNode : method.localVariables) {
                            localVariableNode.desc = typeMapping.getOrDefault(localVariableNode.desc, localVariableNode.desc);
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
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/concurrent/CopyOnWriteArrayList", "<init>", "()V", false));
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "it/unimi/dsi/fastutil/objects/ReferenceLinkedOpenHashSet":
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/concurrent/FalseReferenceLinkedOpenHashSet", "<init>", "()V", false));
                                    } else {
                                        method.instructions.add(instruction);
                                    }
                                    break;
                                case "it/unimi/dsi/fastutil/longs/LongOpenHashSet":
                                case "java/util/HashSet":
                                case "it/unimi/dsi/fastutil/objects/ObjectArraySet":
                                case "it/unimi/dsi/fastutil/objects/ObjectOpenHashSet":
                                    if (methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                        method.instructions.add(new InsnNode(Opcodes.POP));
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
                                case "it/unimi/dsi/fastutil/objects/Reference2ReferenceArrayMap" -> {
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
                                case "java/util/ArrayList" ->
                                        method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/util/concurrent/CopyOnWriteArrayList"));
                                case "it/unimi/dsi/fastutil/objects/ObjectOpenHashSet" ->
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                                "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
                                                "concurrentObjectSet",
                                                "()Lit/unimi/dsi/fastutil/objects/ObjectSortedSet;"));
                                case "it/unimi/dsi/fastutil/longs/LongOpenHashSet" ->
                                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                                "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
                                                "concurrentLongSet",
                                                "()Lit/unimi/dsi/fastutil/longs/LongSortedSet;"));
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
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {

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
            List<String> stringStringMap = fieldMappings.computeIfAbsent(asmTarget.className.replace(".", "/"), k -> new ArrayList<>());
            stringStringMap.addAll(Arrays.asList(asmTarget.mappingFields));

        }
        return Set.of(stringsList.stream().map(AsmTarget::className).map(Target::targetClass).toArray(Target[]::new));
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
