package n1luik.K_multi_threading.debug.mixin.log;

import n1luik.K_multi_threading.core.base.CalculateTask;
import n1luik.K_multi_threading.core.util.OB2;
import n1luik.K_multi_threading.debug.ex.DebugLog;
import n1luik.K_multi_threading.debug.ex.FalseCallNode;
import n1luik.K_multi_threading.debug.ex.Relationship;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveTask;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(value = CalculateTask.class, remap = false)
public abstract class CalculateTaskMixin extends RecursiveTask<Object> {
    @Shadow(remap = false) @Final protected Object sync1;
    @Shadow(remap = false) protected volatile boolean stop;

    @Shadow(remap = false) public abstract <T> T unsafeWaitThread(Function<Throwable, T> function, Thread wait);

    @Shadow(remap = false) protected int start;
    @Shadow(remap = false) protected int end;
    @Shadow(remap = false) @Final private int min = 1;
    @Shadow(remap = false) public volatile Throwable throwable;
    @Shadow(remap = false) protected BiConsumer<CalculateTask, Integer> run;

    @Shadow(remap = false) protected abstract void unWait();

    @Shadow(remap = false) @Final protected Object errorLock;
    @Unique
    public final boolean debug = DebugLog.debug;
    @Unique
    public Relationship relationship;

    @Inject(method = "<init>(Ljava/util/function/Supplier;IIILjava/util/function/BiConsumer;ILn1luik/K_multi_threading/core/base/CalculateTask;)V", at = @At("RETURN"), remap = false)
    private void init(Supplier name, int start, int end, int min, BiConsumer run, int layer, CalculateTask root, CallbackInfo ci) {
        if (debug) {
            relationship = new Relationship();
            DebugLog.add(relationship);
        }
    }

    @Inject(method = "<init>(Ljava/util/function/Supplier;IIILjava/util/function/Consumer;)V", at = @At("RETURN"), remap = false)
    private void init(Supplier name, int start, int end, int min, Consumer run, CallbackInfo ci) {
        if (debug) {
            relationship = new Relationship();
            DebugLog.add(relationship);
        }
    }
    @Inject(method = "<init>(Ljava/util/function/Supplier;IIILjava/util/function/BiConsumer;)V", at = @At("RETURN"), remap = false)
    private void init(Supplier name, int start, int end, int max, BiConsumer run, CallbackInfo ci) {
         if (debug) {
             relationship = new Relationship();
             DebugLog.add(relationship);
         }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public <T> T waitThread(Function<Throwable, T> function, Thread wait){
        if (wait instanceof ForkJoinWorkerThread)
            synchronized (sync1){
                if (this.stop) {
                    if (debug){
                        relationship.track = wait.getId();
                        relationship.trackName = wait.getName();
                        relationship.trackTime = System.nanoTime();
                        relationship.setTag();
                    }
                    return null;
                }
                join();
            }
        return unsafeWaitThread(function, wait);
    }

    @Inject(method = "unsafeWaitThread(Ljava/util/function/Function;Ljava/lang/Thread;)Ljava/lang/Object;", at = @At("HEAD"), remap = false)
    public <T> void impl1(Function<Throwable, T> function, Thread wait, CallbackInfoReturnable<T> cir) {
        if (debug) {
            relationship.track = wait.getId();
            relationship.trackName = wait.getName();
            relationship.trackTime = System.nanoTime();
            relationship.setTag();
        }
    }

    @Inject(method = "call(Ljava/util/concurrent/ForkJoinPool;Ljava/util/function/Function;)V", at = @At(value = "INVOKE", target = "Ln1luik/K_multi_threading/core/base/CalculateTask;compute()Ljava/lang/Object;", remap = false), remap = false)
    public <T> void impl2(ForkJoinPool pool, Function<Throwable, T> function, CallbackInfo ci) {
        if (debug){
            relationship.track = Thread.currentThread().getId();
            relationship.trackName = Thread.currentThread().getName();
            relationship.trackTime = System.nanoTime();
            relationship.setTag();
        }
    }

    @Inject(method = "callAuto()V", at = @At(value = "INVOKE", target = "Ln1luik/K_multi_threading/core/base/CalculateTask;compute()Ljava/lang/Object;", remap = false), remap = false)
    public void impl4(CallbackInfo ci) {
        if (debug){
            relationship.track = Thread.currentThread().getId();
            relationship.trackName = Thread.currentThread().getName();
            relationship.trackTime = System.nanoTime();
            relationship.setTag();
        }
    }

    @Inject(method = "call(Ljava/util/concurrent/ForkJoinPool;)V", at = @At(value = "INVOKE", target = "Ln1luik/K_multi_threading/core/base/CalculateTask;compute()Ljava/lang/Object;", remap = false), remap = false)
    public void impl5(CallbackInfo ci) {
        if (debug){
            relationship.track = Thread.currentThread().getId();
            relationship.trackName = Thread.currentThread().getName();
            relationship.trackTime = System.nanoTime();
            relationship.setTag();
        }
    }


    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    @Override
    protected synchronized Object compute() {
        if (end - start < 1) return sync1;
        try {

            CalculateTask calculateTask = (CalculateTask) (Object) this;
            if (end - start <= min) {
                if (debug){
                    if (relationship.track == Thread.currentThread().getId()) {
                        relationship.nodes = new Relationship.Node[0];
                        relationship.nodeCall = relationship.nodes.clone();
                    } else {
                        relationship.nodes = new Relationship.Node[1];
                        relationship.nodes[0] = new Relationship.Node(Thread.currentThread(), System.nanoTime());
                        relationship.nodeCall = relationship.nodes.clone();
                    }
                }
                try {
                    for (int i = start; i < end; i++) {
                        run.accept(calculateTask, i);
                    }
                }catch (Throwable e){
                    throwable = e;
                    throw e;
                }finally {
                    if (debug){
                        relationship.nodes[0].stop();
                    }
                }
            } else {
                //if (notCallMode) {
                int rem = (end - start);
                boolean redundancy = rem % min != 0;
                int middle_i = rem / min;
                FalseCallNode[] callNodes = new FalseCallNode[redundancy ? middle_i + 1 : middle_i];
                if (debug){
                    if (relationship.track == Thread.currentThread().getId()) {
                        relationship.initNode(callNodes.length - 1);
                    } else {
                        relationship.initNode(callNodes.length);
                    }
                }
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
                    callNodes[i] = new FalseCallNode(debug ? v->{
                        if (Thread.currentThread().getId() != relationship.track) {
                            relationship.nodes[v.id].start(Thread.currentThread());
                        }
                    } : v->{}, debug ? v->{
                        if (Thread.currentThread().getId() != relationship.track) {
                            relationship.nodes[v.id].stop();
                        }
                    } : v->{}, errorLock, calculateTask, run, start, end,
                            e->throwable = e, ()->throwable
                            , (callNodes.length-1)-i, start + (int)all, add);
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


                //}else {
                //    int middle = (start + end) / 2;
                //    CalculateTask firstTask = new CalculateTask(name, start, middle, min, run/*, ret*/, this.layer,root);
                //    CalculateTask secondTask = new CalculateTask(name, middle, end, min, run/*, ret*/, this.layer,root);
                //    //invokeAll(firstTask,secondTask);
                //    secondTask.fork();
                //    firstTask.compute();
                //    secondTask.join();
                //    if (secondTask.throwable != null) root.throwable = secondTask.throwable;
                //    if (firstTask.throwable != null) root.throwable = firstTask.throwable;
                //}

            }
        }finally {
            synchronized (sync1){
                stop = true;
                unWait();
            }

        }

        return sync1;
    }



}
