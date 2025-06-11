package asm.n1luik.K_multi_threading.asm.mod.create;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

public class CreateTrackGraphSynchronized_Asm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        boolean debug_add1 = false;
        boolean add1 = false;
        boolean add2 = false;

        for (MethodNode method : input.methods) {
            if ((method.name.equals("connectNodes")
                    && method.desc.equals("(Lnet/minecraft/world/level/LevelAccessor;Lcom/simibubi/create/content/trains/graph/TrackNodeLocation$DiscoveredLocation;Lcom/simibubi/create/content/trains/graph/TrackNodeLocation$DiscoveredLocation;Lcom/simibubi/create/content/trains/track/BezierConnection;)V"))){
                debug_add1 = true;
                InsnList instructions = method.instructions;
                InsnList instructions2 = method.instructions = new InsnList();
                LabelNode exitLabelNode = null;

                LabelNode labelNode = new LabelNode();
                //LabelNode labelNode2 = new LabelNode();
                LabelNode labelNode3 = new LabelNode();

                labelNode.getLabel().info = labelNode;
                //labelNode2.getLabel().info = labelNode2;
                labelNode3.getLabel().info = labelNode3;



                for (AbstractInsnNode instruction : instructions) {

                    if (!add1 && instruction.getOpcode() == Opcodes.CHECKCAST){
                        AbstractInsnNode previous = instruction.getPrevious();
                        AbstractInsnNode previous2 = previous == null ? null : previous.getPrevious();
                        AbstractInsnNode previous3 = previous2 == null ? null : previous2.getPrevious();
                        AbstractInsnNode next = instruction.getNext();

                        if (previous != null && next != null && previous3 != null
                                && instruction.getOpcode() == Opcodes.CHECKCAST && instruction instanceof TypeInsnNode typeInsnNode
                                && typeInsnNode.desc.equals("com/simibubi/create/content/trains/graph/TrackGraph")
                                //
                                && next.getOpcode() == Opcodes.ASTORE
                                && previous.getOpcode() == Opcodes.INVOKEINTERFACE && previous instanceof MethodInsnNode methodInsnNode
                                && methodInsnNode.owner.equals("java/util/Iterator")
                                && methodInsnNode.name.equals("next") && methodInsnNode.desc.equals("()Ljava/lang/Object;")
                                //
                                && previous3.getOpcode() == Opcodes.IFEQ && previous3 instanceof JumpInsnNode jumpInsnNode
                        ) {
                            exitLabelNode = jumpInsnNode.label;

                            add1 = true;

                            instructions2.add(labelNode);

                            instructions2.add(new InsnNode(Opcodes.DUP));
                            instructions2.add(new InsnNode(Opcodes.DUP));
                            instructions2.add(new VarInsnNode(Opcodes.ASTORE, method.maxLocals));

                            instructions2.add(new InsnNode(Opcodes.MONITORENTER));

                            instructions2.add(instruction);

                        }else
                            instructions2.add(instruction);
                    }else if (add1 && !add2 && instruction.getOpcode() == Opcodes.GOTO){
                        AbstractInsnNode next = instruction.getNext();
                        if (next != null && next.getOpcode() == -1
                            && next == exitLabelNode){
                        add2 = true;

                        instructions2.add(new VarInsnNode(Opcodes.ALOAD,method.maxLocals));
                        instructions2.add(new InsnNode(Opcodes.MONITOREXIT));

                        //instructions2.add(labelNode2);


                        instructions2.add(labelNode3);

                        instructions2.add(instruction);

                        }else
                            instructions2.add(instruction);
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

        if (!add1 || !add2){
            throw new RuntimeException("Not mapping error: com/simibubi/create/content/trains/graph/TrackGraph");
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
                Target.targetClass("com/simibubi/create/content/trains/graph/TrackGraph"));
    }
}
