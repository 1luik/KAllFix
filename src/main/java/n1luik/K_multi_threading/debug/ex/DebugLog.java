package n1luik.K_multi_threading.debug.ex;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import n1luik.K_multi_threading.core.util.Unsafe;
import n1luik.K_multi_threading.debug.ex.data.LogRoot;
import n1luik.K_multi_threading.debug.ex.data.RelationshipSave;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

public class DebugLog {
    public volatile static boolean debug = false;
    public volatile static long startDebugTime = 0;
    public volatile static long interval = 0;//纳秒
    public static final ConcurrentLinkedQueue<Relationship> relationships = new ConcurrentLinkedQueue<>();
    public static final DebugLog caller = new DebugLog();
    //protected static final TaskRun addTash = new TaskRun("DebugLog.addTask");

    protected final AtomicInteger runSize = new AtomicInteger(0);
    protected volatile boolean run = false;
    protected volatile boolean createNode = false;
    protected volatile boolean createNode2 = false;
    protected volatile int taskSize = 0;
    protected volatile int threadSize = 0;
    protected volatile BooleanSupplier[] tasks = new BooleanSupplier[0];
    //protected volatile boolean[] data = new boolean[0];
    //protected volatile BooleanSupplier[] taskUp = null;
    //protected final Int2ObjectArrayMap<ThreadNode> runThreadNodes = new Int2ObjectArrayMap<>();
    protected final List<ThreadNode> nodes = new ArrayList<>();
    //protected volatile Queue<Integer> emptyId = new ConcurrentLinkedQueue<>();
    protected volatile Queue<ThreadNode> stopNode = new ArrayDeque<>();
    protected volatile List<ThreadNode> runNode = new ArrayList<>();
    protected final AtomicLong runTime = new AtomicLong(0);
    //protected final Object lockCreate = new Object();
    //protected final ReentrantLock lock = new ReentrantLock();
    //protected final Condition condition = lock.newCondition();
    //protected final List<ThreadNode> nodes = new CopyOnWriteArrayList<>();
    //protected final Queue<ThreadNode> stopNode = new ConcurrentLinkedQueue<>();


    static  {
        //addTash.start();
    }

    //public void start(Function<ThreadNode, Runnable> task){
    //    ThreadNode node;
    //    try {
    //        node = stopNode.remove();
    //    } catch (NoSuchElementException e) {
    //        node = new ThreadNode();
    //        node.start();
    //    }
    //    node.restart(task.apply(node));
    //}


    public static void start(long interval) {
        startDebugTime = System.currentTimeMillis();
        relationships.clear();
        DebugLog.interval = interval;
        caller.restart();
        debug = true;

    }

    public static LogRoot stop() {
        debug = false;
        caller.stopAll();
        relationships.forEach(Relationship::stop);
        LogRoot ret = save();
        relationships.clear();
        return ret;
    }

    //private void stopAll() {
    //    caller.run = false;
    //    stopNode.clear();
    //    stopNode.addAll(nodes);
    //}

    public static LogRoot save() {
        if (debug) throw new RuntimeException("DebugLog is running");
        LogRoot logRoot = new LogRoot();
        ArrayList<RelationshipSave> relationshipSave = new ArrayList<>(relationships.size());
        logRoot.relationshipSave = relationshipSave;
        for (Relationship relationship : relationships) {
            relationshipSave.add(relationship.save());
        }
        logRoot.runTime = System.currentTimeMillis() - startDebugTime;
        logRoot.startTime = startDebugTime;
        logRoot.interval = interval;
        logRoot.totalTicks = 1;
        return logRoot;
    }

    public static void add(Relationship relationship) {
        //addTash.execute(()-> {
        if (debug) {
            relationships.add(relationship);
            synchronized (caller) {
                if (debug) {
                    caller.addTask(relationship);
                    //caller.start(v -> () -> {
                    //    if (relationship.getAsBoolean()) {
                    //        v.stopTask();
                    //    }
                    //});
                }
            }
        }
        //});
    }

    protected synchronized void startNode() {
        ThreadNode node;
        if (stopNode.isEmpty()) {
            node = new ThreadNode();
            nodes.add(node);
            node.initTask = ()->threadSize++;
            node.init(threadSize);
            node.start();
            runNode.add(node);
        }else {
            node = stopNode.remove();
            node.initTask = ()->threadSize++;
            node.init(threadSize);
            runNode.add(node);
        }
    }
    protected boolean manageTask() {
        if (createNode2){
            createNode2 = false;
            createNode = false;
            if (threadSize < taskSize) {
                if ((((double) runTime.get()) / (double) taskSize) > (interval * 0.7)) {
                    createNode2 = false;
                    startNode();

                }
            }
        } else if (createNode) {
            createNode = false;
            if (threadSize < taskSize) {
                if ((((double) runTime.get()) / (double) taskSize) > (interval * 0.7)) {
                    createNode2 = false;
                    startNode();

                }
            }
        }else {
            if (threadSize > 1) {
                if ((((double) runTime.get()) / (double) taskSize) < (interval * 0.7)) {
                    stopNode();
                }
            }
        }
        runTime.set(0);
        return false;
    }

    protected synchronized void stopNode() {
        ThreadNode remove = runNode.remove(runNode.size() - 1);
        remove.runThis = false;
        threadSize--;
        stopNode.add(remove);
    }

    private synchronized void remove(int id) {
        //emptyId.add(id);
        BooleanSupplier task = tasks[taskSize - 1];
        tasks[taskSize-1] = null;
        tasks[id] = task;
        taskSize--;
        //runThreadNodes.remove(id);
    }

    protected DebugLog() {
    }
    public synchronized void addTask(BooleanSupplier task) {
        if (run) {
            if (tasks.length == taskSize) {
                tasks = Arrays.copyOf(tasks, (int) (tasks.length * 1.06) + 1);
                tasks[taskSize] = task;
                taskSize++;
            }else {
                tasks[taskSize] = task;
                taskSize++;
            }
        }
    }

    private synchronized void restart() {
        stopAll();
        taskSize = 1;
        tasks = new BooleanSupplier[128];
        tasks[0] = this::manageTask;
        threadSize = 0;
        //data = new boolean[0];
        //taskUp = null;
        run = true;
        //init()
        startNode();
    }
    protected void stopAll() {
        run = false;
        threadSize = 0;
        runSize.set(0);
        Arrays.fill(tasks, null);
        runNode.clear();
        stopNode.clear();
        stopNode.addAll(nodes);
    }

    /**
     * 多线程运行tasks的任务的基础单位
     * <p>
     * pos是运行的任务数量offset是获取任务的偏移位置
     */
    public class ThreadNode extends Thread{
        public volatile boolean exit = false;
        public volatile boolean runThis = false;
        public volatile boolean use = false;
        //private volatile boolean test = false;
        private volatile int pos = 0;
        private volatile int offset = -1;//运行任务的偏移位置
        private volatile long useTime = 0;//本次运行的时间
        private volatile Runnable initTask = null;//本次运行的时间
        public ThreadNode() {
            super("KMT-Debug-Thread");
        }

        @Override
        public void run() {
            while (!exit){
                if (initTask != null) {
                    initTask.run();
                    initTask = null;
                }

                if (run && runThis) {
                    long l = System.nanoTime();
                    //if (test) {
                    //    //测试是否是单个任务超过interval的时间如果是就标记
                    //    test = false;
                    //    boolean nn = true;
                    //    while (true) {
                    //        int id = pos * offset;
                    //        if (id > taskSize) {
                    //            pos = 1;
                    //            break;
                    //        }
                    //        BooleanSupplier task = tasks[id];
                    //        if (task != null) {
                    //            long l1 = System.nanoTime();
                    //            if (task.getAsBoolean()) {
                    //                tasks[id] = null;
                    //                emptyId.add(id);
                    //            }else {
                    //                if (System.nanoTime() - l1 > interval) {
                    //                    nn = false;
//
                    //                 }
                    //            }
                    //        }
//
                    //    }
                    //    if  (nn) {
                    //        createNode = true;
                    //    }
//
                    //}else {
                    while (true) {
                        int id = ((pos++) * threadSize) + offset;
                        if (id >= taskSize) {
                            pos = 0;
                            break;
                        }
                        BooleanSupplier task = tasks[id];
                        if (task != null) {
                            if (task.getAsBoolean()) {
                                remove(id);
                            }
                        }
                    }
                    //}
                    long now = System.nanoTime();
                    long l1 = (now - l);
                    useTime = l1;
                    runTime.getAndAdd(l1);
                    if (l1 < interval) {
                        Unsafe.unsafe.park(false, interval - l1);
                    }else {
                        if (l1 > interval * 1.2) {
                            createNode2 = true;
                        }
                        createNode = true;
                    }

                }else {
                    use = false;
                    Unsafe.unsafe.park(false, 0);
                }
            }
        }

        public void init(int threadSize) {
            pos = 0;
            offset = threadSize;
            runThis = true;
            Unsafe.unsafe.unpark(this);
        }
    }
    //public class ThreadNode extends Thread{
    //    public volatile boolean exit = false;
    //    public volatile Runnable task = null;
    //    //protected final ReentrantLock stopLock = new ReentrantLock();
    //    public ThreadNode() {
    //        super("KMT-Debug-Thread");
    //    }
//
//
//
    //    @Override
    //    public void run() {
    //        while (!exit){
//
    //            if (run && task != null) {
    //                long l = System.nanoTime();
    //                if (task != null) {
    //                    task.run();
    //                }
    //                long l1 = interval - (System.nanoTime() - l);
    //                if (l1 < 0) {
    //                    Unsafe.unsafe.park(false, l1);
    //                }
//
    //            }else {
    //                if (!run) {
    //                    task = null;
    //                }
    //                //stopLock.lock();
    //                Unsafe.unsafe.park(false, 0);
    //                //stopLock.unlock();
    //            }
    //        }
    //    }
//
    //    public void restart(Runnable apply) {
    //        task = apply;
    //        Unsafe.unsafe.unpark(this);
    //    }
    //
    //    public void stopTask() {
    //        task = null;
    //        stopNode.add(this);
    //    }
    //}

}
