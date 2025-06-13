package asm.n1luik.K_multi_threading.asm;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MappingSrgImpl extends MappingImpl {

    public MappingSrgImpl(String m) {
        Map<String,String> map2 = new HashMap<>();

        for (String s : m.split("(\n|\r\n)+")) {
            String[] split = s.split(": ");
            String[] split1 = split[1].split(" ");

            switch (split[0]) {
                case "CL"-> {
                    map.put(split1[0], (split1[1]));
                }
                case "FD"->{

                    String[] split2 = split1[0].split("/(?!(.+)/)");
                    String[] split3 = split1[1].split("/(?!(.+)/)");

                    map.put(split2[0] + "." + split2[1], split3[0] + "." + split3[1]);
                }
                case "MD"->{

                    String[] split2 = split1[0].split("/(?!(.+)/)");
                    String[] split3 = split1[2].split("/(?!(.+)/)");

                    map.put(split2[0] + "." + split2[1] + split1[1], split3[0] + "." + split3[1] + split1[3]);
                    map.put(split2[0] + "." + split2[1] + "}{", split3[0] + "." + split3[1]);
                }
                default -> {
                    throw new RuntimeException();
                }
            }
        }

    }
}
