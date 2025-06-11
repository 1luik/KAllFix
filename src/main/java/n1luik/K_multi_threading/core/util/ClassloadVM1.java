package n1luik.K_multi_threading.core.util;

//import com.mojang.logging.LogUtils;
//import org.apache.commons.io.filefilter.NotFileFilter;
//import org.slf4j.Logger;

import java.io.*;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.SecureClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassloadVM1 extends SecureClassLoader implements ClassTools {

    //protected static final Logger LOGGER = LogUtils.getLogger();

    public static boolean debugOut = false;
    private static Integer addi = 0;
    private static final File classDataId = new File("./" + System.getProperty("setupMain.dataFile") + "/newClass").getAbsoluteFile();
    protected File classData;

    private final Map<String,byte[]> loadFile = new HashMap<>();
    public final Map<String, JarFuleBuf> file = new HashMap<>();
    public final Map<String, File> getFile = new HashMap<>();
    protected final Map<String, Class<?>> classloader = new HashMap<>();
    public final List<File> addFileList = new ArrayList<>();
    public final int hash;
    public final List<ClassLoader> addload = new ArrayList<>();
    public final Map<Object,Object> dataBuf = new HashMap<>();
    public ClassloadVM1(File JarList){
        this(new File[]{JarList});
    }
    public ClassloadVM1(File ...JarList){
        super();
        addi++;
        hash = Objects.hash(this);///Objects.hash(addi);
        classData = new File(classDataId, "classData_" + hash);
        classData.mkdir();
        //addFile(new File(System.getProperty("java.home")+"\\jmods\\").listFiles());
        // = List.of(JarList);
        for (File loadZip : JarList) {
            addFileList.add(loadZip);
            try {
                JarFile jar = new JarFile(loadZip);
                for (JarEntry entry : jar.stream().toList()) {

                    file.put(entry.getName(), new JarFuleBuf(loadZip.toURI().toURL(), entry,jar.getInputStream(entry)));
                }
            } catch (IOException e) {
            }
        }
    }

    public ClassloadVM1(){
        super();
        addi++;
        hash = Objects.hash(this);///Objects.hash(addi);
        classData = new File(classDataId, "classData_" + hash);
        classData.mkdir();
        //addFile(new File(System.getProperty("java.home")+"\\jmods\\").listFiles());
    }

    public ClassloadVM1(ClassLoader adds) throws NoSuchFieldException, IllegalAccessException {
        super();
        //super(adds);
        addi++;
        hash = Objects.hash(this);///Objects.hash(addi);
        classData = new File(classDataId, "classData_" + hash);
        classData.mkdir();
        addload.add(adds);
        //List<Class<?>> getClass = (ArrayList<Class<?>>)Unsafe.getStatic(ClassLoader.class, "classes", adds);
        //for (Class<?> aClass : getClass) {
        //    classloader.put(aClass.getName(),aClass);
        //}
        //addFile(new File(System.getProperty("java.home")+"\\jmods\\").listFiles());
    }

    public ClassloadVM1(ClassLoader ...addsList) throws NoSuchFieldException, IllegalAccessException {
        super();
        //super(addsList[0]);
        addi++;
        hash = Objects.hash(this);///Objects.hash(addi);
        classData = new File(classDataId, "classData_" + hash);
        classData.mkdir();
        Collections.addAll(addload, addsList);

        //for (ClassLoader adds : addsList) {
        //    List<Class<?>> getClass = (ArrayList<Class<?>>)Unsafe.getStatic(ClassLoader.class, "classes", adds);
        //    for (Class<?> aClass : getClass) {
        //        classloader.put(aClass.getName(),aClass);
        //    }
        //}
        //addFile(new File(System.getProperty("java.home")+"\\jmods\\").listFiles());
    }

    public ClassloadVM1(File loadZip, ClassLoader ...addsList) {
        super();
        //super(addsList[0]);
        addi++;
        hash = Objects.hash(this);///Objects.hash(addi);
        classData = new File(classDataId, "classData_" + hash);
        classData.mkdir();
        //addFile(new File(System.getProperty("java.home")+"\\jmods\\").listFiles());
        Collections.addAll(addload, addsList);
        //for (ClassLoader adds : addsList) {
        //    List<Class<?>> getClass = (ArrayList<Class<?>>)Unsafe.getStatic(ClassLoader.class, "classes", adds);
        //    for (Class<?> aClass : getClass) {
        //        classloader.put(aClass.getName(),aClass);
        //    }
        //}

        addFileList.add(loadZip);
        try {
            JarFile jar = new JarFile(loadZip);
            for (JarEntry entry : jar.stream().toList()) {

                file.put(entry.getName(), new JarFuleBuf(loadZip.toURI().toURL(), entry,jar.getInputStream(entry)));
            }
        } catch (IOException e) {
        }

        //addFile(new File(System.getProperty("java.home")+"\\lib\\src.zip"));
    }

    @Override
    public void addClassLoader(ClassLoader ...addsList) {
        Collections.addAll(addload, addsList);
    }

    public boolean getResURLthrow = true;
    public URL getResURL(String name) throws IOException {

        for (File file1 : addFileList) {
            if (file1.getCanonicalPath().equals(name)) return file1.toURI().toURL();
        }

        if (getFile.get(name) != null) return getFile.get(name).toURI().toURL();
        File i = new File(classData,"name_hash"+Objects.hash(name)+".DAT");
        if (debugOut)System.out.println("getResURL "+name);//
        i.createNewFile();
        OutputStream outputStream = new FileOutputStream(i);
        byte[] noNull = readFile(name);

        if (noNull == null) if (getResURLthrow) throw new RuntimeException("目前导入的jar没有"+name+"这个文件");else return null;


        outputStream.write(noNull);
        outputStream.close();
        getFile.put(name, i);
        return i.toURI().toURL();
    }

    @Override
    public URL findResource(String name) {
        if (debugOut)System.out.println("findResource : "+name);//if (loadConfig.Config != null) if (loadConfig.Config.ClassLoadRunLog)LOGGER.debug("findResource : "+name);
        //super.findResource(name);
        try {
            return getResURL(name);//super.findResource(name);
        } catch (IOException e) {
            return null;
            //throw new RuntimeException(e);
        }
    }

    @Override
    public void addFile(File ...JarList){
        // = List.of(JarList);
        for (File loadZip : JarList) {
            addFileList.add(loadZip);
            try {
                JarFile jar = new JarFile(loadZip);
                for (JarEntry entry : jar.stream().toList()) {
                    if (entry.getName() == "META-INF/MANIFEST.MF") continue;
                    file.put(entry.getName(), new JarFuleBuf(loadZip.toURI().toURL(), entry,jar.getInputStream(entry)));
                }
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void MFaddFile(File ...JarList){
        // = List.of(JarList);
        for (File loadZip : JarList) {
            addFileList.add(loadZip);
            try {
                JarFile jar = new JarFile(loadZip);
                for (JarEntry entry : jar.stream().toList()) {
                    if (entry.getName() == "META-INF/MANIFEST.MF") continue;
                    file.put(entry.getName(), new JarFuleBuf(loadZip.toURI().toURL(), entry,jar.getInputStream(entry)));
                }
            } catch (IOException e) {
            }
        }
    }

    private byte[] readFile(InputStream inputStream, String nam) throws IOException {
        byte[] ret = loadFile.get(inputStream);
        if (ret == null){
            ret = inputStream.readAllBytes();
            loadFile.put(nam, ret);
        }
        return ret;
    }

    @Override
    public byte[] readFile(String name) throws IOException {
        JarFuleBuf buf = file.get(name);
        if (buf == null)return null;
        return readFile(buf.jarRead,name);
    }

    @Override
    public Class<?> defineClass(JarFuleBuf buf, String name) {

        byte[] adds;
        try {
            adds = readFile(buf.jarRead,name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CodeSigner[] signers = buf.jarEntry.getCodeSigners();
        CodeSource source = new CodeSource(buf.jarFile, signers);
        return defineClass(name, adds, 0, adds.length, source);
    }

    @Override
    public void setClass(Class<?> c){
        classloader.remove(c.getName());
        classloader.put(c.getName(),c);
    }

    @Override
    public void setClass(URL jarFile,JarEntry jarEntry,byte[] c,String name){

        CodeSigner[] signers = jarEntry.getCodeSigners();
        CodeSource source = new CodeSource(jarFile, signers);
        classloader.remove(name);
        classloader.put(name,defineClass(name, c, 0, c.length, source));
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        if (debugOut)System.out.println("findClass : "+name);//if (loadConfig.Config != null) if (loadConfig.Config.ClassLoadRunLog)LOGGER.debug("findClass : "+name);

        Class<?> buf1 = classloader.get(name);
        if (buf1 != null)return buf1;//classloader.get(name);

        String path = name.replace('.', '/').concat(".class");

        JarFuleBuf fileBuf = file.get(path);

        if (fileBuf==null){
            for (ClassLoader loader : addload) {
                Class<?> aClass = null;
                try {
                    aClass = loader.loadClass(name);
                }catch (Throwable e){

                }
                if (aClass != null) {
                    classloader.put(name, aClass);
                    return aClass;
                }
            }
        }
        if(fileBuf == null)throw new RuntimeException("目前导入的jar没有"+name+"这个文件");
        Class<?> set = defineClass(fileBuf, name);
        classloader.put(name, set);
        return set;
    }

    //public Class<?> loadClass(String name) throws ClassNotFoundException {
    //    return loadClass(name, false);
    //}


    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (debugOut)System.out.println("loadClass : "+name);//if (loadConfig.Config != null) if (loadConfig.Config.ClassLoadRunLog) LOGGER.debug("loadClass : "+name);
//
        //    // First, check if the class has already been loaded
        //    Class<?> c = findLoadedClass(name);
        //    if (c == null) {
        //        long t0 = System.nanoTime();
        //        try {
        //            if (parent != null) {
        //                c = parent.loadClass(name, false);
        //            } else {
        //                c = findBootstrapClassOrNull(name);
        //            }
        //        } catch (ClassNotFoundException e) {
        //            // ClassNotFoundException thrown if class not found
        //            // from the non-null parent class loader
        //        }
//
        //        if (c == null) {
        //            // If still not found, then invoke findClass in order
        //            // to find the class.
        //            long t1 = System.nanoTime();
        //            c = findClass(name);
//
        //            // this is the defining class loader; record the stats
        //            PerfCounter.getParentDelegationTime().addTime(t1 - t0);
        //            PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
        //            PerfCounter.getFindClasses().increment();
        //        }
        //    }
        //    if (resolve) {
        //        resolveClass(c);
        //    }
        //    return c;




        //if ((name.startsWith("java.")||name.startsWith("jdk."))){
        //    //try {
        Class<?> jc = super.loadClass(name,resolve);
        //        if (jc != null) {
        //classloader.put(name, jc);
        return jc;
        //        }
        //}catch (Throwable e){
        //}
        //}

        //try {
        //super.loadClass(name,resolve);
        //}catch (Throwable e){
        //}

        //if (c == null)
        //if (name.startsWith("java.")||name.startsWith("jdk.")){
        //    Class<?> jc = super.loadClass(name,resolve);
        //    if (jc != null) {
        //        classloader.put(name, jc);
        //        if (resolve) {
        //            resolveClass(jc);
        //        }
        //        return jc;
        //    }
        //}

        //Class<?> c = findClass(name);




        //if (resolve) {
        //    resolveClass(c);
        //}
        //return c;
        //throw new ClassNotFoundException(name);
    }

    @Override
    public void LoadAllClass()
    {
        file.forEach((path,file)-> {
            if (path.endsWith(".class")){
                String name = (path.replace('/', '.')+"\n\t").replace(".class\n\t","");

                if (debugOut)System.out.println("LoadAllClass : "+name);//if (loadConfig.Config != null) if (loadConfig.Config.ClassLoadRunLog)LOGGER.debug("LoadAllClass : "+name);


                if (classloader.get(name) != null)return;

                JarFuleBuf fileBuf = this.file.get(path);


                Class<?> set = defineClass(fileBuf, name);
                classloader.put(name, set);
            }
        });
    }

    public Map<String, Class<?>> getLoad(){return classloader;}

    @Override
    public Map<String, Class<?>> getLoadList() {
        return classloader;
    }

    static {
        ClassLoader.registerAsParallelCapable();
        deleteDir(classDataId);
        //new File("./"+System.getProperty("KCS.dataFile")).mkdir();
        classDataId.mkdir();
        //new File(System.getProperty("java.io.tmpdir"))
    }

    public static void deleteDir(File directory){
        File files[] = directory.listFiles();
        if ( files != null)
            for (File file : files) {
                if(file.isDirectory()){
                    deleteDir(file);
                }else {
                    file.delete();
                }
            }
        directory.delete();
    }
}
