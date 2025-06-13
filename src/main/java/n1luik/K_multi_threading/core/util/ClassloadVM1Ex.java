package n1luik.K_multi_threading.core.util;

import java.io.*;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.SecureClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassloadVM1Ex extends SecureClassLoader implements ClassTools {

    public static boolean debugOut = false;
    private static Integer addi = 0;
    private static final File classDataId = new File("./" + System.getProperty("setupMain.dataFile") + "/newClass").getAbsoluteFile();

    static {
        ClassLoader.registerAsParallelCapable();
        deleteDir(classDataId);
        classDataId.mkdir();
    }

    protected File classData;

    private final Map<String,byte[]> loadFile = new HashMap<>();
    public final Map<String, JarFuleBuf> file = new HashMap<>();
    public final Map<String, File> getFile = new HashMap<>();
    protected final Map<String, Class<?>> classloader = new HashMap<>();
    public final List<File> addFileList = new ArrayList<>();
    public final int hash;
    public final List<ClassLoader> addload = new ArrayList<>();
    public final List<CoreLoader> cores = new ArrayList<>();
    public final Map<Object,Object> dataBuf = new HashMap<>();

    public ClassloadVM1Ex(){
        super();
        addi++;
        hash = Objects.hash(this);///Objects.hash(addi);
        classData = new File(classDataId, "classData_" + hash);
        classData.mkdir();
        //addFile(new File(System.getProperty("java.home")+"\\jmods\\").listFiles());
    }

    public ClassloadVM1Ex(File JarList){
        this(new File[]{JarList});
    }
    public ClassloadVM1Ex(File ...JarList){
        this();

        addFile(JarList);
    }

    public ClassloadVM1Ex(ClassLoader adds) throws NoSuchFieldException, IllegalAccessException {
        this();
        addload.add(adds);
    }

    public ClassloadVM1Ex(ClassLoader ...addsList) throws NoSuchFieldException, IllegalAccessException {
        this();
        Collections.addAll(addload, addsList);
    }

    public ClassloadVM1Ex(File loadZip, ClassLoader ...addsList) {
        this();

        Collections.addAll(addload, addsList);
        addFile(loadZip);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return new Enumeration<URL>() {
            Iterator<URL> urls = cores.stream().map(c -> {
                try {
                    return c._getResURL(name);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).filter(Objects::nonNull).toList().iterator();

            @Override
            public boolean hasMoreElements() {
                return urls.hasNext();
            }

            @Override
            public URL nextElement() {
                return urls.next();
            }
        };
    }

    @Override
    public void addClassLoader(ClassLoader ...addsList) {
        Collections.addAll(addload, addsList);
    }

    public boolean getResURLthrow = false;

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
        Arrays.stream(JarList).map(CoreLoader::new).forEach(cores::add);
    }

    @Override
    public void MFaddFile(File ...JarList){
        // = List.of(JarList);
        for (File loadZip : JarList) {
            addFileList.add(loadZip);
            try {
                JarFile jar = new JarFile(loadZip);
                for (JarEntry entry : jar.stream().toList()) {
                    if (entry.getName().equals("META-INF/MANIFEST.MF")) continue;
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
        for (CoreLoader core : cores) {
            byte[] bytes = core.readFile(name);
            if (bytes != null)return bytes;
        }

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

    private Class<?> findClass(String name, CoreLoader coreLoader) {
        for (CoreLoader loader : cores.stream().filter(c -> c.hash != coreLoader.hash).toList()) {
            Class<?> aClass = null;
            try {
                aClass = loader._findClass(name);
            } catch (ClassNotFoundException e) {
            }
            if (aClass != null) return aClass;
        }
        return null;
    }

    private URL getResURL(String name, CoreLoader coreLoader) {
        for (CoreLoader loader : cores.stream().filter(c -> c.hash != coreLoader.hash).toList()) {
            URL url = null;
            try {
                url = loader._getResURL(name);
            } catch (IOException ignored) {
            }
            if (url != null) return url;
        }
        return null;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {

        for (CoreLoader core : cores) {
            Class<?> aClass;
            try {
                aClass = core.findClass(name);
            }catch (Throwable e){
                continue;
            }
            if (aClass != null)return aClass;
        }

        if (debugOut)System.out.println("findClass : "+name);//if (loadConfig.Config != null) if (loadConfig.Config.ClassLoadRunLog)LOGGER.debug("findClass : "+name);

        Class<?> buf1 = classloader.get(name);
        if (buf1 != null)return buf1;//classloader.get(name);

        String path = name.replace('.', '/').concat(".class");

        JarFuleBuf fileBuf = file.get(path);

        /*if (fileBuf==null){
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
        }*/

        if(fileBuf == null)throw new ClassNotFoundException("目前导入的jar没有"+name+"这个文件");
        Class<?> set = defineClass(fileBuf, name);
        classloader.put(name, set);
        return set;
    }

    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (debugOut)System.out.println("loadClass : "+name);
        Class<?> jc = super.loadClass(name,resolve);
        return jc;
    }

    @Override
    public void LoadAllClass()
    {
        cores.forEach(CoreLoader::LoadAllClass);
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

    public URL getResURL(String name) throws IOException {

        for (CoreLoader core : cores) {
            URL url;
            try {
                url = core.getResURL(name);
            }catch (Throwable e){
                continue;
            }
            if (url != null)return url;
        }

        for (File file1 : addFileList) {
            if (file1.getCanonicalPath().equals(name)) return file1.toURI().toURL();
        }

        if (getFile.get(name) != null) return getFile.get(name).toURI().toURL();
        File i = new File(classData,"name_hash"+Objects.hash(name)+".DAT");
        if (debugOut)System.out.println("getResURL "+name);//
        i.createNewFile();
        OutputStream outputStream = new FileOutputStream(i);
        byte[] noNull = readFile(name);

        if (noNull == null) {
            outputStream.close();

            /*for (ClassLoader loader : addload) {
                URL resource = loader.getResource(name);
                if (resource != null) return resource;
            }*/

            if (getResURLthrow) throw new RuntimeException("目前导入的jar没有" + name + "这个文件");
            else return null;
        }

        outputStream.write(noNull);
        outputStream.close();
        getFile.put(name, i);
        return i.toURI().toURL();
    }

    

    private class CoreLoader extends SecureClassLoader implements ClassTools{

        static {
            ClassLoader.registerAsParallelCapable();
            deleteDir(classDataId);
            classDataId.mkdir();
        }

        protected File classData;

        private final Map<String,byte[]> loadFile = new HashMap<>();
        public final Map<String, JarFuleBuf> file = new HashMap<>();
        public final Map<String, File> getFile = new HashMap<>();
        protected final Map<String, Class<?>> classloader = new HashMap<>();
        public final List<File> addFileList = new ArrayList<>();
        public final int hash;
        public final Map<Object,Object> dataBuf = new HashMap<>();

        CoreLoader(){
            super();
            addi++;
            hash = Objects.hash(this);///Objects.hash(addi);
            classData = new File(classDataId, "classData_" + hash);
            classData.mkdir();
            //addFile(new File(System.getProperty("java.home")+"\\jmods\\").listFiles());
        }

        public CoreLoader(File JarList){
            this(new File[]{JarList});
        }

        public CoreLoader(File ...JarList){
            this();

            addFile(JarList);
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            return ClassloadVM1Ex.this.getResources(name);
        }

        public void addClassLoader(ClassLoader ...addsList) {
            Collections.addAll(addload, addsList);
        }

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

        public void addFile(File ...JarList){
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

        @Override
        public void MFaddFile(File ...JarList){
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

        public Class<?> _findClass(String name) throws ClassNotFoundException {
            if (debugOut)System.out.println("findClass : "+name);//if (loadConfig.Config != null) if (loadConfig.Config.ClassLoadRunLog)LOGGER.debug("findClass : "+name);

            Class<?> buf1 = classloader.get(name);
            if (buf1 != null)return buf1;//classloader.get(name);

            String path = name.replace('.', '/').concat(".class");

            JarFuleBuf fileBuf = file.get(path);


            if(fileBuf == null)throw new ClassNotFoundException("目前导入的jar没有"+name+"这个文件");
            Class<?> set = defineClass(fileBuf, name);
            classloader.put(name, set);
            return set;
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            if (debugOut)System.out.println("findClass : "+name);//if (loadConfig.Config != null) if (loadConfig.Config.ClassLoadRunLog)LOGGER.debug("findClass : "+name);

            Class<?> buf1 = classloader.get(name);
            if (buf1 != null)return buf1;//classloader.get(name);

            String path = name.replace('.', '/').concat(".class");

            JarFuleBuf fileBuf = file.get(path);

            if (fileBuf==null){
                Class<?> aClass1 = ClassloadVM1Ex.this.findClass(name, this);
                if (aClass1 != null) return aClass1;
                for (ClassLoader loader : addload) {
                    Class<?> aClass = null;
                    try {
                        URL resource = loader.getResource(path);
                        if (resource == null)
                            aClass = loader.loadClass(name);
                        else {
                            JarFuleBuf value = new JarFuleBuf(resource, new JarEntry(path), resource.openConnection().getInputStream());
                            file.put(path, value);
                            aClass = defineClass(value, name);
                        }

                    }catch (Throwable e){

                    }
                    if (aClass != null) {
                        classloader.put(name, aClass);
                        return aClass;
                    }
                }
            }
            if(fileBuf == null)throw new ClassNotFoundException("目前导入的jar没有"+name+"这个文件");
            Class<?> set = defineClass(fileBuf, name);
            classloader.put(name, set);
            return set;
        }

        public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (debugOut)System.out.println("loadClass : "+name);
            Class<?> jc = super.loadClass(name,resolve);
            return jc;
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

        public URL _getResURL(String name) throws IOException {


            for (File file1 : addFileList) {
                if (file1.getCanonicalPath().equals(name)) return file1.toURI().toURL();
            }

            if (getFile.get(name) != null) return getFile.get(name).toURI().toURL();
            File i = new File(classData,"name_hash"+Objects.hash(name)+".DAT");
            if (debugOut)System.out.println("getResURL "+name);//
            i.createNewFile();
            OutputStream outputStream = new FileOutputStream(i);
            byte[] noNull = readFile(name);

            if (noNull == null) {
                outputStream.close();
                return null;
            }


            outputStream.write(noNull);
            outputStream.close();
            getFile.put(name, i);
            return i.toURI().toURL();
        }

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

            if (noNull == null) {
                outputStream.close();

                URL resURL = ClassloadVM1Ex.this.getResURL(name, this);
                if (resURL != null) return resURL;
                for (ClassLoader loader : addload) {
                    URL resource = loader.getResource(name);
                    if (resource != null) return resource;
                }

                if (getResURLthrow) throw new RuntimeException("目前导入的jar没有" + name + "这个文件");
                else return null;
            }


            outputStream.write(noNull);
            outputStream.close();
            getFile.put(name, i);
            return i.toURI().toURL();
        }
    }
}
