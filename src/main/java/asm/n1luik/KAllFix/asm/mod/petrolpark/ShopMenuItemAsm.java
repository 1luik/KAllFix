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
public class ShopMenuItemAsm implements ITransformer<ClassNode> {
    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {
        int debug1 = 0;
        String[] strings = ForgeAsm.minecraft_map.mapMethod("com/petrolpark/shop/ShopMenuItem.m_7373_(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V");
        String[] strings2 = ForgeAsm.minecraft_map.mapMethod("com/petrolpark/shop/ShopMenuItem.getTeamSelectionScreenTitle(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/network/chat/Component;");
        List<MethodNode> methods = new ArrayList<>(input.methods.size()-1);
        for (MethodNode method : input.methods) {
            if ((!method.name.equals(strings2[1]) || !method.desc.equals(strings2[2])) &&
                    (!method.name.equals(strings[1]) || !method.desc.equals(strings[2]))) {
                methods.add(method);
            }else {
                debug1++;
            }


        }
        input.methods = methods;
        if (debug1 != 2) {
            log.error("ShopMenuItemAsm没有正常工作，可能是新版本的petrolpark");
            //throw new RuntimeException("ShopMenuItemAsm没有正常工作");
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
                Target.targetClass("com.petrolpark.shop.ShopMenuItem")
        );
    }
}
