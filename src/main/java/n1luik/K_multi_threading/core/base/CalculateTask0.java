package n1luik.K_multi_threading.core.base;

import lombok.Getter;
import n1luik.K_multi_threading.core.util.Unsafe;

import javax.annotation.Nullable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveTask;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CalculateTask0 extends RecursiveTask<Object> {
    public static int callMax = Runtime.getRuntime().availableProcessors()-1;
    private final static Supplier<String> NullStringSupplier  = () -> "[null]";
    protected final Object sync1 = new Object();//防止在运行完之后等待
    private final int min;
    protected int start;
    protected int end;
    protected BiConsumer<CalculateTask0,Integer> run;
    //protected final ContainsMapList<T> ret;
    protected final int layer;
    protected volatile int size;
    protected volatile boolean stop = false;
    public boolean notCallMode = true;
    public final CalculateTask0 root;//用于实现等待
    @Nullable
    public Thread wait;//用于实现等待
    public volatile Throwable throwable;//等待时的报错
    @Getter
    protected int taskCompleted;//等待时的报错
    public final Supplier<String> name;

    public void waitThread(Thread wait){
        waitThread(t-> {
            throw new RuntimeException("多线程异常", t);
        }, wait);
    }

    public <T> T waitThread(Function<Throwable, T> function, Thread wait){
        synchronized (sync1){
            if (stop || wait instanceof ForkJoinWorkerThread) {
                //this.join();
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
        waitThread(wait);
    }

    public void waitThread(){
        waitThread(Thread.currentThread());
    }

    public <T> T waitThread(Function<Throwable, T> function){
        return waitThread(function, Thread.currentThread());
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
            waitThread(thread);
        }
    }


    public CalculateTask0(int start, int end, int min, Consumer<Integer> run) {
        this(NullStringSupplier, start, end, min, run);
    }
    public CalculateTask0(Supplier<String> name, int start, int end, int min, Consumer<Integer> run) {
        this.start = start;
        this.end = end;
        this.run = (n,i)->run.accept(i);
        this.min = min;
        //this.ret = new ContainsMapList<>(false);
        this.layer = 0;
        this.root = this;
        this.name  = name;
    }
    public CalculateTask0(int start, int end, Consumer<Integer> run) {
        this(NullStringSupplier, start, end, run);
    }
    public CalculateTask0(Supplier<String> name, int start, int end, Consumer<Integer> run) {
        this(name, start,end,1+((end-start) / callMax),run);
    }

    public CalculateTask0(int start, int end, int max, BiConsumer<CalculateTask0,Integer> run) {
        this(NullStringSupplier, start, end, max, run);
    }

    public CalculateTask0(Supplier<String> name, int start, int end, int max, BiConsumer<CalculateTask0,Integer> run) {
        this.start = start;
        this.end = end;
        this.run = run;
        this.min = max;
        //this.ret = new ContainsMapList<>((end-start)+1);
        this.layer = 0;
        this.root = this;
        this.name  = name;
    }

    public CalculateTask0(int start, int end, int min, BiConsumer<CalculateTask0,Integer> run/*, final ContainsMapList<T> ret*/, int layer, CalculateTask0 root) {
        this(NullStringSupplier, start,end,min,run,layer,root);
    }
    public CalculateTask0(Supplier<String> name, int start, int end, int min, BiConsumer<CalculateTask0,Integer> run/*, final ContainsMapList<T> ret*/, int layer, CalculateTask0 root) {
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
    protected Object compute() {
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
                    boolean redundancy = (end - start) % min != 0;
                    int middle_i = (end - start) / min;
                    RecursiveTask<?>[] callNodes = new RecursiveTask[redundancy ? middle_i + 1 : middle_i];

                    int all = 0;
                    int rem = (end - start);
                    double taskSize = (min * ((end - start) / (double)min)) / ((end - start) / (double)min);
                    for (int i = 0; i < callNodes.length; i++) {
                        //if (redundancy && i == callNodes.length - 1) {
                        //    all += (start + (i * min)) - (end - start);
                        //    callNodes[i] = new CallNode(start + (int)all, (start + (i * min)) - (end - start));
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
                    //System.out.printf("%s %s %s %s %s %n",taskSize,taskSize * callNodes.length,all,callNodes.length,(end - start));

                    for (int i = 1; i < callNodes.length; i++) {
                        callNodes[i].fork();
                    }
                    ((CallNode)callNodes[0]).compute();
                    for (int i = 1; i < callNodes.length; i++) {
                        callNodes[i].join();
                    }
                    //while (size > 0) Thread.onSpinWait();


                }else {
                    int middle = (start + end) / 2;
                    CalculateTask0 firstTask = new CalculateTask0(name, start, middle, min, run/*, ret*/, this.layer,root);
                    CalculateTask0 secondTask = new CalculateTask0(name, middle, end, min, run/*, ret*/, this.layer,root);
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
                if (wait != null && !(wait instanceof ForkJoinWorkerThread))
                    Unsafe.unsafe.unpark(wait);
            }

        }

        return sync1;
    }

    @Override
    public String toString() {
        return name+"-['Task size': "+(end - start)+", 'taskCompleted': "+taskCompleted+", 'hash': "+super.toString()+"]";
    }

    public class CallNode extends RecursiveTask<Object>{

        protected int pos = -1;
        protected int min = 0;

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
            try {
                int max = (end - start);
                for (int i = 0; i < min && max > i; i++) {
                    run.accept(CalculateTask0.this, pos + i);
                    CalculateTask0.this.taskCompleted++;
                    //ret.put(pos + i, apply);
                }
                //synchronized (CalculateTask.this) {
                //    size--;
                //}
            } catch (Throwable e) {
                root.throwable = throwable = e;
                throw e;
            }
            return null;
        }
    }
}