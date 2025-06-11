package asm.n1luik.K_multi_threading.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {



    /**
     *
     * @param longStr 长字符串
     * @param mixStr 子字符串
     * @return 包含个数
     */
    public static int countStr(String longStr, String mixStr) {
        //如果确定传入的字符串不为空，可以把下面这个判断去掉，提高执行效率
//        if(longStr == null || mixStr == null || "".equals(longStr.trim()) || "".equals(mixStr.trim()) ){
//             return 0;
//        }
        int count = 0;
        int index = 0;
        while((index = longStr.indexOf(mixStr,index))!= -1){
            index = index + mixStr.length();
            count++;
        }
        return count;
    }

    public static final Pattern descIs = Pattern.compile("(|\\[+)(L(.+);|[VBZCSIFDJ])",Pattern.UNIX_LINES);
    public static String[] toDescList(String methodDescriptor){

        Matcher matcher = descIs.matcher(methodDescriptor.replaceAll("[()]", "").replace(";",";\n"));
        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches.toArray(new String[0]);/*
        CharList bytes = new CharArrayList();
        List<String> ret = new ArrayList<>();
        boolean isClass = false;
        int a = 0;
        for (char aByte : methodDescriptor.toCharArray()) {
            if (aByte == '[') a++;
            else  {

                if (!isClass && aByte == 'L') {
                    isClass = true;
                }else if (isClass) {
                    if (aByte != ';')
                        bytes.add(aByte);
                    else {
                        ret.add("[".repeat(a)+new String(bytes.toCharArray()));
                        a=0;
                        isClass = false;
                    }
                } else {
                    ret.add("[".repeat(a)+String.valueOf(aByte));
                }
            }
        }
        return ret.toArray(new String[0]);*/
    }

    public static boolean isDefaultClass(String text) {
        return switch (text.replace("[","")){
            case "B", "J", "C", "Z", "F", "I", "S", "D", "V" -> true;
            default ->  false;
        };
    }
}
