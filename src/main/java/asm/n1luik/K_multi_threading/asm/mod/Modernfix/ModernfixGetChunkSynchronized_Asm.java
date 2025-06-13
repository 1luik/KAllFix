package asm.n1luik.K_multi_threading.asm.mod.Modernfix;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.extern.slf4j.Slf4j;
import n1luik.K_multi_threading.core.Base;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;

@Slf4j
public class ModernfixGetChunkSynchronized_Asm implements ITransformer<ClassNode> {
    //@NotNull
    //@Override
    //public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
    //    String[] strings = ForgeAsm.minecraft_map.mapField("net/minecraft/server/level/ServerChunkCache.mainThread");
    //    boolean debug_add1 = false;
//
    //    if (input.name.equals(strings[0])){
    //        for (MethodNode method : input.methods) {
    //            if (method.name.equals("handler$zdl000$bailIfServerDead")){
    //                InsnList instructions = method.instructions;
    //                InsnList instructions2 = method.instructions = new InsnList();
    //                for (AbstractInsnNode instruction : instructions) {
    //                    if (instruction.getOpcode() == Opcodes.INVOKESTATIC && instruction instanceof MethodInsnNode methodInsnNode
    //                            && methodInsnNode.owner.equals("java/lang/Thread") && methodInsnNode.name.equals("currentThread")) {
    //                        debug_add1 = true;
    //                        log.info("ModernfixGetChunkSynchronized Asm");
    //                        instructions2.add(new VarInsnNode(Opcodes.ALOAD, 0));//this
    //                        instructions2.add(new FieldInsnNode(Opcodes.GETFIELD, strings[0], strings[1], "Ljava/lang/Thread;"));//this
    //                    }
    //                    instructions2.add(instruction);
    //                }
    //            }
    //        }
    //    }
//
    //    if (!debug_add1){
    //        log.error("Not method error");
    //    }
//
//
    //    return input;
    //}
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        String[] strings = ForgeAsm.minecraft_map.mapField("net/minecraft/server/level/ServerChunkCache.mainThread");
        boolean debug_add1 = false;

        for (MethodNode method : input.methods) {
            InsnList instructions = method.instructions;
            InsnList instructions2 = method.instructions = new InsnList();
            for (AbstractInsnNode instruction : instructions) {
                if (instruction.getOpcode() == Opcodes.INVOKESTATIC && instruction instanceof MethodInsnNode methodInsnNode
                        && methodInsnNode.owner.equals("java/lang/Thread") && methodInsnNode.name.equals("currentThread")) {
                    debug_add1 = true;
                    log.info("ModernfixGetChunkSynchronized Asm");
                    instructions2.add(new VarInsnNode(Opcodes.ALOAD, 0));//this
                    instructions2.add(new FieldInsnNode(Opcodes.GETFIELD, "org/embeddedt/modernfix/common/mixin/bugfix/chunk_deadlock/ServerChunkCacheMixin", strings[1], "Ljava/lang/Thread;"));//this
                }else {
                    instructions2.add(instruction);
                }
            }
        }

        if (!debug_add1){
            log.error("Not method error");//throw new RuntimeException("Not method");
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
                //Target.targetClass("net/minecraft/server/level/ServerChunkCache"));
                Target.targetClass("org/embeddedt/modernfix/common/mixin/bugfix/chunk_deadlock/ServerChunkCacheMixin"));
    }
}
