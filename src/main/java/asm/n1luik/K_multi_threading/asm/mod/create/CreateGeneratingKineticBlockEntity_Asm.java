package asm.n1luik.K_multi_threading.asm.mod.create;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

public class CreateGeneratingKineticBlockEntity_Asm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        boolean debug_add1 = false;
        boolean add1 = false;
        boolean add2 = false;

        if (input.name.equals("com/simibubi/create/content/kinetics/base/GeneratingKineticBlockEntity")){
            for (MethodNode method : input.methods) {
                if ((method.name.equals("updateGeneratedRotation") && method.desc.equals("()V"))){
                    debug_add1 = true;
                    InsnList instructions = method.instructions;
                    InsnList instructions2 = method.instructions = new InsnList();
                    LabelNode labelNode = new LabelNode();
                    //LabelNode labelNode2 = new LabelNode();
                    LabelNode labelNode3 = new LabelNode();

                    labelNode.getLabel().info = labelNode;
                    //labelNode2.getLabel().info = labelNode2;
                    labelNode3.getLabel().info = labelNode3;



                    for (AbstractInsnNode instruction : instructions) {
                        if (!add1 && instruction.getOpcode() == Opcodes.INVOKEVIRTUAL && instruction instanceof MethodInsnNode methodInsnNode
                                && methodInsnNode.owner.equals("com/simibubi/create/content/kinetics/base/GeneratingKineticBlockEntity")
                                && methodInsnNode.name.equals("hasSource")){
                            add1 = true;

                            instructions2.add(labelNode);

                            instructions2.add(new VarInsnNode(Opcodes.ALOAD,0));
                            instructions2.add(new FieldInsnNode(Opcodes.GETFIELD,"com/simibubi/create/content/kinetics/base/KineticBlockEntity","f_58857_","Lnet/minecraft/world/level/Level;"));

                            instructions2.add(new InsnNode(Opcodes.DUP));
                            instructions2.add(new VarInsnNode(Opcodes.ASTORE,method.maxLocals));

                            instructions2.add(new InsnNode(Opcodes.MONITORENTER));

                            instructions2.add(instruction);

                        }else if (!add2 && instruction.getOpcode() == Opcodes.INVOKEVIRTUAL && instruction instanceof MethodInsnNode methodInsnNode
                                && methodInsnNode.owner.equals("com/simibubi/create/content/kinetics/base/GeneratingKineticBlockEntity")
                                && methodInsnNode.name.equals("applyNewSpeed")){
                            add2 = true;
                            instructions2.add(instruction);

                            instructions2.add(new VarInsnNode(Opcodes.ALOAD,method.maxLocals));
                            instructions2.add(new InsnNode(Opcodes.MONITOREXIT));

                            //instructions2.add(labelNode2);


                            instructions2.add(labelNode3);

                        }else
                            instructions2.add(instruction);
                    }
                    method.visitLocalVariable("缓存_6","Ljava/lang/Object;","",labelNode.getLabel(),labelNode3.getLabel(),method.maxLocals);
                    method.maxLocals++;


                    //InsnList start = new InsnList();
                    //InsnList end = new InsnList();
                    //start = new InsnList();
                    //start.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    //start.add(new InsnNode(Opcodes.MONITORENTER));
                    //end = new InsnList();
                    //end.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    //end.add(new InsnNode(Opcodes.MONITOREXIT));
                    //InsnList il = method.instructions;
                    //AbstractInsnNode ain = il.getFirst();
                    //while (ain != null) {
                    //    if (ain.getOpcode() == Opcodes.ATHROW || ain.getOpcode() == Opcodes.RETURN || ain.getOpcode() == Opcodes.ARETURN
                    //            || ain.getOpcode() == Opcodes.DRETURN || ain.getOpcode() == Opcodes.FRETURN
                    //            || ain.getOpcode() == Opcodes.IRETURN || ain.getOpcode() == Opcodes.LRETURN) {
                    //        il.insertBefore(ain, end);
                    //        end = new InsnList();
                    //        end.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "asm/n1luik/K_multi_threading/asm/mod/Empty","empty","()V"));
                    //        end.add(new VarInsnNode(Opcodes.ALOAD, 1));
                    //        end.add(new InsnNode(Opcodes.MONITOREXIT));
                    //    }
                    //    ain = ain.getNext();
                    //}
                    //il.insertBefore(il.getFirst(), start);
                }
            }
        }

        if (!add1 || !add2){
            throw new RuntimeException("Not mapping error: com/simibubi/create/content/kinetics/base/GeneratingKineticBlockEntity");
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
                Target.targetClass("com/simibubi/create/content/kinetics/base/GeneratingKineticBlockEntity"));
    }
}
