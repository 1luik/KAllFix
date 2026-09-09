package asm.n1luik.K_multi_threading.asm.mod;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import asm.n1luik.K_multi_threading.asm.util.ITransformer2;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
public class ClearMixinClass_ASM extends ITransformer2 {
    public final List<String> stringsList = new ArrayList<>(List.of(
            "dev/ryanhcode/sable/mixin/plot/LevelChunkMixin",
            "com.ishland.c2me.fixes.general.threading_issues.mixin.asynccatchers.MixinServerChunkManager".replace(".", "/")
    ));


    @Override
    public @NotNull ClassNode transform(ClassNode input) {

        for (String strings : stringsList) {
            if (input.name.equals(strings)) {
                input.methods.clear();
                input.fields.clear();
                log.info("remove mixin all: " + strings);
                return input;
            }
        }


        return input;
    }

    

    @Override
    public @NotNull Set<String> targets() {

        File f = new File("config/K_multi_threading-RemoveMixin-list.txt");
        if (f.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(f))) {
                r.lines().filter(s -> !(s.startsWith("#") || s.startsWith("//") || s.equals("")))
                        .forEach(stringsList::add);
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
        for (String strings : stringsList) {
            if (!list.contains(strings)) {
                list.add(strings);
            }
        }

        return Set.of(list.stream().toArray(String[]::new));
    }
}
