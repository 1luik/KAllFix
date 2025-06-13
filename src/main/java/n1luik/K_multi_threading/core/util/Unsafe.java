package n1luik.K_multi_threading.core.util;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.*;

//D:\-saa\openjdk-11+28_windows-x64_bin\jdk-11\bin/java.exe -agentpath:D:\JPROFI~2\bin\WINDOW~1\jprofilerti.dll=port=12345 -cp log4j-iostreams-2.17.1.jar --add-opens java.base/jdk.internal.loader=ALL-UNNAMED --illegal-access=warn -Xmx10G -Xms10G -jar forge-1.16.5-36.2.39-launcher.jar
@SuppressWarnings("all")
public class Unsafe {
    public static final sun.misc.Unsafe unsafe;
    public static final MethodHandles.Lookup lookup;
    public static final MethodHandle defineClass;

    static {
        try {
            Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (sun.misc.Unsafe) theUnsafe.get(null);
            unsafe.ensureClassInitialized(MethodHandles.Lookup.class);
            Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            Object base = unsafe.staticFieldBase(field);
            long offset = unsafe.staticFieldOffset(field);
            lookup = (MethodHandles.Lookup) unsafe.getObject(base, offset);
            MethodHandle mh;
            try {
                Method sunMisc = unsafe.getClass().getMethod("defineClass", String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class);
                mh = lookup.unreflect(sunMisc).bindTo(unsafe);
            } catch (Exception e) {
                Class<?> jdkInternalUnsafe = Class.forName("jdk.internal.misc.Unsafe");
                Field internalUnsafeField = jdkInternalUnsafe.getDeclaredField("theUnsafe");
                Object internalUnsafe = unsafe.getObject(unsafe.staticFieldBase(internalUnsafeField), unsafe.staticFieldOffset(internalUnsafeField));
                Method internalDefineClass = jdkInternalUnsafe.getMethod("defineClass", String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class);
                mh = lookup.unreflect(internalDefineClass).bindTo(internalUnsafe);
            }
            defineClass = Objects.requireNonNull(mh);
            //Jadd("--add-exports java.base/jdk.internal.misc=ALL-UNNAMED");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param cl         注入的类型
     * @param ctorTypes  注入的数据类型（ImmutableList.of(class1.class,class2.class,...)）
     * @param i          注入的是第几个元素
     * @param ctorParams 数据
     */
    @SuppressWarnings("unchecked")
    public static <T> T makeEnum(Class<T> cl, String name, int i, List<Class<?>> ctorTypes, List<Object> ctorParams) {
        try {
            unsafe.ensureClassInitialized(cl);
            List<Class<?>> ctor = new ArrayList<>(ctorTypes.size() + 2);
            ctor.add(String.class);//名字
            ctor.add(int.class);//第几个元素
            ctor.addAll(ctorTypes);//注入的数据类型（ImmutableList.of(class1.class,class2.class,...)
            MethodHandle constructor = lookup.findConstructor(cl, MethodType.methodType(void.class, ctor));//这tm注入是枚举元素
            List<Object> param = new ArrayList<>(ctorParams.size() + 2);
            param.add(name);
            param.add(i);
            param.addAll(ctorParams);
            return (T) constructor.invokeWithArguments(param);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getStatic(Class<?> cl, String name) {
        try {
            unsafe.ensureClassInitialized(cl);
            Field field = cl.getDeclaredField(name);
            Object materialByNameBase = unsafe.staticFieldBase(field);
            long materialByNameOffset = unsafe.staticFieldOffset(field);
            return (T) unsafe.getObject(materialByNameBase, materialByNameOffset);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> publicJava(Class<?> cl) {
        Map<String, Object> ret = new HashMap<>();
        for (Method method1 : cl.getDeclaredMethods()) {
            method1.setAccessible(true);
            ret.put(method1.toString(), method1);
        }
        for (Field declaredField : cl.getDeclaredFields()) {
            declaredField.setAccessible(true);
            ret.put(declaredField.toString(), declaredField);
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public static Object publicJava(Class<?> cl, String name, Object val, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = cl.getDeclaredMethod(name);
        method.setAccessible(true);
        return method.invoke(val, args);
    }

    @SuppressWarnings("unchecked")
    public static Object _MpublicJava(Class<?> cl, String name, Object val, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        for (Method declaredMethod : cl.getDeclaredMethods()) {
            if (declaredMethod.getName().equals(name)) {
                try {
                    declaredMethod.setAccessible(true);
                } catch (Throwable e) {
                }
                return declaredMethod.invoke(val, args);
            }
        }
        Method method = cl.getDeclaredMethod(name);
        method.setAccessible(true);
        return method.invoke(val, args);
    }

    @SuppressWarnings("unchecked")
    public static Field publicJava(Class<?> cl, String name) throws NoSuchFieldException {
        Field field = cl.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    @SuppressWarnings("unchecked")
    public static Object _publicJava(Class<?> cl, String name, Object var) throws NoSuchFieldException, IllegalAccessException {
        Field field = cl.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(var);
    }
    @SuppressWarnings("unchecked")
    public static boolean _publicJavaBool(Class<?> cl, String name, Object var) throws NoSuchFieldException, IllegalAccessException {
        Field field = cl.getDeclaredField(name);
        field.setAccessible(true);
        return field.getBoolean(var);
    }

    @SuppressWarnings("unchecked")
    public static <T> void _publicJava(Class<T> cl, String name, Object var, Object setvar) throws NoSuchFieldException, IllegalAccessException {
        Field field = cl.getDeclaredField(name);
        field.setAccessible(true);
        field.set(var, setvar);
    }

    @SuppressWarnings("unchecked")
    public static void setfinal(Field field, Object in, Object put) {
        unsafe.putObject(put, unsafe.objectFieldOffset(field), in);
    }
    @SuppressWarnings("unchecked")
    public static void setfinalBool(Field field, boolean in, Object put) {
        unsafe.putBoolean(put, unsafe.objectFieldOffset(field), in);
    }

    //瞎改的能用就行
    @SuppressWarnings("all")
    public static void Jadd(String jadds) {

        String[] moloader_args;
        String runClass = "";
        String[] lib = null;
        try {
            String[] lib2 = new String[]{};
            //InputStream runtexeIn = new FileInputStream(new File(tBin,"win_args.txt"));
            StringBuilder Add_args = new StringBuilder();
            List<String> opens = new ArrayList<>();
            List<String> exports = new ArrayList<>();
            List<String> modules = new ArrayList<>();
            //exports.add("cpw.mods.bootstraplauncher/cpw.mods.bootstraplauncher=ALL-UNNAMED");

            for (String s : jadds.split("(\n|\r\n)+")) {
                if (!runClass.equals("")) {
                    Add_args.append(s).append(" ");
                    continue;
                }
                if (!s.startsWith("-")) {
                    runClass = s;
                    continue;
                } else if (s.startsWith("-p ")) {
                    lib = s.replace("-p ", "").split(";", -1);
                } else if (s.startsWith("-DlegacyClassPath=")) {
                    lib2 = s.replace("-DlegacyClassPath=", "").split(";", -1);
                } else if (s.startsWith("-D")) {
                    String[] s1 = s.substring(2).split("=", 2);
                    System.setProperty(s1[0], s1[1]);
                } else if (s.startsWith("--add-opens ") || s.startsWith("--add-opens=")) {
                    opens.add(s.substring("--add-opens ".length()).trim());
                } else if (s.startsWith("--add-exports ") || s.startsWith("--add-exports=")) {
                    exports.add(s.substring("--add-exports ".length()).trim());
                }
            }
            MethodHandles.Lookup IMPL_LOOKUP = Unsafe.lookup;

            try {
                MethodHandle Opens_implAddOpensMH = IMPL_LOOKUP.findVirtual(Module.class, "implAddOpens", MethodType.methodType(void.class, String.class, Module.class));
                MethodHandle Opens_implAddOpensToAllUnnamedMH = IMPL_LOOKUP.findVirtual(Module.class, "implAddOpensToAllUnnamed", MethodType.methodType(void.class, String.class));
                opens.forEach(extra -> {
                    //ParserData data = parseModuleExtra(extra);
                    String[] all = extra.split("=", 2);
                    if (all.length < 2) {
                        return;
                    }

                    String[] source = all[0].split("/", 2);
                    if (source.length < 2) {
                        return;
                    }
                    final String module = source[0];
                    final String packages = source[1];
                    final String target = all[1];
                    ModuleLayer.boot().findModule(module).ifPresent(m -> {
                        try {
                            if ("ALL-UNNAMED".equals(target)) {
                                Opens_implAddOpensToAllUnnamedMH.invokeWithArguments(m, packages);
                            } else {
                                ModuleLayer.boot().findModule(target).ifPresent(tm -> {
                                    try {
                                        Opens_implAddOpensMH.invokeWithArguments(m, packages, tm);
                                    } catch (Throwable t) {
                                        throw new RuntimeException(t);
                                    }
                                });
                            }
                        } catch (Throwable t) {
                            throw new RuntimeException(t);
                        }
                    });

                });
                MethodHandle Exports_implAddExportsMH = IMPL_LOOKUP.findVirtual(Module.class, "implAddExports", MethodType.methodType(void.class, String.class, Module.class));
                MethodHandle Exports_implAddExportsToAllUnnamedMH = IMPL_LOOKUP.findVirtual(Module.class, "implAddExportsToAllUnnamed", MethodType.methodType(void.class, String.class));

                exports.forEach(extra -> {
                    //ParserData data = parseModuleExtra(extra);
                    String[] all = extra.split("=", 2);
                    if (all.length < 2) {
                        return;
                    }

                    String[] source = all[0].split("/", 2);
                    if (source.length < 2) {
                        return;
                    }
                    final String module = source[0];
                    final String packages = source[1];
                    final String target = all[1];
                    ModuleLayer.boot().findModule(module).ifPresent(m -> {
                        try {
                            if ("ALL-UNNAMED".equals(target)) {
                                Exports_implAddExportsToAllUnnamedMH.invokeWithArguments(m, packages);
                            } else {
                                ModuleLayer.boot().findModule(target).ifPresent(tm -> {
                                    try {
                                        Exports_implAddExportsMH.invokeWithArguments(m, packages, tm);
                                    } catch (Throwable t) {
                                        throw new RuntimeException(t);
                                    }
                                });
                            }
                        } catch (Throwable t) {
                            throw new RuntimeException(t);
                        }
                    });

                });


            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        } finally {

        }
    }

    public static long addressOf(Object o) {

        Object[] array = new Object[]{o};

        long baseOffset = unsafe.arrayBaseOffset(Object[].class);
        int addressSize = unsafe.addressSize();
        long objectAddress;
        switch (addressSize) {
            case 4:
                objectAddress = unsafe.getInt(array, baseOffset);
                break;
            case 8:
                objectAddress = unsafe.getLong(array, baseOffset);
                break;
            default:
                throw new RuntimeException("你的内存一定大于9223372036854775807bit了吧，或者你要瞎搞jvm好吗这玩意他不行搞 或者你是一个非常nb的人你会印cpu？？？ 我整个紧适用x86或amd64也可能是arm64但是他不适用与未来啊老弟     因为描述描述内存他有这么大啊: " + addressSize);
        }
        return (objectAddress);
    }

    public static Class defineClass(String name, byte[] b, ProtectionDomain pd){
        try {
            return (Class) Unsafe.defineClass.invoke(name, b, 0, b.length, pd);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Class defineClass(String name, byte[] b, CodeSource codeSource, ClassLoader loader){
        Permissions permissions = new Permissions();
        permissions.add(new AllPermission());
        return defineClass(name, b, new ProtectionDomain(codeSource, permissions, loader, null));
    }

    public static Class defineClass(String name, byte[] b, CodeSource codeSource) {
        return defineClass(name, b, codeSource, Thread.currentThread().getContextClassLoader());
    }

    public static Class defineClass(String name, byte[] b) {
        return  defineClass(name, b, new CodeSource(null, (java.security.cert.Certificate[])null));
    }
    /**
     * 这个开销大
     * @param loader MethodHandles.Lookup只能使用class读取他的类加载器
     * @param implType 打包的类型
     * */
    public static CallSite metafactory(Class<?> loader, Method impl, MethodHandle call) throws IllegalAccessException, LambdaConversionException {
        MethodHandles.Lookup lookup1 = MethodHandles.privateLookupIn(loader, Unsafe.lookup);
        MethodType type = call.type();
        MethodType methodType = MethodType.methodType(impl.getDeclaringClass());

        return LambdaMetafactory.metafactory(
                lookup1,
                impl.getName(),
                methodType,
                MethodType.methodType(impl.getReturnType(), impl.getParameterTypes()),
                call,
                type);
    }
    /**
     * 这个开销大
     * @param loader MethodHandles.Lookup只能使用class读取他的类加载器
     * @param implType 打包的类型
     * */
    public static CallSite metafactory(MethodHandles.Lookup loader, Method impl, MethodHandle call) throws LambdaConversionException {
        MethodType type = call.type();
        MethodType methodType = MethodType.methodType(impl.getDeclaringClass());

        return LambdaMetafactory.metafactory(
                loader,
                impl.getName(),
                methodType,
                MethodType.methodType(impl.getReturnType(), impl.getParameterTypes()),
                call,
                type);
    }


    public static MethodHandles.Lookup privateLookupIn(Class<?> loader) throws IllegalAccessException {
        return MethodHandles.privateLookupIn(loader, Unsafe.lookup);
    }
}
