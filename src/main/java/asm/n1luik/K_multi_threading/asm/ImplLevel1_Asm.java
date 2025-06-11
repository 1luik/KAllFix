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
public class ImplLevel1_Asm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        boolean debug_add1 = false;
        boolean debug_add2 = false;
        String[] strings = ForgeAsm.minecraft_map.mapField("net/minecraft/world/level/Level.neighborUpdater");

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
                                    "n1luik/K_multi_threading/core/base/ConcurrentCollectingNeighborUpdater",
                                    "toConcurrent",
                                    "(Lnet/minecraft/world/level/redstone/CollectingNeighborUpdater;)Ln1luik/K_multi_threading/core/base/ConcurrentCollectingNeighborUpdater;"));
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
        if (!(debug_add1 && debug_add2)){
            throw new RuntimeException("Not mapping error: net.minecraft.world.level.Level: %s %s ".formatted(debug_add1, debug_add2));
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
                Target.targetClass("net.minecraft.world.level.Level"));
    }
}
