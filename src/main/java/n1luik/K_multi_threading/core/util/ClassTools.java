package n1luik.K_multi_threading.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.jar.JarEntry;

public interface ClassTools {
    default Class<?> defineClass(JarFuleBuf buf, String name){return null;}
    void addFile(File...JarList);
    void MFaddFile(File ...JarList);
    void addClassLoader(ClassLoader ...addList);
    void LoadAllClass();
    byte[] readFile(String name) throws IOException;
    Map<String, Class<?>> getLoadList();
    void setClass(Class<?> c);
    void setClass(URL jarFile, JarEntry jarEntry, byte[] c, String name);

    public static class JarFuleBuf
    {
        public final URL jarFile;
        public final JarEntry jarEntry;
        public final InputStream jarRead;
        public JarFuleBuf(URL jarFile, JarEntry jarEntry, InputStream jarRead)
        {
            this.jarFile = jarFile;
            this.jarEntry = jarEntry;
            this.jarRead = jarRead;
        }
    }
}
