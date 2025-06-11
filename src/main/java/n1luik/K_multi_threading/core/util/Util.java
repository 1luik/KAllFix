package n1luik.K_multi_threading.core.util;

import java.io.*;
import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

public class Util {
    public static final long[] EmptyTps = new long[100];

    public static void main(String[] args) throws IOException, URISyntaxException {
        File path = args.length < 1 ? new File("./") : (args[0].toLowerCase().equals("-i") ? new File("./mods") : new File(args[0]));

        path.mkdirs();

        new File(path, "k_multi_threading-base.jar").delete();
        new File(path, "k_multi_threading-asm.jar").delete();
        new File(path, "k_multi_threading-base.jar").createNewFile();
        new File(path, "k_multi_threading-asm.jar").createNewFile();

        JarOutputStream jarBase = new JarOutputStream(new FileOutputStream(new File(path, "k_multi_threading-base.jar")));
        JarOutputStream jarAsm = new JarOutputStream(new FileOutputStream(new File(path, "k_multi_threading-asm.jar")));
        JarFile jarFile = new JarFile(new File(Util.class.getProtectionDomain().getCodeSource().getLocation().toURI()));

        List<String> copy = List.of(
                //"META-INF"
                );
        List<String> mov = List.of(
                "K_multi_threading.mapping",
                "asm/n1luik/K_multi_threading",
                "asm/n1luik/KAllFix",
                "asm/KAllFix.fix/",
                "META-INF/services"
                );
        List<String> names = new ArrayList<>();
        List<String> names2 = new ArrayList<>();
        for (JarEntry jarEntry : jarFile.stream().toList()) {
            String name = jarEntry.getName();
            if (name.endsWith("/"))continue;

            for (String s : copy) {
                if (name.startsWith(s) && !names.contains(name)){
                    names.add(name);
                    jarAsm.putNextEntry(jarEntry);
                    jarAsm.write(jarFile.getInputStream(jarEntry).readAllBytes());
                }
            }
            boolean isAsm = false;
            for (String s : mov) {
                if (name.startsWith(s)){
                    isAsm = true;
                    if (!names.contains(name)){
                        names.add(name);
                        jarAsm.putNextEntry(jarEntry);
                        jarAsm.write(jarFile.getInputStream(jarEntry).readAllBytes());
                    }
                }
            }
            if (!isAsm && !names2.contains(name)) {

                names2.add(name);
                jarBase.putNextEntry(jarEntry);
                jarBase.write(jarFile.getInputStream(jarEntry).readAllBytes());
            }
        }

        jarBase.close();
        jarAsm.close();
    }


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

    public static long getFieldMemoryPos(String className, String fieldName){
        try {
            return getFieldMemoryPos(Class.forName(className),fieldName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static long getFieldMemoryPos(ClassLoader loader,String className, String fieldName){
        try {
            return getFieldMemoryPos(loader.loadClass(className),fieldName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static long getFieldMemoryPos(Class<?> zlass, String fieldName){
        try {
            return Unsafe.unsafe.objectFieldOffset(zlass.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static final long CompletableFuture$result_id = getFieldMemoryPos("java.util.concurrent.CompletableFuture", "result");

    //public static <U> U CallCompletableFuture(CompletableFuture<U> future){
    //    Thread thread = Thread.currentThread();
    //    Unsafe.unsafe.park(false,0);
//
//
//
    //    U r
    //    Unsafe.unsafe.putObjectVolatile(future, CompletableFuture$result_id, r);
    //    CompletableFuture::join;
    //}

    public static String notMaxThreadInfoToString(ThreadInfo threadInfo, int max){

        StringBuilder sb = new StringBuilder("\"" + threadInfo.getThreadName() + "\"" +
                (threadInfo.isDaemon() ? " daemon" : "") +
                " prio=" + threadInfo.getPriority() +
                " Id=" + threadInfo.getThreadId() + " " +
                threadInfo.getThreadState());
        if (threadInfo.getLockName() != null) {
            sb.append(" on " + threadInfo.getLockName());
        }
        if (threadInfo.getLockOwnerName() != null) {
            sb.append(" owned by \"" + threadInfo.getLockOwnerName() +
                    "\" Id=" + threadInfo.getLockOwnerId());
        }
        if (threadInfo.isSuspended()) {
            sb.append(" (suspended)");
        }
        if (threadInfo.isInNative()) {
            sb.append(" (in native)");
        }
        sb.append('\n');
        int i = 0;
        StackTraceElement[] stackTrace = threadInfo.getStackTrace();
        for (; i < stackTrace.length && i < max; i++) {
            StackTraceElement ste = stackTrace[i];
            sb.append("\tat " + ste.toString());
            sb.append('\n');
            if (i == 0 && threadInfo.getLockInfo() != null) {
                Thread.State ts = threadInfo.getThreadState();
                switch (ts) {
                    case BLOCKED:
                        sb.append("\t-  blocked on " + threadInfo.getLockInfo());
                        sb.append('\n');
                        break;
                    case WAITING:
                        sb.append("\t-  waiting on " + threadInfo.getLockInfo());
                        sb.append('\n');
                        break;
                    case TIMED_WAITING:
                        sb.append("\t-  waiting on " + threadInfo.getLockInfo());
                        sb.append('\n');
                        break;
                    default:
                }
            }

            for (MonitorInfo mi : threadInfo.getLockedMonitors()) {
                if (mi.getLockedStackDepth() == i) {
                    sb.append("\t-  locked " + mi);
                    sb.append('\n');
                }
            }
        }
        if (i < stackTrace.length) {
            sb.append("\t...");
            sb.append('\n');
        }

        LockInfo[] locks = threadInfo.getLockedSynchronizers();
        if (locks.length > 0) {
            sb.append("\n\tNumber of locked synchronizers = " + locks.length);
            sb.append('\n');
            for (LockInfo li : locks) {
                sb.append("\t- " + li);
                sb.append('\n');
            }
        }
        sb.append('\n');
        return sb.toString();
    }
}
