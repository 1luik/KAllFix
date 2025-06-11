package asm.n1luik.KAllFix.asm.mod.createdieselgenerators;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class EntityMixinAsm implements ITransformer<ClassNode> {
    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        String[] strings = ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/entity/Entity.getSelfAndPassengers()Ljava/util/stream/Stream;");
        //input.access |= Opcodes.ACC_ABSTRACT;
        //if (input.interfaces == null) input.interfaces = new ArrayList<>();
        //input.interfaces.add("n1luik/KAllFix/Imixin/IEntityEx");

        for (MethodNode method : input.methods) {
            if (method.name.equals("tick")) {
                boolean enable = true;

                for (AbstractInsnNode instruction : method.instructions) {
                    if (instruction.getOpcode() == Opcodes.INVOKEINTERFACE && instruction instanceof MethodInsnNode methodInsnNode) {
                        if (methodInsnNode.name.equals("java/util/stream/Stream")) {
                            enable = false;
                        }
                    }
                }
                if (enable) return input;
                InsnList instructions = method.instructions;
                InsnList instructions1 = method.instructions = new InsnList();

                for (AbstractInsnNode instruction : instructions) {
                    if (instruction.getOpcode() == Opcodes.INVOKEINTERFACE && instruction instanceof MethodInsnNode methodInsnNode) {
                        if (!methodInsnNode.name.equals("java/util/stream/Stream") && !methodInsnNode.name.equals("java/util/List")) {
                            instructions1.add(instruction);
                        }
                    }else {
                        if (instruction.getOpcode() != Opcodes.ICONST_0) {
                            if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL && instruction instanceof MethodInsnNode methodInsnNode) {

                                if (!methodInsnNode.name.equals(strings[1])) {
                                    instructions1.add(instruction);
                                }// else {
                                    //instructions1.add(new VarInsnNode(Opcodes.ALOAD, 0));

                                //}
                            } else instructions1.add(instruction);
                        }
                    }
                }
            }
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
                Target.targetClass("com.jesz.createdieselgenerators.mixins.EntityMixin")
        );
    }
}
