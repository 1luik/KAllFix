package n1luik.K_multi_threading.forge;

import com.mojang.brigadier.arguments.StringArgumentType;
import cpw.mods.modlauncher.TransformingClassLoader;
import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.debug.GetterClassFileCommand;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.common.Mod;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;

@Mod(Base.MOD_ID)
public class ModInit {
    public static BiFunction<TransformingClassLoader, String, byte[]> getclass;
    static {
        try {
            Method buildTransformedClassNodeFor = TransformingClassLoader.class.getDeclaredMethod("buildTransformedClassNodeFor", String.class, String.class);
            buildTransformedClassNodeFor.setAccessible(true);
            getclass = (transformingClassLoader, s) ->
            {
                try {
                    return (byte[]) buildTransformedClassNodeFor.invoke(transformingClassLoader, s, s);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    public ModInit(){
        File file = new File("./_kmt_outc.txt");
        if (file.isFile()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                int i = 0;
                for (String v : new String(fileInputStream.readAllBytes()).split("(\\r\\n|\\n)+")) {
                    if (v.isEmpty())continue;

                    String name = v.replace("/", ".");
                    //try {
                        TransformingClassLoader classLoader = (TransformingClassLoader) GetterClassFileCommand.class.getClassLoader();
                        //classLoader.loadClass(name);
                        byte[] bytes = getclass.apply(classLoader, name);
                        try {
                            File file2 = new File("debug_save_"+(i++)+".class");
                            file2.createNewFile();
                            FileOutputStream fileOutputStream = new FileOutputStream(file2);
                            fileOutputStream.write(bytes);
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    //} catch (ClassNotFoundException e) {
                    //    e.printStackTrace();
                    //}
                }
                fileInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
