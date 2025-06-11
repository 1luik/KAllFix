package n1luik.K_multi_threading.debug.ex.data;

import java.util.List;

public class NodeSave {
    public DataBase start;
    public String threadName;
    public long time;
    public long startTime;
    public long endTime;
    public long threadId;

    public NodeSave() {

    }

    public NodeSave(DataBase start, String threadName, long time, long startTime, long endTime, long threadId) {
        this.start = start;
        this.threadName = threadName;
        this.time = time;
        this.startTime = startTime;
        this.endTime = endTime;
        this.threadId = threadId;
    }

    public NodeSave copy() {
        return new NodeSave(start.copy(), threadName, time, startTime, endTime, threadId);
    }
}
