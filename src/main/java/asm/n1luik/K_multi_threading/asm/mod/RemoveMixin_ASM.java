package asm.n1luik.K_multi_threading.asm.mod;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
public class RemoveMixin_ASM implements ITransformer<ClassNode> {
    public static final List<String[]> stringsList = new ArrayList<>(List.of(
            new String[]{"com/gregtechceu/gtceu/core/mixins/LevelMixin","getTileEntity"},
            new String[]{"com/github/alexthe666/iceandfire/mixin/gen/NoLakesInStructuresMixin","iaf_noLakesInMausoleum"}

    ));


    @Override
    public @NotNull ClassNode transform(ClassNode input, ITransformerVotingContext context) {

        for (String[] strings : stringsList) {
            if (input.name.equals(strings[0])){
                boolean debug_add1 = false;
                for (MethodNode method : input.methods) {
                    if ((method.name.equals(strings[1]))) {
                        debug_add1 = true;
                        log.info("remove mixin: " + Arrays.toString(strings));

                        if(method.visibleAnnotations != null)method.visibleAnnotations.clear();
                        if(method.invisibleAnnotations != null)method.invisibleAnnotations.clear();
                    }
                }

                if (!debug_add1){
                    log.error("Not method error: " + Arrays.toString(strings));
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

        File f = new File("config/K_multi_threading-RemoveMixin-list.txt");
        if (f.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(f))) {
                r.lines().filter(s -> !(s.startsWith("#") || s.startsWith("//") || s.equals("")))
                        .map(ForgeAsm.minecraft_map::mapMethod).forEach(stringsList::add);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();
                FileWriter fw = new FileWriter(f);
                fw.write("""
                        // 使用//或#屏蔽
                        // 这个文件是用于对单独的mixin函数移除接口的
                        // 只能使用函数名
                        
                        // 如何使用:
                        //com/gregtechceu/gtceu/core/mixins/LevelMixin.getTileEntity
                        //跟映射表格式一样simple
                        
                        """);
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ArrayList<String> list = new ArrayList<>();
        for (String[] strings : stringsList) {
            if (!list.contains(strings[0])) {
                list.add(strings[0]);
            }
        }

        return Set.of(list.stream().map(Target::targetClass).toArray(Target[]::new));
    }
}
