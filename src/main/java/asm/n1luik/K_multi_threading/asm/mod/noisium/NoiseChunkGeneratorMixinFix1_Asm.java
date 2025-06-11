package asm.n1luik.K_multi_threading.asm.mod.noisium;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

public class NoiseChunkGeneratorMixinFix1_Asm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        boolean add1 = false;
        String[] strings = ForgeAsm.minecraft_map.mapMethod("net/minecraft/world/level/chunk/ChunkAccess.getSections()[Lnet/minecraft/world/level/chunk/LevelChunkSection;");

        for (MethodNode method : input.methods) {
            if (method.name.equals("noisium$populateNoiseInject")){
                InsnList instructions = method.instructions;
                InsnList instructions2 = method.instructions = new InsnList();



                for (AbstractInsnNode instruction : instructions) {

                    if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL && instruction instanceof MethodInsnNode methodInsnNode){
                        if (methodInsnNode.owner.equals(strings[0]) && methodInsnNode.name.equals(strings[1]) && methodInsnNode.desc.equals(strings[2])){
                            instructions2.add(instruction);
                            instructions2.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
                                    "n1luik/K_multi_threading/fix/NoiseChunkGeneratorMixinFix1Fun", "fun1", "([Lnet/minecraft/world/level/chunk/LevelChunkSection;)[Lnet/minecraft/world/level/chunk/LevelChunkSection;"));
                            add1 = true;
                        }else
                            instructions2.add(instruction);

                    }else
                        instructions2.add(instruction);
                }
            }
        }

        if (!add1){
            throw new RuntimeException("Not mapping error: io/github/steveplays28/noisium/mixin/NoiseChunkGeneratorMixin");
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
                Target.targetClass("io/github/steveplays28/noisium/mixin/NoiseChunkGeneratorMixin"),
                Target.targetClass("io/github/steveplays28/noisium/mixin/compat/lithium/LithiumNoiseChunkGeneratorMixin"));
    }
}
