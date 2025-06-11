package asm.n1luik.K_multi_threading.asm;

import java.util.HashMap;
import java.util.Map;

public abstract class MappingImpl {
    protected final Map<String,String> map = new HashMap<>();

    public String[] mapMethod(String name){
        String orDefault = map.getOrDefault(name, name);
        String[] split = orDefault.split("\\.");
        String[] split1 = split[1].split("\\(");
        return new String[]{split[0],split1[0],"("+split1[1]};
    }

    public String[] mapField(String name) {
        String orDefault = map.getOrDefault(name, name);
        String[] split = orDefault.split("\\.");
        return new String[]{split[0], split[1]};
    }
}
