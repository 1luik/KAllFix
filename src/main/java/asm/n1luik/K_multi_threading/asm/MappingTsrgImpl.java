package asm.n1luik.K_multi_threading.asm;


import java.util.HashMap;
import java.util.Map;

public class MappingTsrgImpl extends MappingImpl {
    public MappingTsrgImpl(String m) {
        Map<String,String> map2 = new HashMap<>();
        String n1 = null;//类名
        String m1 = null;//类名

        for (String s : m.split("(\n|\r\n)+")) {
            int i = Util.countStr(s, "\t");
            String[] split = s.replace("\t","").split(" ");

            switch (i) {
                case 0-> {//类型，名字
                    if (split.length == 3)continue;
                    map2.put(n1 = split[0], (m1=split[1]));
                }
                case 1->{
                    switch (split.length){
                        case 2->{
                            map2.put(n1 + "." + split[0], m1 + "." + split[1]);
                        }
                        case 3->{
                            map2.put(n1 + "." + split[0] + split[1], m1 + "." + split[2]);
                            map2.put(n1 + "." + split[0] + "}{", m1 + "." + split[2]);
                        }
                    }
                }
            }
        }
        for (Map.Entry<String, String> stringStringEntry : map2.entrySet()) {
            if (stringStringEntry.getKey().contains("(")) {

                String[] split1 = stringStringEntry.getKey().split("\\.");
                String[] split = split1[1].split("\\(");
                StringBuilder buffer = new StringBuilder("(");

                String[] strings = Util.toDescList(split[1]);
                for (int i = 0; i < strings.length; i++) {
                    String string = strings[i];
                    if (string.contains("[")) {

                        String replace = string.replace("[", "");
                        if (!Util.isDefaultClass(replace)) {
                            String substring = replace.substring(1, replace.length() - 1);
                            strings[i] = "[".repeat(Util.countStr(string, "[")) + "L" + map2.getOrDefault(substring, substring) + ";";
                        }
                    }else if (!Util.isDefaultClass(string)) {
                        strings[i] = map2.getOrDefault(string, string);
                    }
                }

                for (int i = 0; i < strings.length; i++) {
                    if (i == strings.length-1){
                        buffer.append(")");
                        buffer.append(strings[i]);
                        continue;
                    }
                    buffer.append(strings[i]);
                }

                map.put(stringStringEntry.getKey(), stringStringEntry.getValue() + buffer);
            }else {
                map.put(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
        }

    }
}
