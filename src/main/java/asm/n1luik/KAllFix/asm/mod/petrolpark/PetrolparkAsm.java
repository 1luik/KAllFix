package asm.n1luik.KAllFix.asm.mod.petrolpark;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
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
public class PetrolparkAsm implements ITransformer<ClassNode> {
    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        int debug1 = 0;

        for (MethodNode method : input.methods) {
            if (method.name.equals("<clinit>") && method.desc.equals("()V")) {
                InsnList instructions = method.instructions;
                AbstractInsnNode start = null;
                AbstractInsnNode stop = null;
                for (AbstractInsnNode instruction : instructions) {
                    if (instruction.getOpcode() == Opcodes.PUTSTATIC && instruction instanceof FieldInsnNode fieldInsnNode) {
                        if (fieldInsnNode.owner.equals("com/petrolpark/Petrolpark") && fieldInsnNode.name.equals("MENU")) {
                            start = instruction.getPrevious();
                            stop = instruction;
                            log.info("找到Petrolpark.MENU");
                            while (start != null) {
                                if (start.getOpcode() == Opcodes.GETSTATIC && start instanceof FieldInsnNode fieldInsnNode2 && fieldInsnNode2.owner.equals("com/petrolpark/Petrolpark") && fieldInsnNode2.name.equals("REGISTRATE")){
                                    break;
                                }
                                start = start.getPrevious();
                            }
                            break;
                        }
                    }
                }
                log.info("开始修改");
                if (start == null){
                    log.error("可能是新版本的petrolpark");
                    return input;
                }
                InsnList nl = method.instructions = new InsnList();
                for (AbstractInsnNode instruction : instructions) {
                    if (instruction == start) {
                        debug1 = 1;
                    }else if (instruction == stop) {
                        debug1 = 2;
                        log.info("找到Petrolpark.REGISTRATE");
                        nl.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "n1luik/KAllFix/fix/petrolpark/MockPetrolparkItems2", "register", "()Lcom/tterrag/registrate/util/entry/ItemEntry;", false));
                        nl.add(instruction);
                    }else if (debug1 != 1){
                        nl.add(instruction);
                    }
                }
            }
        }
        if (debug1 != 2) {
            log.error("com.petrolpark.Petrolpark没有正常工作, 可能是新版本的petrolpark");
            //throw new RuntimeException("com.petrolpark.Petrolpark没有正常工作");
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
                Target.targetClass("com.petrolpark.Petrolpark")
        );
    }
}
