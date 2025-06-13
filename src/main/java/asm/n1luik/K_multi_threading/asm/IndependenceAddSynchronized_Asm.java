package asm.n1luik.K_multi_threading.asm;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.*;


@Slf4j
public class IndependenceAddSynchronized_Asm implements ITransformer<ClassNode> {
    public static final List<String[]> stringsList = new ArrayList<>(List.<String[]>of(
            //ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.startTickingChunk(Lnet/minecraft/world/level/chunk/LevelChunk;)V")
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.startTickingChunk(Lnet/minecraft/world/level/chunk/LevelChunk;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkHolder.broadcastChanges(Lnet/minecraft/world/level/chunk/LevelChunk;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ChunkMap.move(Lnet/minecraft/server/level/ServerPlayer;)V"),
            ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/Level.addFreshBlockEntities(Ljava/util/Collection;)V")
    ));

    int posfilter = Opcodes.ACC_PUBLIC;
    int negfilter = Opcodes.ACC_STATIC | /*Opcodes.ACC_SYNTHETIC | Opcodes.ACC_NATIVE |*/ Opcodes.ACC_ABSTRACT
            /*| Opcodes.ACC_ABSTRACT*/ | Opcodes.ACC_BRIDGE;


    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        try {

            for (String[] strings : stringsList) {
                if (input.name.equals(strings[0]) && !input.name.contains("$")) {
                    boolean debug_add1 = false;
                    boolean debug_add2 = false;
                    String name = "K_multi_threading$locked$" + strings[1] + "L" +
                            Integer.toString((strings[2].hashCode())).replace("-", "_");//.substring(1)
                    //.replaceAll("[;)/\\[]", "");
                    if ((input.access & (Opcodes.ACC_INTERFACE | Opcodes.ACC_STATIC)) == 0) {
                        for (MethodNode method : input.methods) {//防止没有函数然后还添加了，会导致浪费一定的资源
                            if ((method.name.equals(strings[1]) && method.desc.equals(strings[2]))) {
                                debug_add2 = true;
                                input.visitField(Opcodes.ACC_PRIVATE/* | Opcodes.ACC_FINAL*/, name
                                        , "Ljava/lang/Object;", null, null);
                                for (MethodNode method2 : input.methods) {
                                    if (method2.name.equals("<init>")) {
                                        AbstractInsnNode[] array = method2.instructions.toArray();
                                        InsnList instructions = new InsnList();
                                        method2.instructions = instructions;
                                        for (AbstractInsnNode instruction : array) {
                                            //确认是不是当前类的init
                                            if (instruction instanceof MethodInsnNode methodInsnNode && methodInsnNode.name.equals("<init>")) {
                                                if (methodInsnNode.owner.equals(input.superName)) {
                                                    instructions.add(instruction);
                                                    instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                                    instructions.add(new TypeInsnNode(Opcodes.NEW, "java/lang/Object"));
                                                    instructions.add(new InsnNode(Opcodes.DUP));
                                                    instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false));
                                                    instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, input.name, name, "Ljava/lang/Object;"));

                                                } else instructions.add(instruction);
                                            } else instructions.add(instruction);
                                        }
                                        method2.maxStack+=2;
                                    }
                                }
                                break;
                            }
                        }
                    }

                    for (MethodNode method : input.methods) {
                        if ((method.name.equals(strings[1]) && method.desc.equals(strings[2]))) {
                            debug_add1 = true;
                            log.info("add {} synchronized", Arrays.toString(strings));

                            InsnList start;
                            InsnList end;
                            if ((input.access & Opcodes.ACC_INTERFACE) == 0) {
                                if ((input.access & Opcodes.ACC_STATIC) == 0) {
                                    if ((method.access & negfilter) == 0 && !method.name.equals("<init>")) {
                                        start = new InsnList();
                                        start.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                        start.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, name, "Ljava/lang/Object;"));
                                        start.add(new InsnNode(Opcodes.MONITORENTER));
                                        end = new InsnList();
                                        end.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                        end.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, name, "Ljava/lang/Object;"));
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
                                                end.add(new FieldInsnNode(Opcodes.GETFIELD, input.name, name, "Ljava/lang/Object;"));
                                                end.add(new InsnNode(Opcodes.MONITOREXIT));
                                            }
                                            ain = ain.getNext();
                                        }
                                        il.insertBefore(il.getFirst(), start);
                                        method.maxStack++;
                                    }
                                } else {
                                    input.access &= Opcodes.ACC_SYNTHETIC;
                                }
                            } else {
                                log.info("add {} synchronized", Arrays.toString(strings));
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
                        }
                    }

                    if (!debug_add1 && !debug_add2) {
                        throw new RuntimeException("Not mapping error: " + Arrays.toString(strings));
                    }
                }
            }


            return input;
        }catch (Throwable e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {

        File f = new File("config/K_multi_threading-independence-sync-Method-list.txt");
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
                        // 这个文件是用于对单独的函数添加synchronized但是是独立的
                        
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
