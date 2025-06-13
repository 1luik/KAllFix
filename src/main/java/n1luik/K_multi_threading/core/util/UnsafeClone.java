package n1luik.K_multi_threading.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UnsafeClone<S, T> {
    public final Class<T> type;
    public final long[] ids;
    public final List<Field> fields;
    private static final sun.misc.Unsafe unsafe = Unsafe.unsafe;

    public UnsafeClone(Class<S> src, Class<T> clazz) {
        Class<? super S> superclass = src;
        fields = new ArrayList<>();
        while (superclass != null && superclass != Object.class){
            superclass = superclass.getSuperclass();
            List<Field> tfields1 = Arrays.stream(src.getDeclaredFields()).filter(field -> !Modifier.isStatic(field.getModifiers())).toList();
            List<String> Tfields = tfields1.stream().map(Field::getName).toList();
            fields.addAll(tfields1);
            Arrays.stream(src.getFields()).filter(field -> !Modifier.isStatic(field.getModifiers()) && !Tfields.contains(field.getName())).forEach(fields::add);
        }
        //fields = Arrays.stream(declared ? src.getDeclaredFields() : src.getFields()).filter(field -> !Modifier.isStatic(field.getModifiers())).toList();
        ids = new long[fields.size()]; // 创建一个与字段数量相同的long数组
        // 获取每个字段的偏移量
        for (int i = 0; i < fields.size(); i++) {
            ids[i] = unsafe.objectFieldOffset(fields.get(i));
        }
        this.type = clazz;
    }
    public T clone(S obj) throws InstantiationException {
        T t = (T) Unsafe.unsafe.allocateInstance(type);
        for (int i = 0; i < ids.length; i++) {
            long id = ids[i];
            Field field = fields.get(i);
            Class<?> type1 = field.getType();
            if (type1 == byte.class){
                unsafe.putByte(t,id, unsafe.getByte(obj, id));
            }else if (type1 == boolean.class){
                unsafe.putBoolean(t,id, unsafe.getBoolean(obj, id));
            }else if (type1 == short.class){
                unsafe.putShort(t,id, unsafe.getShort(obj, id));
            }else if(type1 == char.class){
                unsafe.putChar(t,id, unsafe.getChar(obj, id));
            }else if(type1 == int.class){
                unsafe.putInt(t,id, unsafe.getInt(obj, id));
            }else if(type1 == long.class){
                unsafe.putLong(t,id, unsafe.getLong(obj, id));
            }else if(type1 == float.class){
                unsafe.putFloat(t,id, unsafe.getFloat(obj, id));
            }else if(type1 == double.class){
                unsafe.putDouble(t,id, unsafe.getDouble(obj, id));
            }else {
                unsafe.putObject(t,id, unsafe.getObject(obj, id));
            }
        }

        return t;
    }
}
