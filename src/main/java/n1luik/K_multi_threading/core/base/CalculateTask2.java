package n1luik.K_multi_threading.core.base;

import lombok.Getter;
import n1luik.K_multi_threading.core.util.Unsafe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CalculateTask2 extends RecursiveTask<Object> {
    public static int callMax = Runtime.getRuntime().availableProcessors()-1;
    private static final Logger LOGGER = LogManager.getLogger(CalculateTask2.class);
    private final static Supplier<String> NullStringSupplier  = () -> "[null]";
    protected final Object sync1 = new Object();//防止在运行完之后等待
    private final int min;
    protected int start;
    protected int end;
    protected BiConsumer<CalculateTask2,Integer> run;
    //protected final ContainsMapList<T> ret;
    protected final int layer;
    protected volatile int size;
    protected volatile boolean stop = false;
    public boolean notCallMode = true;
    public final CalculateTask2 root;//用于实现等待
    @Nullable
    public Thread wait;//用于实现等待
    public volatile Throwable throwable;//等待时的报错
    @Getter
    protected final AtomicInteger nodeCompleted = new AtomicInteger(0);//等待时的报错
    public final Supplier<String> name;
    protected final Object errorLock = new Object();

    public void unsafeWaitThread(Thread wait){
        unsafeWaitThread(t-> {
            throw new RuntimeException("多线程异常", t);
        }, wait);
    }
    public void waitThread(Thread wait){
        waitThread(t-> {
            throw new RuntimeException("多线程异常", t);
        }, wait);
    }

    public <T> T waitThread(Function<Throwable, T> function, Thread wait){
        if (wait instanceof ForkJoinWorkerThread)
            synchronized (sync1){
                if (this.stop) {
                    return null;
                }
                join();
            }
        return unsafeWaitThread(function, wait);
    }
    public <T> T unsafeWaitThread(Function<Throwable, T> function, Thread wait){
        synchronized (sync1){
            if (stop || wait instanceof ForkJoinWorkerThread) {
                return null;
            }
            this.wait = wait;
        }

        while (!stop) Unsafe.unsafe.park(false, 0L);
        if (throwable != null && function != null)return function.apply(throwable);
        if (!stop) throw new RuntimeException("等待线程异常");
        return null;
    }

    public void call(Thread wait){
        if (stop)return;
        fork();
        unsafeWaitThread(wait);
    }

    public void waitThread(){
        waitThread(Thread.currentThread());
    }
    public void unsafeWaitThread(){
        unsafeWaitThread(Thread.currentThread());
    }

    public <T> T waitThread(Function<Throwable, T> function){
        return waitThread(function, Thread.currentThread());
    }

    public <T> T unsafeWaitThread(Function<Throwable, T> function){
        return unsafeWaitThread(function, Thread.currentThread());
    }

    public <T> void call(Function<Throwable, T> function){
        call(Thread.currentThread());
    }

    public <T> void call(ForkJoinPool pool, Function<Throwable, T> function){
        Thread thread = Thread.currentThread();
        if (thread instanceof ForkJoinWorkerThread){
            compute();
        }else {
            pool.submit(this);
            unsafeWaitThread(function, thread);
        }
    }

    public void call(){
        call(Thread.currentThread());
    }

    public void call(ForkJoinPool pool){
        Thread thread = Thread.currentThread();
        if (thread instanceof ForkJoinWorkerThread){
            compute();
        }else {
            pool.submit(this);
            unsafeWaitThread(thread);
        }
    }


    public CalculateTask2(int start, int end, int min, Consumer<Integer> run) {
        this(NullStringSupplier, start, end, min, run);
    }
    public CalculateTask2(Supplier<String> name, int start, int end, int min, Consumer<Integer> run) {
        this.start = start;
        this.end = end;
        this.run = (n,i)->run.accept(i);
        this.min = min;
        //this.ret = new ContainsMapList<>(false);
        this.layer = 0;
        this.root = this;
        this.name  = name;
    }
    public CalculateTask2(int start, int end, Consumer<Integer> run) {
        this(NullStringSupplier, start, end, run);
    }
    public CalculateTask2(Supplier<String> name, int start, int end, Consumer<Integer> run) {
        this(name, start,end,1+((end-start) / CalculateTask.callMax),run);
    }
    public CalculateTask2(Supplier<String> name, int start, int end, Consumer<Integer> run, int core) {
        this(name, start,end,1+((end-start) / core),run);
    }

    public CalculateTask2(int start, int end, int max, BiConsumer<CalculateTask2,Integer> run) {
        this(NullStringSupplier, start, end, max, run);
    }

    public CalculateTask2(Supplier<String> name, int start, int end, int max, BiConsumer<CalculateTask2,Integer> run) {
        this.start = start;
        this.end = end;
        this.run = run;
        this.min = max;
        //this.ret = new ContainsMapList<>((end-start)+1);
        this.layer = 0;
        this.root = this;
        this.name  = name;
    }

    public CalculateTask2(int start, int end, int min, BiConsumer<CalculateTask2,Integer> run/*, final ContainsMapList<T> ret*/, int layer, CalculateTask2 root) {
        this(NullStringSupplier, start,end,min,run,layer,root);
    }
    public CalculateTask2(Supplier<String> name, int start, int end, int min, BiConsumer<CalculateTask2,Integer> run/*, final ContainsMapList<T> ret*/, int layer, CalculateTask2 root) {
        this.start = start;
        this.end = end;
        this.run = run;
        this.min = min;
        //this.ret = ret;
        this.layer = layer;
        this.root = root;
        this.name  = name;
    }


    @Override
    protected synchronized Object compute() {
        if (stop) throw new RuntimeException("当前任务已运行");
        if (end - start < 1) return sync1;
        try {

            if (end - start <= min) {
                try {
                    for (int i = start; i < end; i++) {
                        run.accept(this, i);
                    }
                }catch (Throwable e){
                    throwable = e;
                    throw e;
                }
            } else {
                if (notCallMode) {
                    int rem = (end - start);
                    boolean redundancy = rem % min != 0;
                    int middle_i = rem / min;
                    CallNode[] callNodes = new CallNode[redundancy ? middle_i + 1 : middle_i];

                    int all = 0;
                    double taskSize = (min * (rem / (double)min)) / ((int)(rem / (double)min));
                    if (taskSize % 1 != 0) {
                        taskSize = (double) ((int)taskSize)+1;
                    }
                    for (int i = 0; i < callNodes.length; i++) {
                        //if (redundancy && i == callNodes.length - 1) {
                        //    all += (start + (i * min)) - rem;
                        //    callNodes[i] = new CallNode(start + (int)all, (start + (i * min)) - rem);
                        //}else {
                        int add = min;
                        if ((taskSize * i) > all){
                            add = (int) ((taskSize * i)+1) - all;
                        }
                        callNodes[i] = new CallNode(start + (int)all, add);
                        all += add;
                        //}
                    }
                    //size = callNodes.length;
                    //System.out.printf("%s %s %s %s %s %n",taskSize,taskSize * callNodes.length, all, callNodes.length, rem);

                    for (int i = 1; i < callNodes.length; i++) {
                        callNodes[i].fork();
                    }
                    (callNodes[0]).compute();
                    for (int i = 1; i < callNodes.length; i++) {
                        if (callNodes[i].stop)continue;
                        callNodes[i].join();
                    }
                    //while (size > 0) Thread.onSpinWait();


                }else {
                    int middle = (start + end) / 2;
                    CalculateTask2 firstTask = new CalculateTask2(name, start, middle, min, run/*, ret*/, this.layer,root);
                    CalculateTask2 secondTask = new CalculateTask2(name, middle, end, min, run/*, ret*/, this.layer,root);
                    //invokeAll(firstTask,secondTask);
                    secondTask.fork();
                    firstTask.compute();
                    secondTask.join();
                    if (secondTask.throwable != null) root.throwable = secondTask.throwable;
                    if (firstTask.throwable != null) root.throwable = firstTask.throwable;
                }

            }
        }finally {
            synchronized (sync1){
                stop = true;
                unWait();
            }

        }

        return sync1;
    }

    protected void unWait(){
        if (wait != null && !(wait instanceof ForkJoinWorkerThread))
            Unsafe.unsafe.unpark(wait);
    }

    @Override
    public String toString() {
        return name+"-['Task size': "+(end - start)+", 'nodeCompleted': "+nodeCompleted.get()+", 'hash': "+super.toString()+"]";
    }

    public class CallNode extends RecursiveTask<Object>{

        protected int pos = -1;
        protected int min = 0;
        protected boolean stop = false;

        public CallNode(){
        }

        public CallNode(int pos,int min){
            this.pos = pos;
            this.min = min;
        }

        public CallNode(int pos){
            this.pos = pos;
        }


        @Override
        protected Object compute() {
            BiConsumer<CalculateTask2, Integer> run1 = run;
            try {
                int max = (end - start);
                for (int i = 0; i < min && max > i; i++) {
                    run1.accept(CalculateTask2.this, pos + i);
                }
                CalculateTask2.this.nodeCompleted.getAndAdd(1);
            } catch (Throwable e) {
                synchronized (CalculateTask2.this.errorLock) {
                    if (root.throwable != null) {
                        LOGGER.error("Error in task: " + CalculateTask2.this.name, e);
                    }else {
                        root.throwable = e;
                    }
                }
                throw e;
            }finally {
                stop = true;
            }
            return null;
        }
    }
}