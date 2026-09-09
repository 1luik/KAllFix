package n1luik.K_multi_threading.core.util;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.*;

public class UnsafeClone<S, T> {
    public final Class<T> type;
    public final long[] ids;
    public final List<Field> fields;

    private static final MethodHandle mhGetByte;
    private static final MethodHandle mhPutByte;
    private static final MethodHandle mhGetBoolean;
    private static final MethodHandle mhPutBoolean;
    private static final MethodHandle mhGetShort;
    private static final MethodHandle mhPutShort;
    private static final MethodHandle mhGetChar;
    private static final MethodHandle mhPutChar;
    private static final MethodHandle mhGetInt;
    private static final MethodHandle mhPutInt;
    private static final MethodHandle mhGetLong;
    private static final MethodHandle mhPutLong;
    private static final MethodHandle mhGetFloat;
    private static final MethodHandle mhPutFloat;
    private static final MethodHandle mhGetDouble;
    private static final MethodHandle mhPutDouble;
    private static final MethodHandle mhGetObject;
    private static final MethodHandle mhPutObject;
    private static final MethodHandle mhAllocateInstance;

    static {
        try {
            MethodHandles.Lookup L = Unsafe.lookup;
            Class<?> U = Unsafe.getUnsafe().getClass();

            mhGetByte       = L.findVirtual(U, "getByte",    MethodType.methodType(byte.class,   Object.class, long.class)).bindTo(Unsafe.getUnsafe());
            mhPutByte       = L.findVirtual(U, "putByte",    MethodType.methodType(void.class,   Object.class, long.class, byte.class)).bindTo(Unsafe.getUnsafe());
            mhGetBoolean    = L.findVirtual(U, "getBoolean", MethodType.methodType(boolean.class, Object.class, long.class)).bindTo(Unsafe.getUnsafe());
            mhPutBoolean    = L.findVirtual(U, "putBoolean", MethodType.methodType(void.class,   Object.class, long.class, boolean.class)).bindTo(Unsafe.getUnsafe());
            mhGetShort      = L.findVirtual(U, "getShort",   MethodType.methodType(short.class,  Object.class, long.class)).bindTo(Unsafe.getUnsafe());
            mhPutShort      = L.findVirtual(U, "putShort",   MethodType.methodType(void.class,   Object.class, long.class, short.class)).bindTo(Unsafe.getUnsafe());
            mhGetChar       = L.findVirtual(U, "getChar",    MethodType.methodType(char.class,   Object.class, long.class)).bindTo(Unsafe.getUnsafe());
            mhPutChar       = L.findVirtual(U, "putChar",    MethodType.methodType(void.class,   Object.class, long.class, char.class)).bindTo(Unsafe.getUnsafe());
            mhGetInt        = L.findVirtual(U, "getInt",     MethodType.methodType(int.class,    Object.class, long.class)).bindTo(Unsafe.getUnsafe());
            mhPutInt        = L.findVirtual(U, "putInt",     MethodType.methodType(void.class,   Object.class, long.class, int.class)).bindTo(Unsafe.getUnsafe());
            mhGetLong       = L.findVirtual(U, "getLong",    MethodType.methodType(long.class,   Object.class, long.class)).bindTo(Unsafe.getUnsafe());
            mhPutLong       = L.findVirtual(U, "putLong",    MethodType.methodType(void.class,   Object.class, long.class, long.class)).bindTo(Unsafe.getUnsafe());
            mhGetFloat      = L.findVirtual(U, "getFloat",   MethodType.methodType(float.class,  Object.class, long.class)).bindTo(Unsafe.getUnsafe());
            mhPutFloat      = L.findVirtual(U, "putFloat",   MethodType.methodType(void.class,   Object.class, long.class, float.class)).bindTo(Unsafe.getUnsafe());
            mhGetDouble     = L.findVirtual(U, "getDouble",  MethodType.methodType(double.class, Object.class, long.class)).bindTo(Unsafe.getUnsafe());
            mhPutDouble     = L.findVirtual(U, "putDouble",  MethodType.methodType(void.class,   Object.class, long.class, double.class)).bindTo(Unsafe.getUnsafe());
            mhGetObject     = findRefMH(L, U, "getObject", "getReference");
            mhPutObject     = findRefMHPut(L, U, "putObject", "putReference");
            mhAllocateInstance = L.findVirtual(U, "allocateInstance", MethodType.methodType(Object.class, Class.class)).bindTo(Unsafe.getUnsafe());

        } catch (Throwable e) {
            throw new RuntimeException("Failed to initialize UnsafeClone method handles", e);
        }
    }

    public UnsafeClone(Class<S> src, Class<T> clazz) {
        Class<? super S> superclass = src;
        fields = new ArrayList<>();
        while (superclass != null && superclass != Object.class) {
            List<Field> tfields1 = Arrays.stream(superclass.getDeclaredFields())
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .toList();
            List<String> tfieldNames = tfields1.stream().map(Field::getName).toList();
            fields.addAll(tfields1);
            Arrays.stream(superclass.getFields())
                    .filter(field -> !Modifier.isStatic(field.getModifiers()) && !tfieldNames.contains(field.getName()))
                    .forEach(fields::add);
            superclass = superclass.getSuperclass();
        }
        ids = new long[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            ids[i] = Unsafe.getFieldAddress(fields.get(i));
        }
        this.type = clazz;
    }

    @SuppressWarnings("unchecked")
    public T clone(S obj) {
        T t = (T) invoke(mhAllocateInstance, type);
        for (int i = 0; i < ids.length; i++) {
            long id = ids[i];
            Class<?> ft = fields.get(i).getType();

            if      (ft == byte.class)    { invoke(mhPutByte, t, id, invoke(mhGetByte, obj, id)); }
            else if (ft == boolean.class) { invoke(mhPutBoolean, t, id, invoke(mhGetBoolean, obj, id)); }
            else if (ft == short.class)   { invoke(mhPutShort, t, id, invoke(mhGetShort, obj, id)); }
            else if (ft == char.class)    { invoke(mhPutChar, t, id, invoke(mhGetChar, obj, id)); }
            else if (ft == int.class)     { invoke(mhPutInt, t, id, invoke(mhGetInt, obj, id)); }
            else if (ft == long.class)    { invoke(mhPutLong, t, id, invoke(mhGetLong, obj, id)); }
            else if (ft == float.class)   { invoke(mhPutFloat, t, id, invoke(mhGetFloat, obj, id)); }
            else if (ft == double.class)  { invoke(mhPutDouble, t, id, invoke(mhGetDouble, obj, id)); }
            else                            { invoke(mhPutObject, t, id, invoke(mhGetObject, obj, id)); }
        }
        return t;
    }

    private static Object invoke(MethodHandle mh, Object... args) {
        try {
            return mh.invokeWithArguments(args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    // ======================
    // 🔧 辅助
    // ======================

    /** Java 21+ getObject→getReference 兼容 */
    private static MethodHandle findRefMH(MethodHandles.Lookup L, Class<?> U, String... names) throws Exception {
        for (String n : names) {
            try { return L.findVirtual(U, n, MethodType.methodType(Object.class, Object.class, long.class)).bindTo(Unsafe.getUnsafe()); }
            catch (NoSuchMethodException | IllegalAccessException ignored) {}
        }
        throw new NoSuchMethodException("None: " + Arrays.toString(names));
    }

    private static MethodHandle findRefMHPut(MethodHandles.Lookup L, Class<?> U, String... names) throws Exception {
        for (String n : names) {
            try { return L.findVirtual(U, n, MethodType.methodType(void.class, Object.class, long.class, Object.class)).bindTo(Unsafe.getUnsafe()); }
            catch (NoSuchMethodException | IllegalAccessException ignored) {}
        }
        throw new NoSuchMethodException("None: " + Arrays.toString(names));
    }
}
