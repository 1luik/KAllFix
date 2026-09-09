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

    private static final int JAVA_VERSION = Runtime.version().feature();
    private static final boolean JAVA_21_OR_LATER = JAVA_VERSION >= 21;

    // 缓存字段
    private static final Object unsafeInstance;
    private static final MethodHandle defineClassMH;
    private static final MethodHandle ensureInitializedMH;
    private static final MethodHandle implAddOpensMH;
    private static final MethodHandle implAddOpensToAllUnnamedMH;
    private static final MethodHandle implAddExportsMH;
    private static final MethodHandle implAddExportsToAllUnnamedMH;
    private static final MethodHandle staticFieldBaseMH;
    private static final MethodHandle staticFieldOffsetMH;
    private static final MethodHandle getObjectMH;
    private static final MethodHandle putObjectMH;
    private static final MethodHandle objectFieldOffsetMH;
    private static final MethodHandle putObjectMH_field;
    private static final MethodHandle putBooleanMH_field;
    private static final MethodHandle allocateInstanceMH;
    private static final MethodHandle getByteMH;
    private static final MethodHandle putByteMH;
    private static final MethodHandle getBooleanMH;
    private static final MethodHandle getShortMH;
    private static final MethodHandle putShortMH;
    private static final MethodHandle getCharMH;
    private static final MethodHandle putCharMH;
    private static final MethodHandle getIntMH;
    private static final MethodHandle putIntMH;
    private static final MethodHandle getLongMH;
    private static final MethodHandle putLongMH;
    private static final MethodHandle getFloatMH;
    private static final MethodHandle putFloatMH;
    private static final MethodHandle getDoubleMH;
    private static final MethodHandle putDoubleMH;
    public static final MethodHandles.Lookup lookup;

    // Method 缓存（callUnsafeMethod 用，避免每次反射查找）
    private static final Map<Method, MethodHandle> MH_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    static {
        try {
            // 0️⃣ 触发 java.lang.invoke.MethodHandles$Lookup 初始化（保证 IMPL_LOOKUP 已赋值）
            MethodHandles.lookup();

            // 1️⃣ 引导用的 Unsafe：sun.misc.Unsafe
            //    jdk.unsupported 模块无条件 opens sun.misc，反射可直接访问，无需任何 --add-opens/--add-exports
            Object bootUnsafe = getBootstrapUnsafe();

            // 2️⃣ 引导阶段的最小能力：直接用引导 Unsafe 自己的方法（不再对 JDK 内部类 setAccessible）
            MethodHandle bootStaticFieldBase = reflectiveMH(bootUnsafe, "staticFieldBase", Object.class, Field.class);
            MethodHandle bootStaticFieldOffset = reflectiveMH(bootUnsafe, "staticFieldOffset", long.class, Field.class);
            MethodHandle bootGetObject = reflectiveMH(bootUnsafe, "getObject", Object.class, Object.class, long.class);

            // 3️⃣ 获取 IMPL_LOOKUP：用 Unsafe 直接读静态字段，绕开模块访问检查
            MethodHandles.Lookup lookup_ = lookup = getIMPL_LOOKUP(bootUnsafe, bootStaticFieldBase, bootStaticFieldOffset, bootGetObject);

            // 4️⃣ 获取 Unsafe 实例：优先 jdk.internal.misc.Unsafe（同样用 Unsafe 读静态字段，兼容 Java 8–25）
            unsafeInstance = getUnsafeInstance(bootUnsafe, bootStaticFieldBase, bootStaticFieldOffset, bootGetObject);

            // 5️⃣ 初始化 defineClass
            defineClassMH = getDefineClassMH(lookup_, unsafeInstance);

            // 6️⃣ 初始化 ensureInitialized（三段回退）
            ensureInitializedMH = getEnsureInitializedMH(lookup_);

            // 7️⃣ 初始化 Module 的 implAddOpens / implAddExports（用于 Jadd）
            implAddOpensMH = lookup_.findVirtual(Module.class, "implAddOpens",
                    MethodType.methodType(void.class, String.class, Module.class));
            implAddOpensToAllUnnamedMH = lookup_.findVirtual(Module.class, "implAddOpensToAllUnnamed",
                    MethodType.methodType(void.class, String.class));
            implAddExportsMH = lookup_.findVirtual(Module.class, "implAddExports",
                    MethodType.methodType(void.class, String.class, Module.class));
            implAddExportsToAllUnnamedMH = lookup_.findVirtual(Module.class, "implAddExportsToAllUnnamed",
                    MethodType.methodType(void.class, String.class));

            // 8️⃣ Unsafe 的核心 MethodHandle（统一 bindTo(unsafeInstance)，调用只传业务参数）
            //    Java 21+ 把 getObject/putObject 改名为 getReference/putReference，需要兼容

            // Unsafe.class.getDeclaredMethods().stream().filter(m -> m.getName().equals("objectFieldOffset")).forEach(System.out::println);
            staticFieldBaseMH = unsafeMH(lookup_, "staticFieldBase", Object.class, Field.class);
            staticFieldOffsetMH = unsafeMH(lookup_, "staticFieldOffset", long.class, Field.class);
            getObjectMH = unsafeMHTry(lookup_, Object.class, new Class[]{Object.class, long.class}, "getObject", "getReference");
            putObjectMH = unsafeMHTry(lookup_, void.class, new Class[]{Object.class, long.class, Object.class}, "putObject", "putReference");
            objectFieldOffsetMH = unsafeMH(lookup_, "objectFieldOffset", long.class, Field.class);
            putObjectMH_field = putObjectMH;
            putBooleanMH_field = unsafeMH(lookup_, "putBoolean", void.class, Object.class, long.class, boolean.class);
            allocateInstanceMH = unsafeMH(lookup_, "allocateInstance", Object.class, Class.class);

            getByteMH = unsafeMH(lookup_, "getByte", byte.class, Object.class, long.class);
            putByteMH = unsafeMH(lookup_, "putByte", void.class, Object.class, long.class, byte.class);
            getBooleanMH = unsafeMH(lookup_, "getBoolean", boolean.class, Object.class, long.class);
            getShortMH = unsafeMH(lookup_, "getShort", short.class, Object.class, long.class);
            putShortMH = unsafeMH(lookup_, "putShort", void.class, Object.class, long.class, short.class);
            getCharMH = unsafeMH(lookup_, "getChar", char.class, Object.class, long.class);
            putCharMH = unsafeMH(lookup_, "putChar", void.class, Object.class, long.class, char.class);
            getIntMH = unsafeMH(lookup_, "getInt", int.class, Object.class, long.class);
            putIntMH = unsafeMH(lookup_, "putInt", void.class, Object.class, long.class, int.class);
            getLongMH = unsafeMH(lookup_, "getLong", long.class, Object.class, long.class);
            putLongMH = unsafeMH(lookup_, "putLong", void.class, Object.class, long.class, long.class);
            getFloatMH = unsafeMH(lookup_, "getFloat", float.class, Object.class, long.class);
            putFloatMH = unsafeMH(lookup_, "putFloat", void.class, Object.class, long.class, float.class);
            getDoubleMH = unsafeMH(lookup_, "getDouble", double.class, Object.class, long.class);
            putDoubleMH = unsafeMH(lookup_, "putDouble", void.class, Object.class, long.class, double.class);

        } catch (Throwable e) {
            throw new RuntimeException("Failed to initialize Unsafe utility", e);
        }
    }

    // ======================
    // ✅ 公共 API：获取 Unsafe 实例（兼容所有版本）
    // ======================
    public static Object getUnsafe() {
        return unsafeInstance;
    }

    // ======================
    // ✅ defineClass
    // ======================
    public static Class<?> defineClass(String name, byte[] b, ClassLoader loader, ProtectionDomain pd) throws Throwable {
        if (defineClassMH == null) {
            throw new UnsupportedOperationException("Unsafe.defineClass is not available on this JVM (Java " + JAVA_VERSION + ")");
        }
        return (Class<?>) defineClassMH.invokeExact(name, b, 0, b.length, loader, pd);
    }

    public static Class<?> defineClass(String name, byte[] b, ProtectionDomain pd) throws Throwable {
        ClassLoader loader = pd != null ? pd.getClassLoader() : null;
        return defineClass(name, b, loader != null ? loader : Unsafe.class.getClassLoader(), pd);
    }

    public static Class<?> defineClass(String name, byte[] b, CodeSource codeSource, ClassLoader loader) {
        Permissions permissions = new Permissions();
        permissions.add(new AllPermission());
        try {
            return defineClass(name, b, new ProtectionDomain(codeSource, permissions, loader, null));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> defineClass(String name, byte[] b, CodeSource codeSource) {
        return defineClass(name, b, codeSource, Thread.currentThread().getContextClassLoader());
    }

    public static Class<?> defineClass(String name, byte[] b) {
        return defineClass(name, b, new CodeSource(null, (java.security.cert.Certificate[]) null));
    }

    // ======================
    // ✅ makeEnum
    // ======================
    @SuppressWarnings("unchecked")
    public static <T> T makeEnum(Class<T> cl, String name, int i, List<Class<?>> ctorTypes, List<Object> ctorParams) {
        try {
            ensureInitialized(cl);
            List<Class<?>> ctor = new ArrayList<>(ctorTypes.size() + 2);
            ctor.add(String.class);
            ctor.add(int.class);
            ctor.addAll(ctorTypes);
            MethodHandle constructor = MethodHandles.lookup().findConstructor(
                    cl, MethodType.methodType(void.class, ctor)
            );
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

    public static <T> MethodHandle makeEnum(Class<T> cl, Class<?>... ctorTypes) {
        try {
            ensureInitialized(cl);
            List<Class<?>> ctor = new ArrayList<>(ctorTypes.length + 2);
            ctor.add(String.class);
            ctor.add(int.class);
            ctor.addAll(Arrays.asList(ctorTypes));
            return MethodHandles.lookup().findConstructor(
                    cl, MethodType.methodType(void.class, ctor)
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    // ======================
    // ✅ getFieldAddress / setFinal
    // ======================
    public static long getFieldAddress(Field field) {
        try {
            return (long) objectFieldOffsetMH.invokeExact(field);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFinal(Field field, Object target, Object value) {
        setFinal(getFieldAddress(field), target, value);
    }

    public static void setFinal(long field, Object target, Object value) {
        try {
            putObjectMH_field.invokeExact(target, field, value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFinalBool(Field field, boolean value, Object target) {
        try {
            putBooleanMH_field.invokeExact(target, getFieldAddress(field), value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    // ======================
    // ✅ 基础内存读写（供 UnsafeClone 等使用，全部走 MethodHandle，不依赖 --add-opens）
    // ======================
    public static Object allocateInstance(Class<?> c) throws InstantiationException {
        try {
            return allocateInstanceMH.invokeExact(c);
        } catch (Throwable e) {
            throw new InstantiationException(e.getMessage());
        }
    }

    public static Object staticFieldBase(Field field) {
        try { return staticFieldBaseMH.invokeExact(field); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static long staticFieldOffset(Field field) {
        try { return (long) staticFieldOffsetMH.invokeExact(field); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static byte getByte(Object obj, long offset) {
        try { return (byte) getByteMH.invokeExact(obj, offset); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static void putByte(Object obj, long offset, byte v) {
        try { putByteMH.invokeExact(obj, offset, v); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static boolean getBoolean(Object obj, long offset) {
        try { return (boolean) getBooleanMH.invokeExact(obj, offset); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static short getShort(Object obj, long offset) {
        try { return (short) getShortMH.invokeExact(obj, offset); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static void putShort(Object obj, long offset, short v) {
        try { putShortMH.invokeExact(obj, offset, v); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static char getChar(Object obj, long offset) {
        try { return (char) getCharMH.invokeExact(obj, offset); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static void putChar(Object obj, long offset, char v) {
        try { putCharMH.invokeExact(obj, offset, v); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static int getInt(Object obj, long offset) {
        try { return (int) getIntMH.invokeExact(obj, offset); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static void putInt(Object obj, long offset, int v) {
        try { putIntMH.invokeExact(obj, offset, v); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static long getLong(Object obj, long offset) {
        try { return (long) getLongMH.invokeExact(obj, offset); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static void putLong(Object obj, long offset, long v) {
        try { putLongMH.invokeExact(obj, offset, v); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static float getFloat(Object obj, long offset) {
        try { return (float) getFloatMH.invokeExact(obj, offset); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static void putFloat(Object obj, long offset, float v) {
        try { putFloatMH.invokeExact(obj, offset, v); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static double getDouble(Object obj, long offset) {
        try { return (double) getDoubleMH.invokeExact(obj, offset); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static void putDouble(Object obj, long offset, double v) {
        try { putDoubleMH.invokeExact(obj, offset, v); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static Object getObject(Object obj, long offset) {
        try { return getObjectMH.invokeExact(obj, offset); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public static void putObject(Object obj, long offset, Object v) {
        try { putObjectMH.invokeExact(obj, offset, v); } catch (Throwable e) { throw new RuntimeException(e); }
    }

    private static boolean isReturnTypeCompatible(Class<?> expected, Class<?> actual) {
        return expected == actual ||
                (expected == byte.class && actual == Byte.class) ||
                (expected == short.class && actual == Short.class) ||
                (expected == char.class && actual == Character.class) ||
                (expected == int.class && actual == Integer.class) ||
                (expected == long.class && actual == Long.class) ||
                (expected == float.class && actual == Float.class) ||
                (expected == double.class && actual == Double.class) ||
                (expected == boolean.class && actual == Boolean.class);
    }

    private static boolean isParamCompatible(Class<?> expected, Class<?> actual) {
        return expected == actual ||
                (expected.isPrimitive() && expected == getPrimitive(actual)) ||
                (expected.isInstance(actual)); // 兼容 Object -> byte 等自动装箱
    }

    private static Class<?> getPrimitive(Class<?> clazz) {
        if (clazz == Byte.class) return byte.class;
        if (clazz == Short.class) return short.class;
        if (clazz == Character.class) return char.class;
        if (clazz == Integer.class) return int.class;
        if (clazz == Long.class) return long.class;
        if (clazz == Float.class) return float.class;
        if (clazz == Double.class) return double.class;
        if (clazz == Boolean.class) return boolean.class;
        return clazz;
    }
    private static Method findUnsafeMethod(String name, Class<?> returnType, Class<?>... paramTypes) {
        // 尝试：jdk.internal.misc.Unsafe
        Class<?> unsafeClass = unsafeInstance.getClass();
        for (Method m : unsafeClass.getDeclaredMethods()) {
            if (m.getName().equals(name) &&
                    m.getReturnType() == returnType &&
                    Arrays.equals(m.getParameterTypes(), paramTypes)) {
                return m;
            }
        }

        // 备用方案：遍历所有方法 + 类型宽泛匹配（处理泛型如 Object vs byte）
        for (Method m : unsafeClass.getDeclaredMethods()) {
            if (!m.getName().equals(name)) continue;
            if (!isReturnTypeCompatible(m.getReturnType(), returnType)) continue;

            Class<?>[] params = m.getParameterTypes();
            if (params.length != paramTypes.length) continue;

            boolean match = true;
            for (int i = 0; i < params.length; i++) {
                if (!isParamCompatible(params[i], paramTypes[i])) {
                    match = false;
                    break;
                }
            }
            if (match) return m;
        }

        throw new IllegalStateException("Unsafe method not found: " + name + "(" + Arrays.toString(paramTypes) + ") in " + unsafeClass.getName());
    }

    private static Object callUnsafeMethod(String methodName, Class<?> returnType, Object... args) throws Throwable {
        // 构建 MethodType（参数类型）
        Class<?>[] paramTypes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);

        // 动态获取 Method（避免硬编码，因为方法签名可能因 JDK 而异）
        Method method = findUnsafeMethod(methodName, returnType, paramTypes);
        // 用 IMPL_LOOKUP 生成 MethodHandle 调用，不做 setAccessible（避免模块访问检查）
        return MH_CACHE.computeIfAbsent(method, m -> {
            try {
                return lookup.unreflect(m).bindTo(unsafeInstance);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }).invokeWithArguments(args);
    }

    // ======================
    // ✅ getStatic
    // ======================
    @SuppressWarnings("unchecked")
    public static <T> T getStatic(Class<?> cl, String name) {
        try {
            ensureInitialized(cl);
            Field field = cl.getDeclaredField(name);
            try {
                field.setAccessible(true);
                return (T) field.get(null);
            } catch (Throwable ignored) {
                // 模块不可访问时（未加 --add-opens）退回 Unsafe 读静态字段
                return (T) getObject(staticFieldBase(field), staticFieldOffset(field));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    // ======================
    // ✅ Jadd（模块开放/导出）
    // ======================
    public static void Jadd(String jadds) {
        List<String> opens = new ArrayList<>();
        List<String> exports = new ArrayList<>();

        String runClass = "";
        for (String line : jadds.split("\\r?\\n")) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (!runClass.isEmpty()) {
                // ignore for now
            } else if (!line.startsWith("-")) {
                runClass = line;
            } else if (line.startsWith("--add-opens")) {
                opens.add(extractValue(line, "--add-opens"));
            } else if (line.startsWith("--add-exports")) {
                exports.add(extractValue(line, "--add-exports"));
            }
        }

        ModuleLayer.boot().modules().forEach(module -> {
            try {
                for (String open : opens) {
                    applyModuleAction(open, implAddOpensMH, implAddOpensToAllUnnamedMH);
                }
                for (String exp : exports) {
                    applyModuleAction(exp, implAddExportsMH, implAddExportsToAllUnnamedMH);
                }
            } catch (Throwable e) {
                throw new RuntimeException("Failed to apply module opens/exports", e);
            }
        });
    }

    // ======================
    // ✅ 辅助：ensureInitialized
    // ======================
    public static void ensureInitialized(Class<?> clazz) {
        try {
            ensureInitializedMH.invokeExact(clazz);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    // ======================
    // 🔧 私有工具方法（兼容所有版本）
    // ======================

    /**
     * 引导用的 Unsafe：
     * - Java 8~20: 优先 sun.misc.Unsafe（jdk.unsupported 模块无条件 opens sun.misc）
     * - Java 21+:  sun.misc.Unsafe 已受限，直接用 jdk.internal.misc.Unsafe
     */
    private static Object getBootstrapUnsafe() throws Exception {
        // Java 21+：跳过 sun.misc.Unsafe，直接用 jdk.internal.misc.Unsafe
        if (!JAVA_21_OR_LATER) {
            try {
                Class<?> c = Class.forName("sun.misc.Unsafe");
                Field f = c.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                Object o = f.get(null);
                if (o != null) return o;
            } catch (Throwable ignored) {
            }
        }
        // 兜底/Java 21+：直接拿 jdk.internal.misc.Unsafe
        try {
            Class<?> c = Class.forName("jdk.internal.misc.Unsafe");
            Field f = c.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Object o = f.get(null);
            if (o != null) return o;
        } catch (Throwable ignored) {
        }
        // 最后尝试 sun.misc.Unsafe（Java 21+ 仍然可能通过 jdk.unsupported 访问）
        if (JAVA_21_OR_LATER) {
            try {
                Class<?> c = Class.forName("sun.misc.Unsafe");
                Field f = c.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                Object o = f.get(null);
                if (o != null) return o;
            } catch (Throwable ignored) {
            }
        }
        throw new IllegalStateException("Unable to obtain Unsafe instance on Java " + JAVA_VERSION);
    }

    private static MethodHandle getEnsureInitializedMH(MethodHandles.Lookup lookup_) {
        // ① Oracle JDK: Class.ensureInitialized()
        try {
            return lookup_.findVirtual(Class.class, "ensureInitialized", MethodType.methodType(void.class));
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException e) {}
        // ② Java 15+: Lookup.ensureInitialized(Class)
        try {
            return lookup_.findVirtual(MethodHandles.Lookup.class, "ensureInitialized",
                    MethodType.methodType(void.class, Class.class)).bindTo(lookup_);
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException e) {}
        // ③ 所有 JDK: Unsafe.ensureClassInitialized(Class)
        try {
            return unsafeMH(lookup_, "ensureClassInitialized", void.class, Class.class);
        } catch (Exception e) {
            throw new RuntimeException("No ensureInitialized available on this JVM", e);
        }
    }

    /** 反射拿到引导 Unsafe 自身方法的 MethodHandle */
    private static MethodHandle reflectiveMH(Object target, String name, Class<?> ret, Class<?>... params) throws Exception {
        Method m = target.getClass().getDeclaredMethod(name, params);
        try {
            m.setAccessible(true);
        } catch (Throwable ignored) {
        }
        return MethodHandles.lookup().unreflect(m).bindTo(target);
    }

    /** 基于最终的 unsafeInstance 生成 MethodHandle（已 bindTo，调用只传业务参数） */
    private static MethodHandle unsafeMH(MethodHandles.Lookup lookup_, String name, Class<?> ret, Class<?>... params) throws Exception {
        return lookup_.findVirtual(unsafeInstance.getClass(), name, MethodType.methodType(ret, params)).bindTo(unsafeInstance);
    }

    /** 尝试多个方法名，返回第一个找到的 MethodHandle（用于 getObject→getReference 等 JDK 版本差异） */
    private static MethodHandle unsafeMHTry(MethodHandles.Lookup lookup_, Class<?> ret, Class<?>[] params, String... names) throws Exception {
        for (String name : names) {
            try {
                return lookup_.findVirtual(unsafeInstance.getClass(), name, MethodType.methodType(ret, params)).bindTo(unsafeInstance);
            } catch (NoSuchMethodException | IllegalAccessException ignored) {
            }
        }
        throw new NoSuchMethodException("None of the method names found: " + Arrays.toString(names));
    }

    /** 获取 Unsafe 实例：Java 21+ 优先 jdk.internal.misc.Unsafe，旧版本优先 sun.misc.Unsafe */
    private static Object getUnsafeInstance(Object bootUnsafe, MethodHandle sfb, MethodHandle sfo, MethodHandle getObj) {
        // Java 21+：优先 jdk.internal.misc.Unsafe
        if (JAVA_21_OR_LATER) {
            try {
                Class<?> c = Class.forName("jdk.internal.misc.Unsafe");
                Field f = c.getDeclaredField("theUnsafe");
                Object base = sfb.invoke(f);
                long offset = (long) sfo.invoke(f);
                Object o = getObj.invoke(base, offset);
                if (o != null) return o;
            } catch (Throwable ignored) {
            }
        }
        // 旧版本优先 / Java 21+ 兜底：sun.misc.Unsafe
        if (!JAVA_21_OR_LATER) {
            try {
                Class<?> c = Class.forName("sun.misc.Unsafe");
                Field f = c.getDeclaredField("theUnsafe");
                Object base = sfb.invoke(f);
                long offset = (long) sfo.invoke(f);
                Object o = getObj.invoke(base, offset);
                if (o != null) return o;
            } catch (Throwable ignored) {
            }
        }
        // Java 21+ 最后尝试 sun.misc.Unsafe
        if (JAVA_21_OR_LATER) {
            try {
                Class<?> c = Class.forName("sun.misc.Unsafe");
                Field f = c.getDeclaredField("theUnsafe");
                Object base = sfb.invoke(f);
                long offset = (long) sfo.invoke(f);
                Object o = getObj.invoke(base, offset);
                if (o != null) return o;
            } catch (Throwable ignored) {
            }
        }
        // 最终兜底：直接用引导实例
        return bootUnsafe;
    }

    /** 读取 MethodHandles.Lookup.IMPL_LOOKUP（不用 setAccessible，java.base 未 open 也能读） */
    private static MethodHandles.Lookup getIMPL_LOOKUP(Object bootUnsafe, MethodHandle sfb, MethodHandle sfo, MethodHandle getObj) throws Throwable {
        try {
            Method eci = bootUnsafe.getClass().getDeclaredMethod("ensureClassInitialized", Class.class);
            try {
                eci.setAccessible(true);
            } catch (Throwable ignored) {
            }
            eci.invoke(bootUnsafe, MethodHandles.Lookup.class);
        } catch (Throwable ignored) {
        }
        MethodHandles.lookup();
        Field f = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
        return (MethodHandles.Lookup) getObj.invoke(sfb.invoke(f), (long) sfo.invoke(f));
    }

    private static MethodHandle getDefineClassMH(MethodHandles.Lookup lookup_, Object unsafe) {
        // Java 8: sun.misc.Unsafe.defineClass(...)
        // Java 9+: jdk.internal.misc.Unsafe.defineClass(...)
        try {
            Method m = unsafe.getClass().getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class,
                    ClassLoader.class, ProtectionDomain.class);
            return lookup_.unreflect(m).bindTo(unsafe);
        } catch (Throwable e) {
            return null; // 当前 JDK 不支持，真正调用时再报错
        }
    }

    private static String extractValue(String arg, String prefix) {
        String trimmed = arg.trim();
        String withoutPrefix = trimmed.substring(prefix.length()).trim();
        if (withoutPrefix.startsWith("=")) {
            return withoutPrefix.substring(1).trim();
        } else {
            return withoutPrefix;
        }
    }

    private static void applyModuleAction(String spec, MethodHandle normal, MethodHandle toAllUnnamed) throws Throwable {
        String[] parts = spec.split("=");
        if (parts.length != 2) return;

        String[] moduleAndPackage = parts[0].split("/");
        if (moduleAndPackage.length != 2) return;

        String moduleName = moduleAndPackage[0];
        String packageName = moduleAndPackage[1];
        String target = parts[1].trim();

        ModuleLayer.boot().findModule(moduleName).ifPresent(module -> {
            try {
                if ("ALL-UNNAMED".equals(target)) {
                    toAllUnnamed.invokeExact(module, packageName);
                } else {
                    ModuleLayer.boot().findModule(target).ifPresent(exportTo -> {
                        try {
                            normal.invokeExact(module, packageName, exportTo);
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
//    public static class Unsafe21 {
//        public static final sun.misc.Unsafe unsafe;
//        static {
//
//            try {
//                Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
//                theUnsafe.setAccessible(true);
//                unsafe = (sun.misc.Unsafe) theUnsafe.get(null);
//
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
    //public static final MethodHandles.Lookup lookup;
    //public static final MethodHandle defineClass;
    //public static final sun.misc.Unsafe unsafe = Unsafe21.unsafe;


    //public void ensureInit(Class<?> clazz) {
    //    try {
    //        // 使用 MethodHandles.Lookup 来确保类初始化
    //        MethodHandles.lookup().ensureInitialized(clazz);
    //    } catch (IllegalAccessException e) {
    //        // 处理异常，例如记录日志或抛出运行时异常
    //        throw new RuntimeException("Failed to ensure class is initialized", e);
    //    }
    //}


//    static {
//        try {
//            Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
//            theUnsafe.setAccessible(true);
//            MethodHandles.lookup().ensureInitialized(MethodHandles.Lookup.class);//unsafe.ensureClassInitialized(MethodHandles.Lookup.class);
//            Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
//            Object base = unsafe.staticFieldBase(field);
//            long offset = unsafe.staticFieldOffset(field);
//            //field.setAccessible(true);
//            lookup = (MethodHandles.Lookup) //field.get(null);//
//             unsafe.getObject(base, offset);
//            MethodHandle mh;
//            try {
//                Method sunMisc = Class.forName("sun.misc.Unsafe").getMethod("defineClass", String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class);
//                mh = lookup.unreflect(sunMisc).bindTo(Unsafe21.unsafe);
//            } catch (Exception e) {
//                Class<?> jdkInternalUnsafe = Class.forName("jdk.internal.misc.Unsafe");
//                Field internalUnsafeField = jdkInternalUnsafe.getDeclaredField("theUnsafe");
//                //internalUnsafeField.setAccessible(true);
//                Object internalUnsafe = //internalUnsafeField.get(null);//
//                 unsafe.getObject(unsafe.staticFieldBase(internalUnsafeField), unsafe.staticFieldOffset(internalUnsafeField));
//                Method internalDefineClass = jdkInternalUnsafe.getMethod("defineClass", String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class);
//                mh = lookup.unreflect(internalDefineClass).bindTo(internalUnsafe);
//            }
//            defineClass = Objects.requireNonNull(mh);
//            //Jadd("--add-exports java.base/jdk.internal.misc=ALL-UNNAMED");
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * @param cl         注入的类型
//     * @param ctorTypes  注入的数据类型（ImmutableList.of(class1.class,class2.class,...)）
//     * @param i          注入的是第几个元素
//     * @param ctorParams 数据
//     */
//    @SuppressWarnings("unchecked")
//    public static <T> T makeEnum(Class<T> cl, String name, int i, List<Class<?>> ctorTypes, List<Object> ctorParams) {
//        try {
//            lookup.ensureInitialized(cl);
//            List<Class<?>> ctor = new ArrayList<>(ctorTypes.size() + 2);
//            ctor.add(String.class);//名字
//            ctor.add(int.class);//第几个元素
//            ctor.addAll(ctorTypes);//注入的数据类型（ImmutableList.of(class1.class,class2.class,...)
//            MethodHandle constructor = lookup.findConstructor(cl, MethodType.methodType(void.class, ctor));//这tm注入是枚举元素
//            List<Object> param = new ArrayList<>(ctorParams.size() + 2);
//            param.add(name);
//            param.add(i);
//            param.addAll(ctorParams);
//            return (T) constructor.invokeWithArguments(param);
//        } catch (Throwable e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    /**
//     * @param cl         注入的类型
//     * @param ctorTypes  注入的数据类型（ImmutableList.of(class1.class,class2.class,...)）
//     * @param i          注入的是第几个元素
//     * @param ctorParams 数据
//     */
//    public static <T> MethodHandle makeEnum(Class<T> cl, Class<?>... ctorTypes) {
//        try {
//            lookup.ensureInitialized(cl);
//            List<Class<?>> ctor = new ArrayList<>(ctorTypes.length + 2);
//            ctor.add(String.class);//名字
//            ctor.add(int.class);//第几个元素
//            ctor.addAll(Arrays.asList(ctorTypes));//注入的数据类型（ImmutableList.of(class1.class,class2.class,...)
//            MethodHandle constructor = lookup.findConstructor(cl, MethodType.methodType(void.class, ctor));//这tm注入是枚举元素
//            return constructor;
//        } catch (Throwable e) {
//            throw new RuntimeException(e);
//        }
//    }
//    public static <T> T getStatic(Class<?> cl, String name) {
//        try {
//            MethodHandles.lookup().ensureInitialized(cl);
//            Field field = cl.getDeclaredField(name);
//            field.setAccessible(true); // 替代 Unsafe 的权限绕过
//            return (T) field.get(null); // 静态字段传 null
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    public static Map<String, Object> publicJava(Class<?> cl) {
//        Map<String, Object> ret = new HashMap<>();
//        for (Method method1 : cl.getDeclaredMethods()) {
//            method1.setAccessible(true);
//            ret.put(method1.toString(), method1);
//        }
//        for (Field declaredField : cl.getDeclaredFields()) {
//            declaredField.setAccessible(true);
//            ret.put(declaredField.toString(), declaredField);
//        }
//        return ret;
//    }
//
//    @SuppressWarnings("unchecked")
//    public static Object publicJava(Class<?> cl, String name, Object val, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//        Method method = cl.getDeclaredMethod(name);
//        method.setAccessible(true);
//        return method.invoke(val, args);
//    }
//
//    @SuppressWarnings("unchecked")
//    public static Object _MpublicJava(Class<?> cl, String name, Object val, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//
//        for (Method declaredMethod : cl.getDeclaredMethods()) {
//            if (declaredMethod.getName().equals(name)) {
//                try {
//                    declaredMethod.setAccessible(true);
//                } catch (Throwable e) {
//                }
//                return declaredMethod.invoke(val, args);
//            }
//        }
//        Method method = cl.getDeclaredMethod(name);
//        method.setAccessible(true);
//        return method.invoke(val, args);
//    }
//
//    @SuppressWarnings("unchecked")
//    public static Field publicJava(Class<?> cl, String name) throws NoSuchFieldException {
//        Field field = cl.getDeclaredField(name);
//        field.setAccessible(true);
//        return field;
//    }
//
//    @SuppressWarnings("unchecked")
//    public static Object _publicJava(Class<?> cl, String name, Object var) throws NoSuchFieldException, IllegalAccessException {
//        Field field = cl.getDeclaredField(name);
//        field.setAccessible(true);
//        return field.get(var);
//    }
//    @SuppressWarnings("unchecked")
//    public static boolean _publicJavaBool(Class<?> cl, String name, Object var) throws NoSuchFieldException, IllegalAccessException {
//        Field field = cl.getDeclaredField(name);
//        field.setAccessible(true);
//        return field.getBoolean(var);
//    }
//
//    @SuppressWarnings("unchecked")
//    public static <T> void _publicJava(Class<T> cl, String name, Object var, Object setvar) throws NoSuchFieldException, IllegalAccessException {
//        Field field = cl.getDeclaredField(name);
//        field.setAccessible(true);
//        field.set(var, setvar);
//    }
//
//    @SuppressWarnings("unchecked")
//    public static void setfinal(Field field, Object in, Object put) {
//        unsafe.putObject(put, unsafe.objectFieldOffset(field), in);
//    }
//    @SuppressWarnings("unchecked")
//    public static void setfinalBool(Field field, boolean in, Object put) {
//        unsafe.putBoolean(put, unsafe.objectFieldOffset(field), in);
//    }
//
//    //瞎改的能用就行
//    @SuppressWarnings("all")
//    public static void Jadd(String jadds) {
//
//        String[] moloader_args;
//        String runClass = "";
//        String[] lib = null;
//        try {
//            String[] lib2 = new String[]{};
//            //InputStream runtexeIn = new FileInputStream(new File(tBin,"win_args.txt"));
//            StringBuilder Add_args = new StringBuilder();
//            List<String> opens = new ArrayList<>();
//            List<String> exports = new ArrayList<>();
//            List<String> modules = new ArrayList<>();
//            //exports.add("cpw.mods.bootstraplauncher/cpw.mods.bootstraplauncher=ALL-UNNAMED");
//
//            for (String s : jadds.split("(\n|\r\n)+")) {
//                if (!runClass.equals("")) {
//                    Add_args.append(s).append(" ");
//                    continue;
//                }
//                if (!s.startsWith("-")) {
//                    runClass = s;
//                    continue;
//                } else if (s.startsWith("-p ")) {
//                    lib = s.replace("-p ", "").split(";", -1);
//                } else if (s.startsWith("-DlegacyClassPath=")) {
//                    lib2 = s.replace("-DlegacyClassPath=", "").split(";", -1);
//                } else if (s.startsWith("-D")) {
//                    String[] s1 = s.substring(2).split("=", 2);
//                    System.setProperty(s1[0], s1[1]);
//                } else if (s.startsWith("--add-opens ") || s.startsWith("--add-opens=")) {
//                    opens.add(s.substring("--add-opens ".length()).trim());
//                } else if (s.startsWith("--add-exports ") || s.startsWith("--add-exports=")) {
//                    exports.add(s.substring("--add-exports ".length()).trim());
//                }
//            }
//            MethodHandles.Lookup IMPL_LOOKUP = Unsafe.lookup;
//
//            try {
//                MethodHandle Opens_implAddOpensMH = IMPL_LOOKUP.findVirtual(Module.class, "implAddOpens", MethodType.methodType(void.class, String.class, Module.class));
//                MethodHandle Opens_implAddOpensToAllUnnamedMH = IMPL_LOOKUP.findVirtual(Module.class, "implAddOpensToAllUnnamed", MethodType.methodType(void.class, String.class));
//                opens.forEach(extra -> {
//                    //ParserData data = parseModuleExtra(extra);
//                    String[] all = extra.split("=", 2);
//                    if (all.length < 2) {
//                        return;
//                    }
//
//                    String[] source = all[0].split("/", 2);
//                    if (source.length < 2) {
//                        return;
//                    }
//                    final String module = source[0];
//                    final String packages = source[1];
//                    final String target = all[1];
//                    ModuleLayer.boot().findModule(module).ifPresent(m -> {
//                        try {
//                            if ("ALL-UNNAMED".equals(target)) {
//                                Opens_implAddOpensToAllUnnamedMH.invokeWithArguments(m, packages);
//                            } else {
//                                ModuleLayer.boot().findModule(target).ifPresent(tm -> {
//                                    try {
//                                        Opens_implAddOpensMH.invokeWithArguments(m, packages, tm);
//                                    } catch (Throwable t) {
//                                        throw new RuntimeException(t);
//                                    }
//                                });
//                            }
//                        } catch (Throwable t) {
//                            throw new RuntimeException(t);
//                        }
//                    });
//
//                });
//                MethodHandle Exports_implAddExportsMH = IMPL_LOOKUP.findVirtual(Module.class, "implAddExports", MethodType.methodType(void.class, String.class, Module.class));
//                MethodHandle Exports_implAddExportsToAllUnnamedMH = IMPL_LOOKUP.findVirtual(Module.class, "implAddExportsToAllUnnamed", MethodType.methodType(void.class, String.class));
//
//                exports.forEach(extra -> {
//                    //ParserData data = parseModuleExtra(extra);
//                    String[] all = extra.split("=", 2);
//                    if (all.length < 2) {
//                        return;
//                    }
//
//                    String[] source = all[0].split("/", 2);
//                    if (source.length < 2) {
//                        return;
//                    }
//                    final String module = source[0];
//                    final String packages = source[1];
//                    final String target = all[1];
//                    ModuleLayer.boot().findModule(module).ifPresent(m -> {
//                        try {
//                            if ("ALL-UNNAMED".equals(target)) {
//                                Exports_implAddExportsToAllUnnamedMH.invokeWithArguments(m, packages);
//                            } else {
//                                ModuleLayer.boot().findModule(target).ifPresent(tm -> {
//                                    try {
//                                        Exports_implAddExportsMH.invokeWithArguments(m, packages, tm);
//                                    } catch (Throwable t) {
//                                        throw new RuntimeException(t);
//                                    }
//                                });
//                            }
//                        } catch (Throwable t) {
//                            throw new RuntimeException(t);
//                        }
//                    });
//
//                });
//
//
//            } catch (NoSuchMethodException | IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//
//        } finally {
//
//        }
//    }
//
////    public static long addressOf(Object o) {
////
////        Object[] array = new Object[]{o};
////
////        long baseOffset = unsafe.arrayBaseOffset(Object[].class);
////        int addressSize = unsafe.addressSize();
////        long objectAddress;
////        switch (addressSize) {
////            case 4:
////                objectAddress = unsafe.getInt(array, baseOffset);
////                break;
////            case 8:
////                objectAddress = unsafe.getLong(array, baseOffset);
////                break;
////            default:
////                throw new RuntimeException("你的内存一定大于9223372036854775807bit了吧，或者你要瞎搞jvm好吗这玩意他不行搞 或者你是一个非常nb的人你会印cpu？？？ 我整个紧适用x86或amd64也可能是arm64但是他不适用与未来啊老弟     因为描述描述内存他有这么大啊: " + addressSize);
////        }
////        return (objectAddress);
////    }
//
//    public static Class defineClass(String name, byte[] b, ProtectionDomain pd){
//        try {
//            return (Class) Unsafe.defineClass.invoke(name, b, 0, b.length, pd);
//        } catch (Throwable e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static Class defineClass(String name, byte[] b, CodeSource codeSource, ClassLoader loader){
//        Permissions permissions = new Permissions();
//        permissions.add(new AllPermission());
//        return defineClass(name, b, new ProtectionDomain(codeSource, permissions, loader, null));
//    }
//
//    public static Class defineClass(String name, byte[] b, CodeSource codeSource) {
//        return defineClass(name, b, codeSource, Thread.currentThread().getContextClassLoader());
//    }
//
//    public static Class defineClass(String name, byte[] b) {
//        return  defineClass(name, b, new CodeSource(null, (java.security.cert.Certificate[])null));
//    }
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

    //public static long getFieldAddress(Field field) {
    //    return unsafe.objectFieldOffset(field);
    //}

    public static long getTypeFieldAddress(Class<?> c, Class<?> type) {
        for (Field declaredField : c.getDeclaredFields()) {
            if (declaredField.getType().isAssignableFrom(type)) {
                return getFieldAddress(declaredField);
            }
        }
        throw new RuntimeException("没有找到字段 " + c + " " + type);
    }
    public static Field getTypeField(Class<?> c, Class<?> type) {
        for (Field declaredField : c.getDeclaredFields()) {
            if (declaredField.getType().isAssignableFrom(type)) {
                return declaredField;
            }
        }
        throw new RuntimeException("没有找到字段 " + c + " " + type);
    }

    private static CallSite metafactory(MethodHandles.Lookup loader, Method impl, MethodHandle call, MethodType methodType) throws LambdaConversionException {
        MethodType type = call.type();
        if(methodType == null) methodType = MethodType.methodType(impl.getDeclaringClass());

        return LambdaMetafactory.metafactory(
                loader,
                impl.getName(),
                methodType,
                MethodType.methodType(impl.getReturnType(), impl.getParameterTypes()),
                call,
                type);
    }
}
