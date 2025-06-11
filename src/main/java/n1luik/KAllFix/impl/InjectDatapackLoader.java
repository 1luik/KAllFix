package n1luik.KAllFix.impl;

import com.google.gson.Gson;
import com.majruszlibrary.modhelper.Resource;
import lombok.SneakyThrows;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class InjectDatapackLoader implements PackResources {
    private static final Gson GSON = new Gson();
    public static final Function<ResourceLocation, Map<ResourceLocation, byte[]>> resourceLocationMapFunction = k -> new HashMap<>();
    public static final Function<ResourceLocation, Map<ResourceLocation, List<byte[]>>> resourceLocationListFunction = k -> new HashMap<>();
    public static final Function<ResourceLocation, List<byte[]>> resourceLocationListFunction1 = k -> new ArrayList<>();
    public static final InjectDatapackLoader INSTANCE;
    public final AtomicInteger lock = new AtomicInteger(0);

    static {
        try {
            INSTANCE = new InjectDatapackLoader(new File("./InjectDatapack"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final File datapackDir;
    public final Map<ResourceLocation, Map<ResourceLocation, byte[]>> injectDatapacks = new HashMap<>();
    public final Map<ResourceLocation, Map<ResourceLocation, List<byte[]>>> injectTag = new HashMap<>();

    public InjectDatapackLoader(File datapackDir) throws IOException {
        this.datapackDir = datapackDir;
        datapackDir.mkdir();
        load();
    }

    public void clear(){
        injectDatapacks.clear();
        injectTag.clear();
    }

    @Deprecated
    public void load1(ZipFile zip) throws IOException {
        for (ZipEntry zipEntry : zip.stream().toList()) {
            String[] split = zipEntry.getName().split("/", 2);
            if (split.length != 2) {
                continue;
            }
            switch (split[0]) {
                case "data"->{
                    String[] data = split[1].split("/", 4);
                    if (data.length != 4) {
                        continue;
                    }
                    String datum = data[3];
                    if (!datum.endsWith(".json")){
                        continue;
                    }
                    if (datum.equals(".json")){
                        continue;
                    }
                    byte[] bytes = zip.getInputStream(zipEntry).readAllBytes();
                    injectDatapacks.computeIfAbsent(new ResourceLocation(data[0], data[1]), resourceLocationMapFunction)
                            .put(new ResourceLocation(data[2], datum.substring(0, datum.length()-5)), bytes);

                }
                case "tag"->{
                    String[] data = split[1].split("/", 3);
                    if (data.length != 3) {
                        continue;
                    }
                    String datum = data[2];
                    if (!datum.endsWith(".json")){
                        continue;
                    }
                    if (datum.equals(".json")){
                        continue;
                    }
                    injectTag.computeIfAbsent(new ResourceLocation(data[0], data[1]), resourceLocationListFunction)
                            .computeIfAbsent(new ResourceLocation(data[2], datum.substring(0, datum.length()-5)), resourceLocationListFunction1)
                            .add(zip.getInputStream(zipEntry).readAllBytes());



                }
            }
        }

    }

    public void load(ZipFile zip, Config config) throws IOException {
        for (Data datum : config.data) {
            injectDatapacks.computeIfAbsent(new ResourceLocation(datum.type), resourceLocationMapFunction)
                    .put(new ResourceLocation(datum.name),
                            zip.getInputStream(zip.getEntry(datum.file)).readAllBytes());

        }
        for (Tag tag : config.tag) {
            injectTag.computeIfAbsent(new ResourceLocation(tag.type), resourceLocationListFunction)
                    .computeIfAbsent(new ResourceLocation(tag.tag), resourceLocationListFunction1)
                    .add(zip.getInputStream(zip.getEntry(tag.file)).readAllBytes());

        }
    }

    public void load() throws IOException {
        if (!datapackDir.isDirectory()) return;
        File[] files = datapackDir.listFiles();
        if (files != null) {
            for (ZipFile zipFile : Arrays.stream(files).filter(f -> !f.isDirectory()).map(file1 -> {
                try {
                    return new ZipFile(file1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).toList()) {
                load(zipFile, GSON.fromJson(new InputStreamReader(zipFile.getInputStream(zipFile.getEntry("pack.mcmeta")), StandardCharsets.UTF_8), InjectDatapackLoader.Config.class));
            }
        }
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... p_252049_) {
        //非标不通用
        return null;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType p_215339_, ResourceLocation p_249034_) {
        //不知道功能
        return null;
    }

    @Override
    public void listResources(PackType p_10289_, String p_251379_, String p_251932_, ResourceOutput p_249347_) {
        //不知道功能
    }

    @Override
    public Set<String> getNamespaces(PackType p_10283_) {
        //非标不通用
        return Set.of();
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> p_10291_) throws IOException {
        //没有pack.mcmeta
        return null;
    }

    @Override
    public String packId() {
        return "KAllFix_InjectDatapackLoader";
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    public static class Config {
        public List<Data> data;
        public List<Tag> tag;
    }
    public static class Data {
        public String type;
        public String name;
        public String file;
    }
    public static class Tag {
        public String type;
        public String file;
        public String tag;
    }
}
