package n1luik.K_multi_threading.debug.ex.spark;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import me.lucko.spark.common.monitor.cpu.CpuMonitor;
import me.lucko.spark.common.monitor.disk.DiskUsage;
import me.lucko.spark.common.monitor.memory.GarbageCollectorStatistics;
import me.lucko.spark.common.monitor.memory.MemoryInfo;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.util.IndexedListBuilder;
import me.lucko.spark.proto.SparkProtos;
import me.lucko.spark.proto.SparkSamplerProtos;
import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.util.NodeMap.Long2ObjectNodeHashMap;
import n1luik.K_multi_threading.debug.ex.DebugLog;
import n1luik.K_multi_threading.debug.ex.data.*;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.util.*;

import static n1luik.K_multi_threading.debug.ex.data.KMTDebugCommand.GSON;

public class SparkSave {
    //public static final ForgeClassSourceLookup CLASS_SOURCE_LOOKUP = new ForgeClassSourceLookup();
    public final LogRoot src;
    public NodeSave link = null;
    public NodeSave sum = new NodeSave();
    public final long linkThreadId;

    public SparkSave(LogRoot src, long linkThreadId) {
        this.src = src;
        this.linkThreadId = linkThreadId;
    }

    private DataBase linkAutoGetNext(JvmCallLogData jvmCallLogData, DataBase data) {
        for (DataBase dataBase : data.next) {
            if (dataBase.data.equals(jvmCallLogData)) {
                return dataBase;
            }
        }
        return null;

    }

    private void createSum() {
        sum.start = new DataBase();
        sum.threadName = "sum";
        sum.threadId = -1;
        sum.time = src.runTime;
        sum.startTime = src.startTime;
        sum.endTime = src.startTime + src.runTime;
        for (RelationshipSave relationshipSave : src.relationshipSave) {
            for (NodeSave datum : relationshipSave.data) {
                sum.start.sum(datum.start);
            }
        }
    }
    private void linkTree(Long2ObjectMap<ArrayList<LinkPosData>> linkBuf, long start, long end, long id, DataBase add) {
        assert start <= end;
        //DataBase dataBase = new DataBase();
        Object linkTag = new Object();
        if (!linkBuf.containsKey(id)) return;
        List<LinkPosData> linkPosData = linkBuf.get(id).stream().filter(v->v.trackTime <= start && end <= v.trackTime).toList();
        for (LinkPosData linkPosDatum : linkPosData) {
            linkPosDatum.link = linkTag;
        }
        for (LinkPosData linkPosDatum : linkPosData) {
            for (NodeSave datum : linkPosDatum.data) {
                DataBase linkStart = getDataBase(add, linkPosDatum);
                linkTree(linkBuf, linkPosDatum.startTime, linkPosDatum.endTime, datum.threadId, linkStart);

            }
        }
        for (LinkPosData linkPosDatum : linkPosData) {
            if (linkPosDatum.link != linkTag || linkPosDatum.runLink) {
                continue;
            }
            linkPosDatum.runLink = true;
            for (NodeSave datum : linkPosDatum.data) {
                DataBase linkStart = add;
                for (JvmCallLogData jvmCallLogData : linkPosDatum.pos) {
                    DataBase linkStart1 = linkAutoGetNext(jvmCallLogData, linkStart);
                    if (linkStart1 == null) {
                        linkStart.add(new DataBase(jvmCallLogData, linkPosDatum.endTime - linkPosDatum.startTime, new ArrayList<>(1)));
                    }else {
                        linkStart = linkStart1;
                    }
                }
                linkStart.sum(datum.start.copy());
            }
        }
    }

    private DataBase getDataBase(DataBase add, LinkPosData linkPosDatum) {
        DataBase linkStart = add;
        for (JvmCallLogData jvmCallLogData : linkPosDatum.pos) {
            DataBase linkStart1 = linkAutoGetNext(jvmCallLogData, linkStart);
            if (linkStart1 == null) {
                DataBase data = new DataBase(jvmCallLogData, linkPosDatum.endTime - linkPosDatum.startTime, new ArrayList<>(1));
                linkStart.add(data);
                linkStart = data;
            }else {
                linkStart = linkStart1;
            }
        }
        return linkStart;
    }

    private boolean link() {
        if (!linkCopy())return false;
        Long2ObjectMap<ArrayList<LinkPosData>> linkBuf = new Long2ObjectNodeHashMap<>();
        for (RelationshipSave relationshipSave : src.relationshipSave) {
            if (relationshipSave.emptyTag) {
                continue;
            }
            ArrayList<LinkPosData> linkPosData = linkBuf.computeIfAbsent(relationshipSave.trackId, k -> new ArrayList<>());
            linkPosData.add(new LinkPosData(relationshipSave.trackTime, relationshipSave.startTime, relationshipSave.stopTime, relationshipSave.tag, relationshipSave.data.stream().filter(v -> v.threadId != -1).toList()));
        }

        linkTree(linkBuf, 0, Long.MAX_VALUE, linkThreadId, link.start);

        return true;
    }
    private boolean linkCopy() {
        for (RelationshipSave relationshipSave : src.relationshipSave) {
            for (NodeSave datum : relationshipSave.data) {
                if (datum.threadId == linkThreadId) {
                    if (link == null) {
                        link = datum.copy();
                    }else {
                        link.start.sum(datum.start.copy());
                    }
                }
            }
        }
        return link != null;
    }

    public SparkProtos.PlatformStatistics savePlatformStatistics() {
        return SparkProtos.PlatformStatistics.newBuilder()
                .setMemory(saveMemory())
                .setUptime(src.runTime)
                .build();
    }

    private SparkProtos.PlatformStatistics.Memory saveMemory() {
        return SparkProtos.PlatformStatistics.Memory.newBuilder()
                .setHeap(saveHeap())
                .build();
    }

    private SparkProtos.PlatformStatistics.Memory.MemoryPool saveHeap() {
        MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        return SparkProtos.PlatformStatistics.Memory.MemoryPool.newBuilder()
                .setUsed(memoryUsage.getUsed())
                .setTotal(memoryUsage.getCommitted())
                .build();
    }

    public SparkSamplerProtos.SamplerMetadata saveMetadata() {
        return SparkSamplerProtos.SamplerMetadata.newBuilder()
                //.setComment(null)
                .setNumberOfTicks(src.totalTicks)
                .setCreator(SparkProtos.CommandSenderMetadata.newBuilder()
                        .setType(SparkProtos.CommandSenderMetadata.Type.OTHER)
                        .setName(Base.MOD_ID)
                        .build())
                .setSystemStatistics(saveSystemStatistics())
                .setStartTime(src.startTime)
                .setInterval((int)src.interval_ms())
                .setThreadDumper(SparkSamplerProtos.SamplerMetadata.ThreadDumper.newBuilder()
                        .addAllIds(Collections::emptyIterator)
                        .setType(SparkSamplerProtos.SamplerMetadata.ThreadDumper.Type.ALL)
                        .build())
                .setDataAggregator(SparkSamplerProtos.SamplerMetadata.DataAggregator.newBuilder()
                        .setType(SparkSamplerProtos.SamplerMetadata.DataAggregator.Type.SIMPLE)
                        .setThreadGrouper(SparkSamplerProtos.SamplerMetadata.DataAggregator.ThreadGrouper.AS_ONE)
                        .build())
                .setPlatformStatistics(savePlatformStatistics())
                .setPlatformMetadata(SparkProtos.PlatformMetadata.newBuilder()
                        .setType(SparkProtos.PlatformMetadata.Type.SERVER)
                        .setName(Base.MOD_ID)
                        .setVersion("1")
                        .setSparkVersion(PlatformInfo.DATA_VERSION))
                .build();
    }

    private SparkProtos.SystemStatistics saveSystemStatistics() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        long uptime = runtimeBean.getUptime();
        SparkProtos.SystemStatistics.Builder builder = SparkProtos.SystemStatistics.newBuilder()
                .setCpu(SparkProtos.SystemStatistics.Cpu.newBuilder()
                        .setThreads(Runtime.getRuntime().availableProcessors())
                        .setProcessUsage(SparkProtos.SystemStatistics.Cpu.Usage.newBuilder()
                                .setLast1M(CpuMonitor.processLoad1MinAvg())
                                .setLast15M(CpuMonitor.processLoad15MinAvg())
                                .build()
                        )
                        .setSystemUsage(SparkProtos.SystemStatistics.Cpu.Usage.newBuilder()
                                .setLast1M(CpuMonitor.systemLoad1MinAvg())
                                .setLast15M(CpuMonitor.systemLoad15MinAvg())
                                .build()
                        )
                        .build()
                )
                .setMemory(SparkProtos.SystemStatistics.Memory.newBuilder()
                        .setPhysical(SparkProtos.SystemStatistics.Memory.MemoryPool.newBuilder()
                                .setUsed(MemoryInfo.getUsedPhysicalMemory())
                                .setTotal(MemoryInfo.getTotalPhysicalMemory())
                                .build()
                        )
                        .setSwap(SparkProtos.SystemStatistics.Memory.MemoryPool.newBuilder()
                                .setUsed(MemoryInfo.getUsedSwap())
                                .setTotal(MemoryInfo.getTotalSwap())
                                .build()
                        )
                        .build()
                )
                .setDisk(SparkProtos.SystemStatistics.Disk.newBuilder()
                        .setTotal(DiskUsage.getTotal())
                        .setUsed(DiskUsage.getUsed())
                        .build()
                )
                .setOs(SparkProtos.SystemStatistics.Os.newBuilder()
                        .setArch(System.getProperty("os.arch"))
                        .setName(System.getProperty("os.name"))
                        .setVersion(System.getProperty("os.version"))
                        .build()
                )
                .setJava(SparkProtos.SystemStatistics.Java.newBuilder()
                        .setVendor(System.getProperty("java.vendor", "unknown"))
                        .setVersion(System.getProperty("java.version", "unknown"))
                        .setVendorVersion(System.getProperty("java.vendor.version", "unknown"))
                        .setVmArgs(String.join(" ", runtimeBean.getInputArguments()))
                        .build())
                .setUptime(uptime);

        Map<String, GarbageCollectorStatistics> gcStats = GarbageCollectorStatistics.pollStats();
        gcStats.forEach((name, statistics) -> builder.putGc(
                name,
                SparkProtos.SystemStatistics.Gc.newBuilder()
                        .setTotal(statistics.getCollectionCount())
                        .setAvgTime(statistics.getAverageCollectionTime())
                        .setAvgFrequency(statistics.getAverageCollectionFrequency(uptime))
                        .build()
        ));

        return builder
                .build();
    }

    public SparkSamplerProtos.StackTraceNode saveBase2(DataBase dataBase, Iterable<Integer> childrenRefs, long startTime) {
        SparkSamplerProtos.StackTraceNode.Builder proto = SparkSamplerProtos.StackTraceNode.newBuilder()
                .setClassName(dataBase.data.declaringClass)
                .setMethodName(dataBase.data.method);
        {
            long start = startTime;
            for (long l = dataBase.time / src.interval; l > 0; l--) {
                proto.addTimes((double) start / 1000000);
                start += src.interval;
            }
        }

        if (dataBase.data.lineNumber >= 0) {
            proto.setLineNumber(dataBase.data.lineNumber);
        }

        if (dataBase.data.parentLineNumber >= 0) {
            proto.setParentLineNumber(dataBase.data.parentLineNumber);
        }


        proto.addAllChildrenRefs(childrenRefs);
        return proto.build();

    }
    public SparkSamplerProtos.ThreadNode saveTree2(String name, NodeSave node) {
        SparkSamplerProtos.ThreadNode.Builder proto = SparkSamplerProtos.ThreadNode.newBuilder()
                .setName(name == null ? node.threadName : name);
        //添加假的times
        {
            long start = node.startTime;
            for (long l = node.time / src.interval; l > 0; l--) {
                proto.addTimes((double) start / 1000000);
                start += src.interval;
            }
        }
        IndexedListBuilder<SparkSamplerProtos.StackTraceNode> nodesArray = new IndexedListBuilder();

        Deque<Node> stack = new ArrayDeque<>();
        List<Integer> childrenRefs = new LinkedList<>();

        for(DataBase child : node.start.next) {
            stack.push(new Node(child, childrenRefs));
        }

        while(!stack.isEmpty()) {
            Node node2 = stack.peek();
            if (node2.firstVisit) {
                for (DataBase child : node2.stackTraceNode.next) {
                    stack.push(new Node(child, node2.childrenRefs));
                }

                node2.firstVisit = false;
            } else {

                SparkSamplerProtos.StackTraceNode childProto = saveBase2(node2.stackTraceNode, node2.childrenRefs, node.startTime);
                int childIndex = nodesArray.add(childProto);
                node2.parentChildrenRefs.add(childIndex);
                stack.pop();
            }
        }

        proto.addAllChildrenRefs(childrenRefs);
        proto.addAllChildren(nodesArray.build());

        return proto.build();
    }

    public SparkSamplerProtos.SamplerData save(boolean all) {
        //ClassSourceLookup.Visitor visitor = ClassSourceLookup.createVisitor(CLASS_SOURCE_LOOKUP);
        //if (visitor.hasMappings()) {
        //    proto.putAllClassSources(visitor.getMapping());
        //}

        if(!link()) {
            List<String> list = new ArrayList<>();
            for (RelationshipSave relationshipSave : src.relationshipSave) {
                for (NodeSave datum : relationshipSave.data) {
                    String e = datum.threadName + " " + datum.threadId + "\n";
                    if (!list.contains(e)) {
                        list.add(e);
                    }
                }
            }
            throw new RuntimeException("link failed: main thread id[%s], all: [%s]".formatted(linkThreadId, list));
        }
        //createSum();//sum会炸火花我不知道为什么超级恶心
        //window在时间很短的情况会增加很大的开销所以没有addTimeWindows
        SparkSamplerProtos.SamplerData.Builder builder = SparkSamplerProtos.SamplerData.newBuilder()
                .setMetadata(saveMetadata())
                .putAllTimeWindowStatistics(Map.of());

        builder.addThreads(saveTree2("kmt-link", link));
        //builder.addThreads(saveTree2("kmt-sum", sum));
        if (all) {
            for (RelationshipSave relationshipSave : src.relationshipSave) {
                for (NodeSave datum : relationshipSave.data) {
                    builder.addThreads(saveTree2("[%s] -> [%s]".formatted(relationshipSave.track, datum.threadName), link));

                }

            }
        }

        return builder
                .build();
    }
    private static final class Node {
        private final DataBase stackTraceNode;
        private boolean firstVisit;
        private final List<Integer> childrenRefs;
        private final List<Integer> parentChildrenRefs;

        private Node(DataBase node, List<Integer> parentChildrenRefs) {
            this.firstVisit = true;
            this.childrenRefs = new LinkedList<>();
            this.stackTraceNode = node;
            this.parentChildrenRefs = parentChildrenRefs;
        }
    }

    public static final class LinkPosData {
        private final long trackTime;
        private final long startTime;
        private final long endTime;
        private final List<JvmCallLogData> pos;
        private final List<NodeSave> data;
        public Object link = null;
        public boolean runLink = false;

        public LinkPosData(long trackTime, long startTime, long endTime, List<JvmCallLogData> pos, List<NodeSave> data) {
            this.trackTime = trackTime;
            this.startTime = startTime;
            this.endTime = endTime;
            this.pos = pos;
            this.data = data;
        }

        public long startTime() {
            return startTime;
        }

        public long endTime() {
            return endTime;
        }

        public List<JvmCallLogData> pos() {
            return pos;
        }

        public List<NodeSave> data() {
            return data;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (LinkPosData) obj;
            return this.startTime == that.startTime &&
                    this.endTime == that.endTime &&
                    Objects.equals(this.pos, that.pos) &&
                    Objects.equals(this.data, that.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startTime, endTime, pos, data);
        }

        @Override
        public String toString() {
            return "LinkPosData[" +
                    "startTime=" + startTime + ", " +
                    "endTime=" + endTime + ", " +
                    "pos=" + pos + ", " +
                    "data=" + data + ']';
        }
    }
    public static class Command implements com.mojang.brigadier.Command<CommandSourceStack> {

        @Override
        public int run(CommandContext<CommandSourceStack> v) throws CommandSyntaxException {
            String fileName = StringArgumentType.getString(v, "fileName");
            Util.ioPool().execute(() -> {
                File file = new File(fileName + ".sparkprofile");
                if (file.isDirectory()) {
                    v.getSource().sendSystemMessage(Component.literal("有一个跟文件名一样的目录|" + fileName + ".sparkprofile"));
                    return;
                }
                if (file.isFile()) {
                    v.getSource().sendSystemMessage(Component.literal("有一个同名文件，会替换掉他|" + fileName + ".sparkprofile"));
                }
                v.getSource().sendSystemMessage(Component.literal("正在生成"));
                LogRoot stop = DebugLog.stop();
                v.getSource().sendSystemMessage(Component.literal("正在保存"));
                SparkSamplerProtos.SamplerData all = new SparkSave(stop, v.getSource().getServer().getRunningThread().getId()).save(BoolArgumentType.getBool(v, "spark_all"));
                v.getSource().sendSystemMessage(Component.literal("正在写入文件"));
                v.getSource().sendSystemMessage(Component.literal("生成的文件需要到火花官网读取"));
                try {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(all.toByteArray());
                    fileOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                v.getSource().sendSystemMessage(Component.literal("写入完成"));
            });
            return 1;
        }
    }
    public static class CommandAll implements com.mojang.brigadier.Command<CommandSourceStack> {

        @Override
        public int run(CommandContext<CommandSourceStack> v) throws CommandSyntaxException {
            String fileName = StringArgumentType.getString(v, "fileName");
            Util.ioPool().execute(() -> {
                File file = new File(fileName + ".sparkprofile");
                File file2 = new File(fileName + ".json");
                if (file2.isDirectory()) {
                    v.getSource().sendSystemMessage(Component.literal("有一个跟文件名一样的目录|" + fileName + ".json"));
                    return;
                }
                if (file2.isFile()) {
                    v.getSource().sendSystemMessage(Component.literal("有一个同名文件，会替换掉他|" + fileName + ".json"));
                }
                if (file.isDirectory()) {
                    v.getSource().sendSystemMessage(Component.literal("有一个跟文件名一样的目录|" + fileName + ".sparkprofile"));
                    return;
                }
                if (file.isFile()) {
                    v.getSource().sendSystemMessage(Component.literal("有一个同名文件，会替换掉他|" + fileName + ".sparkprofile"));
                }
                v.getSource().sendSystemMessage(Component.literal("正在生成"));
                LogRoot stop = DebugLog.stop();
                v.getSource().sendSystemMessage(Component.literal("正在保存"));
                String json = GSON.toJson(stop);
                SparkSamplerProtos.SamplerData all = new SparkSave(stop, v.getSource().getServer().getRunningThread().getId()).save(BoolArgumentType.getBool(v, "spark_all"));
                v.getSource().sendSystemMessage(Component.literal("正在写入文件"));
                v.getSource().sendSystemMessage(Component.literal("生成的文件需要到火花官网读取"));
                try {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(all.toByteArray());
                    fileOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    file2.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file2);
                    fileOutputStream.write(json.getBytes());
                    fileOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                v.getSource().sendSystemMessage(Component.literal("写入完成"));
            });
            return 1;
        }
    }
}
