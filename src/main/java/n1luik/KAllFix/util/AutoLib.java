package n1luik.KAllFix.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public abstract class AutoLib<T> extends URLClassLoader  {
    public static final File DownloadPath = new File("./lib");
    static {
        DownloadPath.mkdirs();
    }
    public static URL download(URL url, String name) throws MalformedURLException {
        File file = new File(DownloadPath, name);
        if (!file.isFile()) {
            try {
                if (file.createNewFile()) {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(url.openConnection().getInputStream().readAllBytes());
                    fileOutputStream.close();
                }else {
                    throw new RuntimeException("Failed to create file "+file.getAbsolutePath());
                }
            } catch (IOException e) {
                file.delete();
                throw new RuntimeException(e);
            }
        }
        return file.toURI().toURL();
    }
    public final String url;
    public final String version;
    public final String name;
    public final String groupId;

    public AutoLib(String url, String version, String name, String groupId) throws MalformedURLException {
        super(new URL[]{download(new URL(url+"/"+(groupId.replace(".", "/"))+"/"+name+"/"+version+"/"+name+"-"+version+".jar"), name+"-"+version+".jar")}, AutoLib.class.getClassLoader());
        this.url = url;
        this.version = version;
        this.name = name;
        this.groupId = groupId;
    }

    public abstract T getInstance();
}
