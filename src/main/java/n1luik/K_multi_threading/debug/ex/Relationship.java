package n1luik.K_multi_threading.debug.ex;

import com.mojang.logging.LogUtils;
import n1luik.K_multi_threading.debug.ex.data.DataBase;
import n1luik.K_multi_threading.debug.ex.data.JvmCallLogData;
import n1luik.K_multi_threading.debug.ex.data.NodeSave;
import n1luik.K_multi_threading.debug.ex.data.RelationshipSave;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

public class Relationship implements BooleanSupplier {
    public static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    static final Logger LOGGER = LogUtils.getLogger();
    public long track = 0;
    public String trackName = null;
    public long trackTime = 0;
    public volatile long stop = 0;
    public volatile ThreadInfo tag = null;
    public @Nullable Node[] nodes = null;
    public @Nullable Node[] nodeCall = null;


    public void initNode(int size){
        nodes = new Node[size];
        for (int i = 0; i < size; i++) {
            nodes[i] = new Node();
        }
        nodeCall = nodes.clone();
    }

    public boolean isStop(){
        if (nodeCall != null) {
            return stop >= nodeCall.length;
        }
        return false;
    }

    public void setTag(){
        tag = threadMXBean.getThreadInfo(Thread.currentThread().getId(), Integer.MAX_VALUE);
    }

    public RelationshipSave save(){
        RelationshipSave relationshipSave = new RelationshipSave();
        relationshipSave.track = trackName;
        relationshipSave.stopTime = stop;
        relationshipSave.startTime = trackTime;
        saveTrack(relationshipSave);
        if (nodes != null) {
            List<NodeSave> nodeSaves = new ArrayList<>(nodes.length);
            for (Node node : nodes) {
                if (node == null) continue;
                nodeSaves.add(node.save());
            }
            relationshipSave.data = nodeSaves;
        }else {
            relationshipSave.data = List.of();
        }

        return relationshipSave;
    }

    public void saveTrack(RelationshipSave save){
        if (tag == null) {
            save.tag = null;
            save.emptyTag = true;
            save.track = "not found";
            save.trackId = -1;
            save.trackTime = -1;
            return;
        }
        save.track = trackName;
        save.trackId = track;
        save.trackTime = trackTime;
        StackTraceElement[] stackTrace = tag.getStackTrace();
        List<JvmCallLogData> out = new ArrayList<>(stackTrace.length);
        //System.out.println(Arrays.toString(stackTrace));
        for (int i = stackTrace.length - 1; i >= 0; i--) {
            StackTraceElement stackTraceElement = stackTrace[i];
            if (stackTraceElement.getClassName().equals(Relationship.class.getName())
                    && stackTraceElement.getMethodName().equals("setTag")
            ) break;
            out.add(new JvmCallLogData(stackTraceElement.getClassName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber()));

        }
        save.tag = out;
        save.emptyTag = false;
    }

    public void call(){
        //System.out.println("call");
        if (nodes == null) return;
        if (nodeCall == null) return;
        for (int i = 0; i < nodeCall.length; i++) {
            Node node = nodeCall[i];
            if (node == null)continue;
            if (node.thread == null)continue;
            if (node.stop) {
                stop++;
                nodeCall[i] = null;
                continue;
            }
            node.tick();
        }
    }

    @Override
    public boolean getAsBoolean() {
        call();
        return isStop();
    }

    public void stop() {
        if (nodeCall == null) return;
        for (Node node : nodeCall) {
            if (node == null) continue;
            node.testStop();
        }
    }

    public static class Node {
        public volatile Thread thread;
        public volatile long threadId = -1;
        public String threadNane;
        public volatile boolean stop = false;
        public volatile long startTime = -1;
        public volatile long endTime = -1;
        public volatile long time = 0;
        public final StackTraceNode root = new StackTraceNode();

        public Node(Thread thread, long startTime) {
            this.thread = thread;
            this.threadNane = thread.getName();
            this.startTime = startTime;
        }
        public Node() {
        }

        public void stop(){
            this.stop = true;
            endTime = System.nanoTime();
        }

        public NodeSave save(){
            DataBase save = root.save();
            return new NodeSave(save, threadNane == null ? "" : threadNane, time, startTime, endTime, threadId);
        }
        
        public void start(Thread thread){
            startTime = System.nanoTime();
            this.threadNane = thread.getName();
            threadId = thread.getId();
            this.thread = thread;
            
        }

        public void tick() {
            if (thread != null) {
                ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId, Integer.MAX_VALUE);
                if (threadInfo != null) {
                    try {
                        StackTraceElement[] stack = threadInfo.getStackTrace();
                        if (stack.length != 0) {
                            time += DebugLog.interval;
                            StackTraceNode node = root;
                            //StackTraceElement previousElement = null;

                            for(int offset = 0; offset < stack.length; ++offset) {
                                StackTraceElement element = stack[stack.length - 1 - offset];
                                node = node.resolveChild(new StackTraceNode.Description(element.getClassName(), element.getMethodName(), element.getLineNumber(), -1));//这样会出现跟多毛刺根本没办法正常看//previousElement == null ? -1 : previousElement.getLineNumber()));
                                node.time += DebugLog.interval;
                                //previousElement = element;
                            }

                        }
                    } catch (Exception var4) {
                        LOGGER.error("", var4);
                    }

                }
            }
        }


        public void testStop() {
            if (thread != null && !stop) {
                stop();
            }
        }
    }
}
 