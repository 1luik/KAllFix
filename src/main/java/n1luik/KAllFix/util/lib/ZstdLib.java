package n1luik.KAllFix.util.lib;

import lombok.Getter;
import n1luik.KAllFix.util.AutoLib;
import n1luik.K_multi_threading.core.util.ConstOB2;
import n1luik.K_multi_threading.core.util.OB2;
import n1luik.K_multi_threading.core.util.Unsafe;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.function.Function;

@Getter
public class ZstdLib extends AutoLib<ConstOB2<Function<InputStream, InputStream>, Function<OutputStream, OutputStream>>> {
    public static final ZstdLib INSTANCE;
    public static final Function<InputStream, InputStream> in;
    public static final Function<OutputStream, OutputStream> out;
    static {

        try {
            INSTANCE = new ZstdLib();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        in = INSTANCE.instance.t1;
        out = INSTANCE.instance.t2;

    }
    public static void cinit(){

    }
    public final ConstOB2<Function<InputStream, InputStream>, Function<OutputStream, OutputStream>> instance;

    @SuppressWarnings("all")
    public ZstdLib() throws Throwable {
        super("https://repo1.maven.org/maven2", "1.5.7-2", "zstd-jni", "com.github.luben");
        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(Class.forName("com.github.luben.zstd.Zstd", true, this), Unsafe.lookup);
        MethodType interfaceMethodType = MethodType.methodType(InputStream.class, InputStream.class);
        MethodType interfaceMethodType2 = MethodType.methodType(OutputStream.class, OutputStream.class);
        MethodType interfaceMethodType3 = MethodType.methodType(Object.class, Object.class);
        MethodType factoryType = MethodType.methodType(Function.class);
        instance = new ConstOB2<>(
                (Function<InputStream, InputStream>)LambdaMetafactory.metafactory(
                        lookup,
                        "apply",
                        factoryType,
                        interfaceMethodType3,
                        lookup.findConstructor(loadClass("com.github.luben.zstd.ZstdInputStream"), MethodType.methodType(void.class, InputStream.class)),
                        interfaceMethodType
                ).dynamicInvoker().invoke(),
                (Function<OutputStream, OutputStream>)LambdaMetafactory.metafactory(
                        lookup,
                        "apply",
                        factoryType,
                        interfaceMethodType3,
                        lookup.findConstructor(loadClass("com.github.luben.zstd.ZstdOutputStream"), MethodType.methodType(void.class, OutputStream.class)),
                        interfaceMethodType2
                ).dynamicInvoker().invoke()
        );
    }
}   
