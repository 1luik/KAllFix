package n1luik.K_multi_threading.debug.ex.data;

import java.util.List;

/**
 * 使用gson保存
 * */
public class LogRoot {
    public List<RelationshipSave> relationshipSave;
    public int totalTicks;
    public long runTime;
    public long startTime;
    public long interval;

    public long interval_ms(){
        if (((double)interval) / 1000000 < 1)return 1;
        return interval / 1000000L;
    }
}
