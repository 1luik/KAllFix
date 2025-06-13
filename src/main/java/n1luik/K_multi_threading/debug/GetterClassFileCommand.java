package n1luik.K_multi_threading.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import cpw.mods.modlauncher.TransformingClassLoader;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;

public class GetterClassFileCommand {
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
    public static void register(CommandDispatcher<CommandSourceStack> p_214446_) {
            p_214446_.register(Commands.literal("debug_GetterClassFile").requires((p_137777_) -> {
                return p_137777_.hasPermission(3);
            }).then(Commands.argument("name", StringArgumentType.string())
                    .executes(v->{
                        String name = StringArgumentType.getString(v, "name").replace("/", ".");
                        try {
                            TransformingClassLoader classLoader = (TransformingClassLoader) GetterClassFileCommand.class.getClassLoader();
                            Class<?> aClass = classLoader.loadClass(name);
                            byte[] bytes = getclass.apply(classLoader, name);
                            try {
                                File file = new File(System.currentTimeMillis() + "_save.class");
                                file.createNewFile();
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(bytes);
                                fileOutputStream.close();
                                v.getSource().sendSystemMessage(Component.literal("保存成功 "+name));
                            } catch (IOException e) {
                                e.printStackTrace();
                                return 0;
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            v.getSource().sendSystemMessage(Component.literal("没有这个class "+name));
                            return 0;
                        }
                        return 1;

                    })
        ));
    }
}
