package n1luik.KAllFix.util;

import n1luik.K_multi_threading.core.base.CalculateTask;

public class Args {
    public static final int JEITaskMax = Integer.getInteger("KAF-JeiMultiThreading-TasxMax", CalculateTask.callMax);
    public static final int PlainTextSearchTreeTaskMax = Integer.getInteger("KAF-PlainTextSearchTreeMultiThreading-TasxMax", CalculateTask.callMax);
}
