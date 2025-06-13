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
public class AddServerLevelSync1_Asm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        String[] strings = ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.sendBlockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;I)V");
        String[] strings2 = ForgeAsm.minecraft_map.mapField("net/minecraft/server/level/ServerLevel.serverLevelData");

        boolean debug_add1 = false;

        if (input.name.equals(strings[0])){

            for (MethodNode method : input.methods) {
                if (method.name.equals("<init>")) {
                    //debug_add2 = true;
                    InsnList instructions = method.instructions;
                    method.instructions = new InsnList();
                    debug_add1 = true;

                    for (AbstractInsnNode instruction : instructions) {
                        if (instruction.getOpcode() == Opcodes.NEW && instruction instanceof TypeInsnNode typeInsnNode) {
                            if (typeInsnNode.desc.equals("it/unimi/dsi/fastutil/objects/ObjectOpenHashSet")) {
                                method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                        "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
                                        "concurrentObjectSet",
                                        "()Lit/unimi/dsi/fastutil/objects/ObjectSortedSet;"));
                            }else {
                                method.instructions.add(instruction);
                            }
                        } else if (instruction.getOpcode() == Opcodes.INVOKESPECIAL && instruction instanceof MethodInsnNode methodInsnNode) {
                            if (methodInsnNode.owner.equals("it/unimi/dsi/fastutil/objects/ObjectOpenHashSet")
                            && methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
                                    method.instructions.add(new InsnNode(Opcodes.POP));
                            } else {
                                method.instructions.add(instruction);
                            }
                        } else {
                            method.instructions.add(instruction);
                        }
                    }
                }
            }
        }

        if (!debug_add1){
            throw new RuntimeException("Not mapping error: net.minecraft.server.level.ServerLevel: "+ debug_add1);
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
