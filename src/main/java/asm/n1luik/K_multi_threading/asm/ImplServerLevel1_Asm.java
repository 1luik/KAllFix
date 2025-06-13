package asm.n1luik.K_multi_threading.asm;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

@Slf4j
public class ImplServerLevel1_Asm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        boolean debug_add1 = false;
        boolean debug_add2 = false;
        boolean debug_add3 = false;
        boolean debug_add4 = false;
        boolean debug_add5 = false;
        String[] strings = ForgeAsm.minecraft_map.mapField("net/minecraft/server/level/ServerLevel.chunkSource");
        String[] strings2 = ForgeAsm.minecraft_map.mapField("net/minecraft/server/level/ServerLevel.players");
        String[] strings3 = ForgeAsm.minecraft_map.mapField("net/minecraft/server/level/ServerLevel.navigatingMobs");
        String[] strings4 = ForgeAsm.minecraft_map.mapField("net/minecraft/server/level/ServerLevel.dragonParts");

        for (MethodNode method : input.methods) {
            if (method.name.equals("<init>")) {
                //debug_add2 = true;
                InsnList instructions = method.instructions;
                method.instructions = new InsnList();
                debug_add1 = true;

                for (AbstractInsnNode instruction : instructions) {
                    if (instruction.getOpcode() == Opcodes.PUTFIELD && instruction instanceof FieldInsnNode fieldInsnNode) {
                        if (fieldInsnNode.name.equals(strings[1])) {
                            debug_add2 = true;
                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "n1luik/K_multi_threading/core/base/ParaServerChunkProvider",
                                    "toPara",
                                    "(Lnet/minecraft/server/level/ServerChunkCache;)Ln1luik/K_multi_threading/core/base/ParaServerChunkProvider;"));
                            method.instructions.add(instruction);
                        }else if (fieldInsnNode.name.equals(strings2[1])) {
                            debug_add3 = true;
                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
                                    "concurrentList",
                                    "(Ljava/util/Collection;)Ljava/util/concurrent/CopyOnWriteArrayList;"));
                            method.instructions.add(instruction);
                        }else if (fieldInsnNode.name.equals(strings3[1])) {
                            debug_add4 = true;
                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
                                    "concurrentSet",
                                    "(Ljava/util/Set;)Ljava/util/concurrent/ConcurrentHashMap$KeySetView;"));
                            method.instructions.add(instruction);
                        }else if (fieldInsnNode.name.equals(strings4[1])) {
                            debug_add5 = true;
                            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
                                    "concurrentInt2ObjectMap",
                                    "(Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;)Ln1luik/K_multi_threading/core/util/concurrent/ConcurrentInt2ObjectOpenHashMap;"));
                            method.instructions.add(instruction);
                        }else {
                            method.instructions.add(instruction);
                        }
                    } else {
                        method.instructions.add(instruction);
                    }
                }
            }
        }
        //method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
        //                                    "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
        //                                    "concurrentMap",
        //                                    "(Ljava/util/Map;)Ljava/util/concurrent/ConcurrentHashMap;"))
        if (!(debug_add1 && debug_add2 && debug_add3 && debug_add4 && debug_add5)){
            throw new RuntimeException("Not mapping error: net.minecraft.server.level.ServerLevel: %s %s %s %s %s".formatted(debug_add1, debug_add2, debug_add3, debug_add4, debug_add5));
        }


        return input;
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {
        return Set.of(
                Target.targetClass("net.minecraft.server.level.ServerLevel"));
    }
}
