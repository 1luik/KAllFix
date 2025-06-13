package asm.n1luik.K_multi_threading.asm;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

@Deprecated
@Slf4j
public class _AddServerLevelSync1_Asm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        String[] strings = ForgeAsm.minecraft_map.mapMethod("net/minecraft/server/level/ServerLevel.sendBlockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;I)V");
        String[] strings2 = ForgeAsm.minecraft_map.mapField("net/minecraft/server/level/ServerLevel.serverLevelData");

        boolean debug_add1 = false;
        boolean debug_add2 = false;

        if (input.name.equals(strings[0])){

            String fieldName1 = "K_multi_threading$ServerLevelSync1";
            input.visitField(Opcodes.ACC_PROTECTED | Opcodes.ACC_FINAL, fieldName1, "Ljava/lang/Object;", null, false);
            for (MethodNode method : input.methods) {
                if ((method.name.equals(strings[1]) && method.desc.equals(strings[2]))){
                    AbstractInsnNode[] instructions = method.instructions.toArray();
                    method.instructions = new InsnList();
                    LabelNode labelNode1 = new LabelNode();
                    labelNode1.getLabel().info = labelNode1;
                    LabelNode labelNode2 = new LabelNode();
                    labelNode2.getLabel().info = labelNode2;
                    //LabelNode labelNode3 = new LabelNode();
                    //labelNode3.getLabel().info = labelNode3;
                    //method.localVariables.add(new LocalVariableNode("$", "Ljava/lang/Throwable;", "", labelNode2, labelNode3, method.maxLocals));
                    method.tryCatchBlocks.add(new TryCatchBlockNode(labelNode1, labelNode2, labelNode2, null));
                    //boolean m2 = false;
                    debug_add1 = true;

                    method.instructions.add(labelNode1);
                    method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    method.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, strings2[0], fieldName1, "Ljava/lang/Object;"));
                    method.instructions.add(new InsnNode(Opcodes.MONITORENTER));

                    for (AbstractInsnNode instruction : instructions) {
                        //if (m2){
                        if (instruction.getOpcode() == Opcodes.RETURN || instruction.getOpcode() == Opcodes.ARETURN
                                || instruction.getOpcode() == Opcodes.DRETURN || instruction.getOpcode() == Opcodes.FRETURN
                                || instruction.getOpcode() == Opcodes.IRETURN || instruction.getOpcode() == Opcodes.LRETURN) {
                            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            method.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, strings2[0], fieldName1, "Ljava/lang/Object;"));
                            method.instructions.add(new InsnNode(Opcodes.MONITOREXIT));
                        }//else if (instruction instanceof LabelNode labelNode) {
                        //    LabelNode labelNode4 = labelNode;
                        //    while (true){
                        //        if (labelNode4.getNext() == null){
                        //            debug_add1 = true;
                        //            method.instructions.add(labelNode2);
                        //            method.instructions.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[] { "java/lang/Throwable" }));
                        //            method.instructions.add(new VarInsnNode(Opcodes.ASTORE, method.maxLocals));
                        //            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        //            method.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, strings2[0], fieldName1, "Ljava/lang/Object;"));
                        //            method.instructions.add(new InsnNode(Opcodes.MONITOREXIT));
                        //            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, method.maxLocals));
                        //            method.instructions.add(new InsnNode(Opcodes.ATHROW));
                        //            method.instructions.add(labelNode3);
                        //            break;
                        //        } else if (labelNode4.getNext() instanceof LabelNode labelNode5)  {
                        //            labelNode4 = labelNode5;
                        //        }else break;
                        //    }
                        //}
                        method.instructions.add(instruction);



                        //}else {
                        //    method.instructions.add(instruction);
                        //
                        //    if (instruction instanceof LabelNode labelNode) {
                        //        if (!(labelNode.getNext() instanceof LabelNode)) {
                        //            method.instructions.add(labelNode1);
                        //            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        //            method.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, strings2[0], fieldName1, "Ljava/lang/Object;"));
                        //            method.instructions.add(new InsnNode(Opcodes.MONITORENTER));
                        //            m2 = true;
                        //        }
                        //    }
                        //}
                    }

                    method.instructions.add(labelNode2);
                    method.instructions.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[] { "java/lang/Throwable" }));
                    //method.instructions.add(new VarInsnNode(Opcodes.ASTORE, method.maxLocals));
                    method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    method.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, strings2[0], fieldName1, "Ljava/lang/Object;"));
                    method.instructions.add(new InsnNode(Opcodes.MONITOREXIT));
                    //method.instructions.add(new VarInsnNode(Opcodes.ALOAD, method.maxLocals));
                    method.instructions.add(new InsnNode(Opcodes.ATHROW));
                    //method.instructions.add(labelNode3);

                    log.info("[K_multi_threading] Add [{}] synchronize 1", strings[1]);
                    //AbstractInsnNode[] instructions1 = method.instructions.toArray();
                    //for (int i = instructions1.length - 1; i >= 0; i--) {
                    //    AbstractInsnNode node = instructions1[i];
                    //    if (node instanceof LabelNode labelNode) {
                    //        if (!(labelNode.getPrevious() instanceof LabelNode)) {
                    //            InsnList abstractInsnNodes = new InsnList();
                    //            abstractInsnNodes.add(labelNode2);
                    //            abstractInsnNodes.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[] { "java/lang/Throwable" }));
                    //            abstractInsnNodes.add(new VarInsnNode(Opcodes.ASTORE, method.maxLocals));
                    //            abstractInsnNodes.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    //            abstractInsnNodes.add(new FieldInsnNode(Opcodes.GETFIELD, strings2[0], fieldName1, "Ljava/lang/Object;"));
                    //            abstractInsnNodes.add(new InsnNode(Opcodes.MONITOREXIT));
                    //            abstractInsnNodes.add(new VarInsnNode(Opcodes.ALOAD, method.maxLocals));
                    //            abstractInsnNodes.add(new InsnNode(Opcodes.ATHROW));
                    //            abstractInsnNodes.add(labelNode3);
                    //            method.instructions.insertBefore(node, abstractInsnNodes);
                    //            break;
                    //        }
                    //    }
                    //}

                    method.maxStack++;
                    //method.maxLocals++;
                } else if (method.name.equals("<init>")) {
                    //debug_add2 = true;
                    InsnList instructions = method.instructions;
                    method.instructions = new InsnList();

                    for (AbstractInsnNode instruction : instructions) {
                        if (instruction.getOpcode() == Opcodes.PUTFIELD && instruction instanceof FieldInsnNode fieldInsnNode) {
                            if (fieldInsnNode.owner.equals(strings2[0]) && fieldInsnNode.name.equals(strings2[1])) {
                                debug_add2 = true;
                                method.instructions.add(instruction);
                                method.instructions.add(new TypeInsnNode(Opcodes.NEW, "java/lang/Object"));
                                method.instructions.add(new InsnNode(Opcodes.DUP));
                                method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false));
                                method.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, strings2[0], fieldName1, "Ljava/lang/Object;"));
                            }else {
                                method.instructions.add(instruction);
                            }
                        }else {
                            method.instructions.add(instruction);
                        }
                    }
                }
            }
        }

        if (!debug_add1 || !debug_add2){
            throw new RuntimeException("Not mapping error: net.minecraft.server.level.ServerLevel: "+ debug_add1 +" "+debug_add2);
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
