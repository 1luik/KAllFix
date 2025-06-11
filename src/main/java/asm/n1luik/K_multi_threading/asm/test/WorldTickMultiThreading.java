package asm.n1luik.K_multi_threading.asm.test;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import asm.n1luik.K_multi_threading.asm.OB2_ASM;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.Dynamic;

import java.util.*;

@Deprecated
@Slf4j
public class WorldTickMultiThreading  implements ITransformer<ClassNode> {

    protected boolean isStartCopy1 = false;
    protected boolean isStartCopy2 = true;
    protected int isStartCopy3 = 0;
    protected boolean isStartCopy4 = false;
    protected int ib1 = 0;
    protected Object stopPos = null;


    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        String[] mn1 = ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/MinecraftServer.tickChildren(Ljava/util/function/BooleanSupplier;)V");
        String[] mn2 = ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/MinecraftServer.getWorldArray()[Lnet/minecraft/server/level/ServerLevel;");
        //boolean debug_add1 = false;

        LabelNode labelNode1 = new LabelNode();
        labelNode1.getLabel().info = labelNode1;
        LabelNode labelNode2 = new LabelNode();
        labelNode2.getLabel().info = labelNode2;
        LabelNode labelNode3 = new LabelNode();
        labelNode3.getLabel().info = labelNode3;
        LabelNode labelNode4 = new LabelNode();
        labelNode4.getLabel().info = labelNode4;

        String newDescriptor = "(" +
                "[Lnet/minecraft/server/level/ServerLevel;" +
                "Ljava/util/function/BooleanSupplier;" +
                "I" +
                "I" +
                ")V";
        String newName = mn1[1]+"$K_multi_threading$tickChildren$WorldTickMultiThreading";
        MethodNode tickChildren$MT = (MethodNode)input.visitMethod(Opcodes.ACC_PUBLIC, newName, newDescriptor, null, null);
        InsnList tickMT_ins = new InsnList();//tickChildren$MT.instructions;
        MethodNode tickChildren$lambda = (MethodNode)input.visitMethod(Opcodes.ACC_PRIVATE, newName+"_call$lambda$", "(" +
                "[Lnet/minecraft/server/level/ServerLevel;" +
                "Ljava/util/function/BooleanSupplier;" +
                "I" +
                "Ljava/lang/Integer;" +
                ")Ljava/lang/Object;", null, null);

        {
            InsnList tickLambda_ins = tickChildren$lambda.instructions;
            tickChildren$lambda.visitLocalVariable("this", "Lnet/minecraft/server/MinecraftServer;", null, labelNode2.getLabel(), labelNode3.getLabel(), 0);
            tickChildren$lambda.visitLocalVariable("_level1", "Lnet/minecraft/server/level/ServerLevel;", null, labelNode2.getLabel(), labelNode3.getLabel(), 1);
            tickChildren$lambda.visitLocalVariable("_bs1", "Ljava/util/function/BooleanSupplier;", null, labelNode2.getLabel(), labelNode3.getLabel(), 2);
            tickChildren$lambda.visitLocalVariable("i", "Ljava/lang/Integer;", null, labelNode2.getLabel(), labelNode3.getLabel(), 3);
            tickChildren$lambda.visitLocalVariable("i2", "I", null, labelNode2.getLabel(), labelNode3.getLabel(), 4);
            tickLambda_ins.add(labelNode2);
            tickLambda_ins.add(new VarInsnNode(Opcodes.ALOAD, 0));
            tickLambda_ins.add(new VarInsnNode(Opcodes.ALOAD, 1));
            tickLambda_ins.add(new VarInsnNode(Opcodes.ALOAD, 2));
            tickLambda_ins.add(new VarInsnNode(Opcodes.ILOAD, 4));
            tickLambda_ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false));
            tickLambda_ins.add(new InsnNode(Opcodes.DUP));
            tickLambda_ins.add(new VarInsnNode(Opcodes.ILOAD, 3));
            tickLambda_ins.add(new JumpInsnNode(Opcodes.IF_ICMPGE, labelNode4));
            tickLambda_ins.add(new VarInsnNode(Opcodes.ALOAD, 3));
            tickLambda_ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/server/MinecraftServer", tickChildren$lambda.name, tickChildren$lambda.desc, false));
            tickLambda_ins.add(labelNode4);
            tickLambda_ins.add(new InsnNode(Opcodes.ACONST_NULL));
            tickLambda_ins.add(new InsnNode(Opcodes.ARETURN));
            tickLambda_ins.add(labelNode3);
            tickChildren$lambda.visitMaxs(5, 5);
        }


        for (MethodNode method : input.methods) {
            if (method.name.equals(mn1[1])) {
                InsnList instructions = method.instructions;
                InsnList instructions2 = method.instructions = new InsnList();


                for (AbstractInsnNode instruction : instructions) {
                    if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL && instruction instanceof MethodInsnNode methodInsnNode) {
                        if (methodInsnNode.owner.equals("net/minecraft/server/MinecraftServer")) {
                            if (methodInsnNode.name.equals(mn2[1])) {
                                isStartCopy1 = true;
                                instructions2.add(instruction);
                                if (isStartCopy2) {
                                    tickMT_ins.add(instruction);
                                }
                            } else {
                                instructions2.add(instruction);
                                if (isStartCopy2) {
                                    tickMT_ins.add(instruction);
                                }
                            }
                        } else {
                            instructions2.add(instruction);
                            if (isStartCopy2) {
                                tickMT_ins.add(instruction);
                            }

                        }
                    } else if (instruction.getOpcode() == Opcodes.IF_ICMPGE) {
                        if (isStartCopy1 && !isStartCopy2) {
                            isStartCopy2 = true;
                            stopPos = ((JumpInsnNode) instruction).label;
                        }
                        instructions2.add(instruction);
                        if (isStartCopy2) {
                            tickMT_ins.add(instruction);
                        }
                    } else if (instruction.getOpcode() == Opcodes.ARRAYLENGTH) {
                        if (isStartCopy1 && !isStartCopy2 && isStartCopy3 == 0) {
                            isStartCopy3 = 1;
                        }
                        instructions2.add(instruction);
                        if (isStartCopy2) {
                            tickMT_ins.add(instruction);
                        }
                    } else if (instruction.getOpcode() == -1) {
                        if (instruction instanceof LabelNode labelNode) {
                            //if (isStartCopy3 < 4 || isStartCopy4) {
                                tickMT_ins.add(labelNode);
                            //} else if (isStartCopy2) {
                            //    tickMT_ins.add(instruction);
                            //}
                            if (stopPos == instruction) {
                                isStartCopy1 = false;
                                isStartCopy2 = false;
                                isStartCopy4 = true;
                                instructions2.add(labelNode1);
                            }
                            instructions2.add(instruction);
                        }else {
                            instructions2.add(instruction);
                            if (isStartCopy2) {
                                tickMT_ins.add(instruction);
                            }
                        }
                    } else if (instruction.getOpcode() == Opcodes.ISTORE) {
                        if (isStartCopy1 && !isStartCopy2) {
                            if (isStartCopy3 == 1) {
                                isStartCopy3 = 2;
                            } else if (isStartCopy3 == 3) {
                                isStartCopy3 = 4;
                                instructions2.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "n1luik/K_multi_threading/core/Base",
                                        "getEx",
                                        "()Ln1luik/K_multi_threading/core/Base$ForkJoinPool_;", false));
                                instructions2.add(new TypeInsnNode(Opcodes.NEW, "n1luik/K_multi_threading/core/util/CalculateTask"));
                                instructions2.add(new InsnNode(Opcodes.DUP));
                                instructions2.add(new IntInsnNode(Opcodes.ILOAD, 4));
                                instructions2.add(new IntInsnNode(Opcodes.ILOAD, 3));

                                instructions2.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                instructions2.add(new VarInsnNode(Opcodes.ALOAD, 2));
                                instructions2.add(new VarInsnNode(Opcodes.ALOAD, 1));
                                instructions2.add(new VarInsnNode(Opcodes.ILOAD, 3));
                                instructions2.add(new InvokeDynamicInsnNode("apply",
                                        "(" +
                                                "Lnet/minecraft/server/MinecraftServer" +
                                                "[Lnet/minecraft/server/level/ServerLevel;" +
                                                "Ljava/util/function/BooleanSupplier;" +
                                                "I" +
                                                ")Ljava/util/function/Function;",
                                        new Handle(Opcodes.H_INVOKESTATIC,
                                                "java/lang/invoke/LambdaMetafactory",
                                                "metafactory",
                                                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                                                false),
                                        Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"),
                                        new Handle(Opcodes.H_INVOKESTATIC,
                                                "net/minecraft/server/MinecraftServer",
                                                tickChildren$lambda.name,
                                                tickChildren$lambda.desc,
                                                false),
                                        Type.getType("(Ljava/lang/Integer;)Ljava/lang/Object;")
                                ));

                                instructions2.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "n1luik/K_multi_threading/core/util/CalculateTask", "<init>", "(IILjava/util/function/Function;)V", false));
                                instructions2.add(new InsnNode(Opcodes.DUP));
                                instructions2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "Ljava.util/concurrent/ForkJoinPool", "submit", "(Ljava/util/concurrent/ForkJoinTask;)Ljava/util/concurrent/ForkJoinTask;", false));
                                instructions2.add(new InsnNode(Opcodes.POP));
                                instructions2.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "n1luik/K_multi_threading/core/util/CalculateTask", "waitThread", "()V", false));


                                instructions2.add(new JumpInsnNode(Opcodes.GOTO, labelNode1));
                            }
                        }
                        instructions2.add(instruction);
                        if (isStartCopy2) {
                            tickMT_ins.add(instruction);
                        }
                    } else if (instruction.getOpcode() == Opcodes.ICONST_0) {
                        if (isStartCopy1 && !isStartCopy2) {
                            if (isStartCopy3 == 2) {
                                isStartCopy3 = 3;
                            }
                        }
                        instructions2.add(instruction);
                        if (isStartCopy2) {
                            tickMT_ins.add(instruction);
                        }
                    } else {
                        instructions2.add(instruction);
                        if (isStartCopy2) {
                            tickMT_ins.add(instruction);
                        }
                    }
                }


                method.maxStack += 9;


                if (tickChildren$MT.localVariables == null) {
                    tickChildren$MT.localVariables = new ArrayList<>();
                }
                for (LocalVariableNode localVariable : method.localVariables) {
                    localVariable.accept(tickChildren$MT);
                }

                if (tickChildren$MT.tryCatchBlocks == null) {
                    tickChildren$MT.tryCatchBlocks = new ArrayList<>();
                }
                for (TryCatchBlockNode tryCatchBlock : method.tryCatchBlocks) {
                    tryCatchBlock.accept(tickChildren$MT);
                }

                //log.info("debug1");

                List<OB2_ASM<LabelNode, LabelNode>> tickChildren$MT_insnList = new ArrayList<>();

                for (AbstractInsnNode tickMT_in : tickMT_ins) {
                    //if (tickMT_in instanceof LabelNode labelNode){
                    //    LabelNode labelNode5 = new LabelNode();
                    //    labelNode5.getLabel().info = labelNode5;
                    //    //tickChildren$MT.visitLabel(labelNode5.getLabel());
                    //    tickChildren$MT.instructions.add(labelNode5);
                    //    tickChildren$MT_insnList.add(new OB2_ASM<>(labelNode, labelNode5));
                    //}else {
                        tickMT_in.accept(tickChildren$MT);
                    //}
                }
                tickMT_ins.clear();
                //log.info("debug2");

                for (AbstractInsnNode instruction : tickChildren$MT.instructions) {
                    if (instruction instanceof LabelNode labelNode){
                        LabelNode labelNode5 = new LabelNode();
                        labelNode5.getLabel().info = labelNode5;
                        //tickChildren$MT.visitLabel(labelNode5.getLabel());
                        tickMT_ins.add(labelNode5);
                        tickChildren$MT_insnList.add(new OB2_ASM<>(labelNode, labelNode5));
                    }else {
                        tickMT_ins.add(instruction);
                    }
                }
                //log.info("debug3");
                tickChildren$MT.instructions.clear();
                tickChildren$MT.instructions.add(tickMT_ins);

                tickChildren$MT.visitMaxs(method.maxStack, method.maxLocals);
                for (LocalVariableNode localVariable : tickChildren$MT.localVariables) {
                    boolean test = false;
                    for (OB2_ASM<LabelNode, LabelNode> labelNodeLabelNodeOB2_asm : tickChildren$MT_insnList) {
                        if (localVariable.start.getLabel() == labelNodeLabelNodeOB2_asm.getT1().getLabel()) {
                            localVariable.start = labelNodeLabelNodeOB2_asm.getT2();
                            test = true;
                            break;
                        }
                    }
                    if (!test) {
                        throw new RuntimeException();
                    }
                    boolean test2 = false;
                    for (OB2_ASM<LabelNode, LabelNode> labelNodeLabelNodeOB2_asm : tickChildren$MT_insnList) {
                        if (localVariable.end.getLabel() == labelNodeLabelNodeOB2_asm.getT1().getLabel()) {
                            localVariable.end = labelNodeLabelNodeOB2_asm.getT2();
                            test2 = true;
                            break;
                        }
                    }
                    if (!test2) {
                        throw new RuntimeException();
                    }

                }
                //log.info("debug4");
                for (TryCatchBlockNode tryCatchBlock : tickChildren$MT.tryCatchBlocks) {
                    boolean test = false;
                    for (OB2_ASM<LabelNode, LabelNode> labelNodeLabelNodeOB2_asm : tickChildren$MT_insnList) {
                        if (tryCatchBlock.start.getLabel() == labelNodeLabelNodeOB2_asm.getT1().getLabel()) {
                            tryCatchBlock.start = labelNodeLabelNodeOB2_asm.getT2();
                            test = true;
                            break;
                        }
                    }
                    if (!test) {
                        throw new RuntimeException();
                    }
                    boolean test2 = false;
                    for (OB2_ASM<LabelNode, LabelNode> labelNodeLabelNodeOB2_asm : tickChildren$MT_insnList) {
                        if (tryCatchBlock.end.getLabel() == labelNodeLabelNodeOB2_asm.getT1().getLabel()) {
                            tryCatchBlock.end = labelNodeLabelNodeOB2_asm.getT2();
                            test2 = true;
                            break;
                        }
                    }
                    if (!test2) {
                        throw new RuntimeException();
                    }
                    boolean test3 = false;
                    for (OB2_ASM<LabelNode, LabelNode> labelNodeLabelNodeOB2_asm : tickChildren$MT_insnList) {
                        if (tryCatchBlock.handler.getLabel() == labelNodeLabelNodeOB2_asm.getT1().getLabel()) {
                            tryCatchBlock.handler = labelNodeLabelNodeOB2_asm.getT2();
                            test3 = true;
                            break;
                        }
                    }
                    if (!test3) {
                        throw new RuntimeException();
                    }
                }
                //log.info("debug5");
                for (AbstractInsnNode instruction : tickChildren$MT.instructions) {
                    if (instruction instanceof JumpInsnNode jumpInsnNode){
                        boolean test = false;
                        for (OB2_ASM<LabelNode, LabelNode> labelNodeLabelNodeOB2_asm : tickChildren$MT_insnList) {
                            if (jumpInsnNode.label.getLabel() == labelNodeLabelNodeOB2_asm.getT1().getLabel()) {
                                jumpInsnNode.label = labelNodeLabelNodeOB2_asm.getT2();
                                test = true;
                                break;
                            }
                        }
                        if (!test) {
                            throw new RuntimeException();
                        }
                    }else if (instruction instanceof FrameNode labelNode){
                        {
                            ArrayList<Object> objects = new ArrayList<>();
                            if (labelNode.local != null){
                                for (Object o : labelNode.local) {
                                    if (o instanceof Label label) {
                                        boolean test = false;
                                        for (OB2_ASM<LabelNode, LabelNode> labelNodeLabelNodeOB2_asm : tickChildren$MT_insnList) {
                                            if (label == labelNodeLabelNodeOB2_asm.getT1().getLabel()) {
                                                objects.add(labelNodeLabelNodeOB2_asm.getT2().getLabel());
                                                test = true;
                                                break;
                                            }
                                        }
                                        if (!test) {
                                            throw new RuntimeException();
                                        }
                                    } else {
                                        objects.add(o);
                                    }
                                }
                                labelNode.local = objects;
                            }
                        }
                        {
                            ArrayList<Object> objects = new ArrayList<>();
                            if (labelNode.stack !=  null) {
                                for (Object o : labelNode.stack) {
                                    if (o instanceof Label label) {
                                        boolean test = false;
                                        for (OB2_ASM<LabelNode, LabelNode> labelNodeLabelNodeOB2_asm : tickChildren$MT_insnList) {
                                            if (label == labelNodeLabelNodeOB2_asm.getT1().getLabel()) {
                                                objects.add(labelNodeLabelNodeOB2_asm.getT2().getLabel());
                                                test = true;
                                                break;
                                            }
                                        }
                                        if (!test) {
                                            throw new RuntimeException();
                                        }
                                    } else {
                                        objects.add(o);
                                    }
                                }
                                labelNode.stack = objects;
                            }
                        }
                    }
                }

                //log.info("debug6");

                for (AbstractInsnNode instruction : tickChildren$MT.instructions) {
                    if (instruction instanceof LabelNode labelNode) {
                        labelNode.getLabel().info = ib1;
                    } else {
                        ib1++;
                    }
                }

                List<TryCatchBlockNode> tryRemove = new ArrayList<>();
                List<LocalVariableNode> localRemove = new ArrayList<>();

                for (TryCatchBlockNode tryCatchBlock : tickChildren$MT.tryCatchBlocks) {
                    /*if (tryCatchBlock.start.getLabel().info == null && tryCatchBlock.end.getLabel().info == null){
                        tryRemove.add(tryCatchBlock);
                    }else */
                    if (((int) tryCatchBlock.start.getLabel().info) ==
                            ((int) tryCatchBlock.end.getLabel().info)) {
                        tryRemove.add(tryCatchBlock);
                        log.info("remove try: {} [{}, {}]", tryCatchBlock.type, tryCatchBlock.start.getLabel().info, tryCatchBlock.end.getLabel().info);
                    } else if (!((int) tryCatchBlock.start.getLabel().info < ib1) || !((int) tryCatchBlock.handler.getLabel().info < ib1)) {
                        tryRemove.add(tryCatchBlock);
                        log.info("remove try: {} [{}, {}]", tryCatchBlock.type, tryCatchBlock.start.getLabel().info, tryCatchBlock.end.getLabel().info);

                    }
                }

                int newMaxStack = 0;
                for (LocalVariableNode localVariable : tickChildren$MT.localVariables) {
                    newMaxStack = newMaxStack < localVariable.index ? localVariable.index : newMaxStack;
                    /*if (localVariable.start.getLabel().info == null && localVariable.end.getLabel().info == null){
                        localRemove.add(localVariable);
                    } else */
                    if (((int) localVariable.start.getLabel().info) ==
                            ((int) localVariable.end.getLabel().info)) {
                        log.info("remove Local: {}", localVariable.index);
                        localRemove.add(localVariable);
                    } else if (!((int) localVariable.start.getLabel().info < ib1)) {
                        log.info("remove Local: {}", localVariable.index);
                        localRemove.add(localVariable);
                    }
                }

                InsnList instructions1 = new InsnList();

                for (AbstractInsnNode instruction : tickChildren$MT.instructions) {
                    if (instruction instanceof JumpInsnNode jumpInsnNode) {
                        if (!((int) jumpInsnNode.label.getLabel().info < ib1)) {
                            instructions1.add(instruction);
                        }
                    } else {
                        instructions1.add(instruction);
                    }
                }
                tickChildren$MT.instructions = instructions1;

                tryRemove.forEach(tickChildren$MT.tryCatchBlocks::remove);
                localRemove.forEach(tickChildren$MT.localVariables::remove);

                for (AbstractInsnNode instruction : tickChildren$MT.instructions) {
                    if (instruction instanceof LabelNode labelNode) {
                        labelNode.getLabel().info = labelNode;
                    }
                }
                tickChildren$MT.maxStack = newMaxStack;
                log.info("maxStack: {}", newMaxStack);
                log.info("maxLocals: {}", tickChildren$MT.maxLocals);


                //检测是否有使用没有添加的label
                List<Label> labels = new ArrayList<>();
                for (AbstractInsnNode instruction : tickChildren$MT.instructions) {
                    if (instruction instanceof LabelNode labelNode) {
                        labels.add(labelNode.getLabel());
                    }
                }

                for (AbstractInsnNode instruction : tickChildren$MT.instructions) {
                    if (instruction instanceof JumpInsnNode jumpInsnNode) {
                        if (!labels.contains(jumpInsnNode.label.getLabel())) {
                            throw new RuntimeException();
                        }
                    } else if (instruction instanceof FrameNode frameNode) {
                        if (frameNode.stack != null){
                            for (Object o : frameNode.stack) {
                                if (o instanceof Label label) {
                                    if (!labels.contains(label)) {
                                        throw new RuntimeException();
                                    }
                                }
                            }
                        }
                        if (frameNode.local != null){
                            for (Object o : frameNode.local) {
                                if (o instanceof Label label) {
                                    if (!labels.contains(label)) {
                                        throw new RuntimeException();
                                    }
                                }
                            }
                        }

                    }
                }
                for (LocalVariableNode localVariable : tickChildren$MT.localVariables) {
                    if (!labels.contains(localVariable.start.getLabel())) {
                        throw new RuntimeException();
                    }
                    if (!labels.contains(localVariable.end.getLabel())) {
                        throw new RuntimeException();
                    }
                }
                for (TryCatchBlockNode tryCatchBlock : tickChildren$MT.tryCatchBlocks) {
                    if (!labels.contains(tryCatchBlock.start.getLabel())) {
                        throw new RuntimeException();
                    }
                    if (!labels.contains(tryCatchBlock.end.getLabel())) {
                        throw new RuntimeException();
                    }
                    if (!labels.contains(tryCatchBlock.handler.getLabel())) {
                        throw new RuntimeException();
                    }

                }
                tickChildren$MT.visitInsn(Opcodes.RETURN);


                for (AbstractInsnNode instruction : tickChildren$MT.instructions) {
                    if (instruction instanceof  FrameNode frameNode){
                        log.info("frameNode {} {} {}", frameNode.type, frameNode.stack == null ? "null" : Arrays.toString(frameNode.stack.toArray()), frameNode.local == null ? "null" : Arrays.toString(frameNode.local.toArray()));
                    }

                }
            }
        }

        //if (!debug_add1){
        //    throw  new RuntimeException("Not method error");//throw new RuntimeException("Not method");
        //}


        return input;
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }


    @Override
    public @NotNull Set<Target> targets() {
        return Set.of(
                //Target.targetClass("net/minecraft/server/level/ServerChunkCache"));
                Target.targetClass("net/minecraft/server/MinecraftServer"));
    }
}

