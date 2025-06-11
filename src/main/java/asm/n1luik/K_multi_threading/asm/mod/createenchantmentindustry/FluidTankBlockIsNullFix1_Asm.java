package asm.n1luik.K_multi_threading.asm.mod.createenchantmentindustry;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

public class FluidTankBlockIsNullFix1_Asm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        boolean debug_add1 = false;
        boolean add1 = false;
        boolean add2 = false;

        for (MethodNode method : input.methods) {
            if (method.name.equals("injected")){
                debug_add1 = true;
                InsnList instructions = method.instructions;
                InsnList instructions2 = method.instructions = new InsnList();



                for (AbstractInsnNode instruction : instructions) {
                    if (!add1 && instruction.getOpcode() == Opcodes.INVOKEVIRTUAL && instruction instanceof MethodInsnNode methodInsnNode
                            && methodInsnNode.owner.equals("com/simibubi/create/content/fluids/tank/FluidTankBlockEntity")
                            && methodInsnNode.name.equals("getControllerBE")
                            && methodInsnNode.desc.startsWith("()")){
                        LabelNode labelNode = new LabelNode();
                        labelNode.getLabel().info = labelNode;


                        instructions2.add(instruction);
                        instructions2.add(new InsnNode(Opcodes.DUP));
                        instructions2.add(new JumpInsnNode(Opcodes.IFNONNULL, labelNode));
                        instructions2.add(new InsnNode(Opcodes.RETURN));
                        instructions2.add(labelNode);

                    }else
                        instructions2.add(instruction);
                }

                method.maxStack++;
            }
        }

        if (!debug_add1){
            throw new RuntimeException("Not mapping error: plus/dragons/createenchantmentindustry/foundation/mixin/FluidTankBlockMixin");
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
                Target.targetClass("plus/dragons/createenchantmentindustry/foundation/mixin/FluidTankBlockMixin"));
    }
}
