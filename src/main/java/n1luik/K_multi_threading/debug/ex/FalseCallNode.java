package n1luik.K_multi_threading.debug.ex;

import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.base.CalculateTask;

import java.util.concurrent.RecursiveTask;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FalseCallNode extends RecursiveTask<Object> {
    protected final Consumer<FalseCallNode> startRun;
    protected final Consumer<FalseCallNode> stopRun;
    protected final Object errorLock;
    protected final CalculateTask calculateTask;
    protected final BiConsumer<CalculateTask,Integer> run;
    protected final int start;
    protected final int end;
    protected final Consumer<Throwable> error;
    protected final Supplier<Throwable> errorGet;
    public final int id;
    protected final int pos;// = -1;
    protected final int min;// = 0;
    public boolean stop = false;

    //public CallNode(){
    //}

    public FalseCallNode(Consumer<FalseCallNode> startRun, Consumer<FalseCallNode> stopRun, Object errorLock, CalculateTask calculateTask, BiConsumer<CalculateTask, Integer> run, int start, int end, Consumer<Throwable> error, Supplier<Throwable> errorGet, int id, int pos, int min){
        this.startRun = startRun;
        this.stopRun = stopRun;
        this.errorLock = errorLock;
        this.calculateTask = calculateTask;
        this.run = run;
        this.start = start;
        this.end = end;
        this.error = error;
        this.errorGet = errorGet;
        this.id = id;
        this.pos = pos;
        this.min = min;
    }

    //public CallNode(int pos){
    //    this.pos = pos;
    //}


    @Override
    public Object compute() {
        BiConsumer<CalculateTask, Integer> run1 = run;
        startRun.accept(this);
        try {
            int max = (end - start);
            for (int i = 0; i < min && max > i; i++) {
                run1.accept(calculateTask, pos + i);
            }
            calculateTask.getNodeCompleted().getAndAdd(1);
        } catch (Throwable e) {
            synchronized (errorLock) {
                if (errorGet.get() != null) {
                    Base.LOGGER.error("Error in task: " + calculateTask.name, e);
                }else {
                    error.accept(e);
                }
            }
            throw e;
        }finally {
            stop = true;
            stopRun.accept(this);
        }
        return null;
    }
}