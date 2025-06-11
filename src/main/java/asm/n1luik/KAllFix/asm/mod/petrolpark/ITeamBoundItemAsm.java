package asm.n1luik.KAllFix.asm.mod.petrolpark;

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
public class ITeamBoundItemAsm implements ITransformer<ClassNode> {
    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        int debug1 = 0;
        int debug2 = 0;
        String[] strings = ForgeAsm.minecraft_map.mapMethod("com/petrolpark/team/ITeamBoundItem.getTeamSelectionScreenTitle(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/network/chat/Component;");
        String[] strings2 = ForgeAsm.minecraft_map.mapMethod("com/petrolpark/team/ITeamBoundItem.trySelectTeam(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/InteractionResult;");
        String[] strings3 = ForgeAsm.minecraft_map.mapMethod("com/petrolpark/team/ITeamBoundItem.openScreen(Lnet/minecraft/network/chat/Component;Ljava/util/List;)V");
        List<MethodNode> methods = new ArrayList<>(input.methods.size()-1);
        for (MethodNode method : input.methods) {
             if (method.name.equals(strings2[1]) && method.desc.equals(strings2[2])) {
                debug1++;
                InsnList instructions = method.instructions;
                AbstractInsnNode start = null;
                AbstractInsnNode stop = null;
                for (AbstractInsnNode instruction : instructions) {
                    if (instruction.getOpcode() == Opcodes.GETSTATIC && instruction instanceof FieldInsnNode fieldInsnNode &&
                        fieldInsnNode.owner.equals("net/minecraftforge/api/distmarker/Dist") && fieldInsnNode.name.equals("CLIENT")) {
                        start = instruction;

                        stop = instruction.getNext();
                        while (stop!= null) {
                            if (stop.getOpcode() == Opcodes.INVOKESTATIC && stop instanceof MethodInsnNode methodInsnNode &&
                                methodInsnNode.owner.equals("net/minecraftforge/fml/DistExecutor") && methodInsnNode.name.equals("unsafeRunWhenOn") && methodInsnNode.desc.equals("(Lnet/minecraftforge/api/distmarker/Dist;Ljava/util/function/Supplier;)V")) {
                                break;
                            }
                            stop = stop.getNext();
                        }
                        log.info("找到ITeamBoundItem.trySelectTeam");
                    }
                }
                 if (stop == null){
                     log.error("可能是新版本的petrolpark");
                     return input;
                 }
                 InsnList nl = method.instructions = new InsnList();
                 for (AbstractInsnNode instruction : instructions) {
                    if (instruction == start) {
                        debug2 = 1;
                    }else if (instruction == stop) {
                        debug2 = 2;
                        log.info("找到ITeamBoundItem.openScreen");
                    }else if (debug2!= 1){
                        nl.add(instruction);
                    }
                }
                if (debug2 != 2) {
                    //throw new RuntimeException("ITeamBoundItem.trySelectTeam没有正常工作");
                    log.error("ITeamBoundItem.trySelectTeam没有正常工作，可能是新版本的petrolpark");
                }
                methods.add(method);
            }else if ((!method.name.equals(strings3[1]) || !method.desc.equals(strings3[2])) && (!method.name.equals(strings[1]) || !method.desc.equals(strings[2]))) {
                methods.add(method);
            }else {
                debug1++;
            }


        }
        input.methods = methods;
        if (debug1 != 3) {
            //throw new RuntimeException("ITeamBoundItemAsm没有正常工作");
            log.error("ITeamBoundItemAsm没有正常工作，可能是新版本的petrolpark");
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
                Target.targetClass("com.petrolpark.team.ITeamBoundItem")
        );
    }
}
