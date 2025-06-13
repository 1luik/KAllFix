package asm.n1luik.KAllFix.asm.impl;

import asm.n1luik.KAllFix.asm.KAllFixAsm;
import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Set;
//先将就将就吧台麻烦了
public class PlainTextSearchTreeMultiThreading_Asm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        //String[] strings = ForgeAsm.minecraft_map.mapMethod("net/minecraft/client/searchtree/PlainTextSearchTree.create(Ljava/util/List;Ljava/util/function/Function;)Lnet/minecraft/client/searchtree/PlainTextSearchTree;");
        //String[] strings2 = ForgeAsm.minecraft_map.mapField("net/minecraft/server/level/ServerLevel.serverLevelData");
        //Class<?> def;
        //ClassNode node = new ClassNode();
        //MethodNode method = null;
        //try {
        //    ClassLoader classLoader = PlainTextSearchTreeMultiThreading_Asm.class.getClassLoader();
        //    def = Class.forName("net.minecraft.client.searchtree.PlainTextSearchTree", true, classLoader);
        //    new ClassReader(KAllFixAsm.getclass.apply((TransformingClassLoader) classLoader, "net.minecraft.client.searchtree.PlainTextSearchTree")).accept(node, 0);
//
        //} catch (ClassNotFoundException e) {
        //    throw new RuntimeException(e);
        //}
        //input.outerClass = node.name;
        //input.outerMethod = strings[1];
        //input.outerMethodDesc = strings[2];
        //for (MethodNode methodNode : node.methods) {
        //    if (methodNode.name.equals(strings[1]) && methodNode.desc.equals(strings[2])) {
        //        method = methodNode;
        //    }
        //}
        //if (method == null) {
        //    throw new RuntimeException("Method not found [net/minecraft/client/searchtree/PlainTextSearchTree.create(Ljava/util/List;Ljava/util/function/Function;)Lnet/minecraft/client/searchtree/PlainTextSearchTree;]");
        //}
        //method.instructions
//
//
        //boolean debug_add1 = false;
//
        //if (input.name.equals(strings[0])){
//
        //    for (MethodNode method : input.methods) {
        //        if (method.name.equals("<init>")) {
        //            //debug_add2 = true;
        //            InsnList instructions = method.instructions;
        //            method.instructions = new InsnList();
        //            debug_add1 = true;
//
//
        //            for (AbstractInsnNode instruction : instructions) {
        //                if (instruction.getOpcode() == Opcodes.NEW && instruction instanceof TypeInsnNode typeInsnNode) {
        //                    if (typeInsnNode.desc.equals("it/unimi/dsi/fastutil/objects/ObjectOpenHashSet")) {
        //                        method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
        //                                "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
        //                                "concurrentObjectSet",
        //                                "()Lit/unimi/dsi/fastutil/objects/ObjectSortedSet;"));
        //                    }else {
        //                        method.instructions.add(instruction);
        //                    }
        //                } else if (instruction.getOpcode() == Opcodes.INVOKESPECIAL && instruction instanceof MethodInsnNode methodInsnNode) {
        //                    if (methodInsnNode.owner.equals("it/unimi/dsi/fastutil/objects/ObjectOpenHashSet")
        //                    && methodInsnNode.name.equals("<init>") && methodInsnNode.desc.equals("()V")) {
        //                            method.instructions.add(new InsnNode(Opcodes.POP));
        //                    } else {
        //                        method.instructions.add(instruction);
        //                    }
        //                } else {
        //                    method.instructions.add(instruction);
        //                }
        //            }
        //        }
        //    }
        //}
//
        //if (!debug_add1){
        //    throw new RuntimeException("Not mapping error: net.minecraft.server.level.ServerLevel: "+ debug_add1);
        //}
//
//
        return input;
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {
        return Set.of(
                Target.targetClass("n1luik.KAllFix.impl.PlainTextSearchTreeMultiThreadingUtil$Call"));
    }
}
