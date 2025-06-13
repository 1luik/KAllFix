package asm.n1luik.K_multi_threading.asm;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.Set;


@Deprecated
public class SyncImplGetterChunk_ASM implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        String[] strings = ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerChunkCache.getChunk(IILnet/minecraft/world/level/chunk/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;");
        String[] strings2 = ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerChunkCache.getChunkNow(II)Lnet/minecraft/world/level/chunk/LevelChunk;");
        boolean debug_add1 = false;

        if (input.name.equals(strings[0])){
            for (MethodNode method : input.methods) {
                if ((method.name.equals(strings[1]) && method.desc.equals(strings[2])) ||
                        (method.name.equals(strings2[1]) && method.desc.equals(strings2[2]))){
                    debug_add1 = true;
                    InsnList instructions = method.instructions;
                    InsnList instructions2 = method.instructions = new InsnList();
                    for (AbstractInsnNode instruction : instructions) {
                        if (instruction.getOpcode() == Opcodes.ARETURN){
                            instructions2.add(new InsnNode(Opcodes.DUP));//复制
                            instructions2.add(new VarInsnNode(Opcodes.ALOAD,0));//this
                            instructions2.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "n1luik/K_multi_threading/core/asm/SyncImplGetterChunk_ASMCall","addChunkSync1","(Lnet/minecraft/world/level/chunk/ChunkAccess;Lnet/minecraft/server/level/ServerChunkCache;)V"));//运行添加锁
                        }
                        instructions2.add(instruction);
                    }
                }
            }
        }

        if (!debug_add1){
            throw new RuntimeException("Not mapping error: " + Arrays.toString(strings)  + " | " + Arrays.toString(strings2));
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
                Target.targetClass("net/minecraft/server/level/ServerChunkCache"));
    }
}
