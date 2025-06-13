package asm.n1luik.K_multi_threading.asm.mod.mek;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.Set;

public class MekanismStructureSynchronized_Asm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        boolean debug_add1 = false;

        if (input.name.equals("mekanism/common/lib/multiblock/Structure")){
            for (MethodNode method : input.methods) {
                if ((method.name.equals("add") && method.desc.equals("(Lmekanism/common/lib/multiblock/Structure;)V"))){
                    debug_add1 = true;
//                    InsnList instructions = method.instructions;
//                    InsnList instructions2 = method.instructions = new InsnList();
//
//                    instructions2.add(new VarInsnNode(Opcodes.ALOAD,1));
//                    instructions2.add(new InsnNode(Opcodes.MONITORENTER));//复制
//
//
//                    /*LabelNode labelNode = new LabelNode();
//                    LabelNode labelNode2 = new LabelNode();
//                    LabelNode labelNode3 = new LabelNode();
//
//                    labelNode.getLabel().info = labelNode;
//                    labelNode2.getLabel().info = labelNode2;
//                    labelNode3.getLabel().info = labelNode3;
//
//                    method.visitLocalVariable("缓存_6","Ljava/lang/Throwable;","",labelNode2.getLabel(),labelNode3.getLabel(),method.maxLocals-1);
//
//                    instructions2.add(labelNode);
//                    instructions2.add(new VarInsnNode(Opcodes.ALOAD, 0));
//                    instructions2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object","notify","()V"));
//                    instructions2.add(new JumpInsnNode(Opcodes.GOTO, labelNode3));
//                    instructions2.add(labelNode2);
//                    instructions2.add(new FrameNode(Opcodes.F_CHOP, 0, null, 1, new Object[] { "java/lang/Throwable" }));
//                    instructions2.add(new VarInsnNode(Opcodes.ASTORE, method.maxLocals-1));
//                    instructions2.add(new TypeInsnNode(Opcodes.NEW,"java/lang/RuntimeException"));
//                    instructions2.add(new InsnNode(Opcodes.DUP));
//                    instructions2.add(new VarInsnNode(Opcodes.ALOAD, method.maxLocals-1));
//                    instructions2.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/Throwable;)V"));
//                    instructions2.add(new InsnNode(Opcodes.ATHROW));
//                    instructions2.add(labelNode3);
//
//                    method.visitTryCatchBlock(labelNode.getLabel(),labelNode2.getLabel(),labelNode2.getLabel(),"java/lang/Throwable");
//*/
//
//                    for (AbstractInsnNode instruction : instructions) {
//                        if (instruction.getOpcode() == Opcodes.RETURN || instruction.getOpcode() == Opcodes.ARETURN
//                                || instruction.getOpcode() == Opcodes.DRETURN || instruction.getOpcode() == Opcodes.FRETURN
//                                || instruction.getOpcode() == Opcodes.IRETURN || instruction.getOpcode() == Opcodes.LRETURN){
//                            instructions2.add(new VarInsnNode(Opcodes.ALOAD,1));
//                            instructions2.add(new InsnNode(Opcodes.MONITOREXIT));//复制
//                        }
//                        instructions2.add(instruction);
//                    }

                    InsnList instructions = method.instructions;
                    InsnList instructions2 = method.instructions = new InsnList();

                    for (AbstractInsnNode instruction : instructions) {
                        if (instruction.getOpcode() == Opcodes.INVOKEINTERFACE && instruction instanceof MethodInsnNode methodInsnNode){
                            if (methodInsnNode.owner.equals("java/util/Map") && methodInsnNode.name.equals("forEach") && methodInsnNode.desc.equals("(Ljava/util/function/BiConsumer;)V")) {
                                methodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                                methodInsnNode.owner = "asm/n1luik/K_multi_threading/asm/mod/Empty";
                                methodInsnNode.desc = "(Ljava/util/Map;Ljava/util/function/BiConsumer;)V";
                                methodInsnNode.itf = false;
                            }else if (methodInsnNode.owner.equals("java/util/NavigableMap") && methodInsnNode.name.equals("forEach") && methodInsnNode.desc.equals("(Ljava/util/function/BiConsumer;)V")) {
                                methodInsnNode.setOpcode(Opcodes.INVOKESTATIC);
                                methodInsnNode.owner = "asm/n1luik/K_multi_threading/asm/mod/Empty";
                                methodInsnNode.desc = "(Ljava/util/NavigableMap;Ljava/util/function/BiConsumer;)V";
                                methodInsnNode.itf = false;
                            }else {
                                instructions2.add(instruction);
                            }
                        }else instructions2.add(instruction);
                    }
                }
            }
        }

        if (!debug_add1){
            throw new RuntimeException("Not mapping error: mekanism/common/lib/multiblock/Structure");
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
                Target.targetClass("mekanism/common/lib/multiblock/Structure"));
    }
}
