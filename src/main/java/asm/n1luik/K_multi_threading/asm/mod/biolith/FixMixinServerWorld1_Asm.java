package asm.n1luik.K_multi_threading.asm.mod.biolith;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class FixMixinServerWorld1_Asm implements ITransformer<ClassNode> {
    @NotNull
    @Override
    public ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        boolean debug_add1 = false;
        boolean debug_add2 = false;

        for (MethodNode method : input.methods) {
            if (method.name.equals("biolith$ServerWorld")) {
                //debug_add2 = true;
                debug_add1 = true;

                if (method.invisibleAnnotations != null) {
                    for (AnnotationNode invisibleAnnotation : method.invisibleAnnotations) {
                        if (fix(invisibleAnnotation)) {
                            debug_add2 = true;
                            break;
                        }
                    }
                }
                if (method.visibleAnnotations != null) {
                    for (AnnotationNode invisibleAnnotation : method.visibleAnnotations) {
                        if (fix(invisibleAnnotation)) {
                            debug_add2 = true;
                            break;
                        }
                    }
                }

            }
        }
        //method.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
        //                                    "n1luik/K_multi_threading/core/util/concurrent/FastUtilHackUtil",
        //                                    "concurrentMap",
        //                                    "(Ljava/util/Map;)Ljava/util/concurrent/ConcurrentHashMap;"))
        if (!(debug_add1 && debug_add2)){
            throw new RuntimeException("Not mapping error: %s %s".formatted(debug_add1, debug_add2));
        }


        return input;
    }

    public static boolean fix(AnnotationNode annotationNode){
        if (annotationNode.desc.equals("Lorg/spongepowered/asm/mixin/injection/Inject;")) {
            List<Object> values = annotationNode.values;
            boolean debug_add1 = false;
            boolean ret = false;
            for (int i = 0; i < values.size(); i+=2) {
                String name = (String)values.get(i);
                if (name.equals("at")) {
                    debug_add1 = true;
                    for (AnnotationNode annotationNode1 : (List<AnnotationNode>) values.get(i + 1)) {

                        try {//if (annotationNode1.desc.equals("Lorg/spongepowered/asm/mixin/injection/At;")) {
                            boolean test = true;
                            List<Object> values1 = annotationNode1.values;
                            ArrayList<Object> nv = new ArrayList<>(values1.size());
                            Integer by = null;
                            for (int i2 = 0; i2 < values1.size(); i2 += 2) {
                                String name2 = (String) values1.get(i2);
                                if (name2.equals("shift")) {
                                    nv.add(name2);
                                    String[] strings = (String[]) values1.get(i2 + 1);
                                    nv.add(strings);
                                    if (!strings[1].equals("BY")) {
                                        test = false;
                                        break;
                                    }
                                    //if (!strings[0].equals("Lorg/spongepowered/asm/mixin/injection/At/Shift;")){
                                    //    throw new RuntimeException("不兼容的mixin版本-出现了不该出现的类型: "+strings[0]);
                                    //}
                                } else if (name2.equals("by")) {
                                    by = (Integer) values1.get(i2 + 1);
                                } else {
                                    nv.add(name2);
                                    nv.add(values1.get(i2 + 1));
                                }
                            }
                            if (test) {
                                if (by == null) {
                                    throw new RuntimeException("不兼容的mixin版本-没有by: "+values1);
                                }
                                nv.add("by");
                                nv.add(by + 1);
                                annotationNode1.values = nv;
                                ret = true;
                            }
                        }catch (Exception throwable){//}else {
                            throw new RuntimeException("不兼容的mixin版本: "+annotationNode1.desc, throwable);
                        }
                    }
                    break;
                }
            }
            if (!debug_add1){
                throw new RuntimeException("不兼容的mixin版本: mixin的Inject我只知道他必须有at");
            }
            return ret;
        }
        return false;
    }

    @Override
    public @NotNull TransformerVoteResult castVote(ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {
        return Set.of(
                Target.targetClass("com.terraformersmc.biolith.impl.mixin.MixinServerWorld"));
    }
}
