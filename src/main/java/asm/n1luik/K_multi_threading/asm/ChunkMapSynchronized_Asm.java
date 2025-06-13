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
public class ChunkMapSynchronized_Asm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        String[] strings = ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerChunkCache.getChunk(IILnet/minecraft/world/level/chunk/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;");
        String[] strings2 = ForgeAsm.minecraft_map.mapField("net/minecraft/server/level/ServerChunkCache.mainThreadProcessor");
        boolean debug_add1 = false;
        boolean is = false;

        if (input.name.equals(strings[0])){
            for (MethodNode method : input.methods) {
                if ((method.name.equals(strings[1]) && method.desc.equals(strings[2]))){
                    InsnList instructions = method.instructions;
                    InsnList instructions2 = method.instructions = new InsnList();
                    for (AbstractInsnNode instruction : instructions) {
                        if (instruction.getOpcode() == Opcodes.INVOKESTATIC && instruction instanceof MethodInsnNode methodInsnNode
                                && methodInsnNode.owner.equals("java/util/concurrent/CompletableFuture")&& methodInsnNode.name.equals("supplyAsync")){
                            instructions2.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            instructions2.add(new FieldInsnNode(Opcodes.GETFIELD, strings2[0], strings2[1], "Lnet/minecraft/server/level/ServerChunkCache$MainThreadExecutor;"));
                            instructions2.add(new InsnNode(Opcodes.MONITORENTER));
                            debug_add1 = true;
                        }else if (instruction.getOpcode() == Opcodes.INVOKESTATIC && instruction instanceof MethodInsnNode methodInsnNode
                                && methodInsnNode.owner.equals("java/util/concurrent/CompletableFuture")&& methodInsnNode.name.equals("join")){


                            /*LabelNode labelNode = new LabelNode();
                            LabelNode labelNode2 = new LabelNode();
                            LabelNode labelNode3 = new LabelNode();

                            labelNode.getLabel().info = labelNode;
                            labelNode2.getLabel().info = labelNode2;
                            labelNode3.getLabel().info = labelNode3;

                            method.visitLocalVariable("缓存_6","Ljava/lang/Throwable;","",labelNode2.getLabel(),labelNode3.getLabel(),method.maxLocals-1);

                            instructions2.add(labelNode);
                            instructions2.add(new VarInsnNode(Opcodes.ALOAD, 1));
                            instructions2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object","notify","()V"));
                            instructions2.add(new JumpInsnNode(Opcodes.GOTO, labelNode3));
                            instructions2.add(labelNode2);
                            instructions2.add(new FrameNode(Opcodes.F_CHOP, 0, null, 1, new Object[] { "java/lang/Throwable" }));
                            instructions2.add(new VarInsnNode(Opcodes.ASTORE, method.maxLocals-1));
                            instructions2.add(new TypeInsnNode(Opcodes.NEW,"java/lang/RuntimeException"));
                            instructions2.add(new InsnNode(Opcodes.DUP));
                            instructions2.add(new VarInsnNode(Opcodes.ALOAD, method.maxLocals-1));
                            instructions2.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/Throwable;)V"));
                            instructions2.add(new InsnNode(Opcodes.ATHROW));
                            instructions2.add(labelNode3);*/


                            instructions2.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            instructions2.add(new FieldInsnNode(Opcodes.GETFIELD, strings2[0], strings2[1], "Lnet/minecraft/server/level/ServerChunkCache$MainThreadExecutor;"));
                            instructions2.add(new InsnNode(Opcodes.MONITOREXIT));

                            if (!debug_add1){
                                throw new RuntimeException("Not mapping error: " + Arrays.toString(strings));
                            }

                            return input;
                        }
                        if (instruction.getOpcode() == Opcodes.RETURN || instruction.getOpcode() == Opcodes.ARETURN
                                || instruction.getOpcode() == Opcodes.DRETURN || instruction.getOpcode() == Opcodes.FRETURN
                                || instruction.getOpcode() == Opcodes.IRETURN || instruction.getOpcode() == Opcodes.LRETURN){
                            instructions2.add(new VarInsnNode(Opcodes.ALOAD,1));
                            instructions2.add(new InsnNode(Opcodes.MONITOREXIT));//复制
                        }
                        instructions2.add(instruction);
                    }
                }
            }
        }

        if (!debug_add1){
            throw new RuntimeException("Not mapping error: " + Arrays.toString(strings));
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
