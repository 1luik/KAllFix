package n1luik.K_multi_threading.fix.mek;

import n1luik.K_multi_threading.fix.FixGetterRoot;
import n1luik.K_multi_threading.fix.ICreateFix;
import net.minecraft.server.level.ServerLevel;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class MekanismWorld implements ICreateFix {
    public static FixGetterRoot INSTANCE;
    @Override
    public FixGetterRoot<?> createFix() {
        ClassLoader classLoader = MekanismWorld.class.getClassLoader();
        boolean enable;
        MethodHandle methodHandle;
        try {
            Class<?> aClass = Class.forName("mekanism.common.lib.transmitter.TransmitterNetworkRegistry", false, classLoader);
            Class<?> bClass = Class.forName("n1luik.K_multi_threading.fix.mek.FalseTransmitterNetworkRegistry", true, classLoader);
            enable = true;
            methodHandle = MethodHandles.lookup().findConstructor(bClass, MethodType.methodType(void.class, ServerLevel.class));
        } catch (ClassNotFoundException e) {
            enable = false;
            methodHandle = null;
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        boolean enable1 = enable;
        MethodHandle methodHandle1 = methodHandle;
        return INSTANCE = FixGetterRoot.create("Mekanism World", ()->enable1, ()->v-> {
            try {
                return methodHandle1.invoke(v);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        },()->v->null,()->v->null);
    }
}
