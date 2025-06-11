package asm.n1luik.K_multi_threading.asm.mod.gtceu;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

public class LevelMixin_Asm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        for (MethodNode method : input.methods) {
            if (/*method.name.equals("getBlockState") || */method.name.equals("getTileEntity")) {
                if(method.visibleAnnotations != null)method.visibleAnnotations.clear();
                if(method.invisibleAnnotations != null)method.invisibleAnnotations.clear();
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
                Target.targetClass("com.gregtechceu.gtceu.core.mixins.LevelMixin"));
    }
}
