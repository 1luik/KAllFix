package n1luik.K_multi_threading.core.base;

import n1luik.KAllFix.util.ob.OBListDeque;
import n1luik.K_multi_threading.core.util.OB2;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.CollectingNeighborUpdater;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConcurrentCollectingNeighborUpdater extends CollectingNeighborUpdater {
    //protected volatile int count_v = 0;
    //protected final Map<Thread, Deque<NeighborUpdates>> tasks = new ConcurrentHashMap<>();
//
    //public Deque<NeighborUpdates> getTasks() {
    //    return tasks.computeIfAbsent(Thread.currentThread(), k -> new ArrayDeque<>()/*new ConcurrentLinkedDeque<>()*/);
    //}
    protected final Map<Thread, OBListDeque<NeighborUpdates, NeighborUpdates>> tasks = new ConcurrentHashMap<>();

    public OBListDeque<NeighborUpdates, NeighborUpdates> getTasks() {
        return tasks.computeIfAbsent(Thread.currentThread(), k -> new OBListDeque<>(new ArrayList<>(), new ArrayDeque<>())/*new ConcurrentLinkedDeque<>()*/);
    }

    public ConcurrentCollectingNeighborUpdater(Level p_230643_, int p_230644_) {
        super(p_230643_, p_230644_);
    }

    public static ConcurrentCollectingNeighborUpdater toConcurrent(CollectingNeighborUpdater updater) {
        return new ConcurrentCollectingNeighborUpdater(updater.level, updater.maxChainedNeighborUpdates);
    }

    //留一线
    @Override
    protected void runUpdates() {
        runUpdates(getTasks());
    }
    protected void runUpdates(OBListDeque<NeighborUpdates, NeighborUpdates> v) {
        List<NeighborUpdates> t1 = v.t1;
        Deque<NeighborUpdates> t2 = v.t2;
        try {
            while(!t2.isEmpty() || !t1.isEmpty()) {
                for(int i = t1.size() - 1; i >= 0; --i) {
                    t2.push(t1.get(i));
                }

                t1.clear();
                CollectingNeighborUpdater.NeighborUpdates collectingneighborupdater$neighborupdates = t2.peek();

                while(t1.isEmpty()) {
                    if (!collectingneighborupdater$neighborupdates.runNext(this.level)) {
                        t2.pop();
                        break;
                    }
                }
            }
        } finally {
            t1.clear();
            t2.clear();
        }
    }

    protected void addAndRun(BlockPos p_230661_, CollectingNeighborUpdater.NeighborUpdates p_230662_) {
        OBListDeque<NeighborUpdates, NeighborUpdates> tasks1 = getTasks();
        boolean flag = !tasks1.t2.isEmpty();
        boolean flag1 = this.maxChainedNeighborUpdates >= 0 && tasks1.t2.size() >= this.maxChainedNeighborUpdates;
        if (!flag1) {
            if (flag) {
                tasks1.t1.add(p_230662_);
            } else {
                tasks1.t2.push(p_230662_);
            }
        } else if (tasks1.t2.size() == this.maxChainedNeighborUpdates) {
            LOGGER.error("Too many chained neighbor updates. Skipping the rest. First skipped position: " + p_230661_.toShortString());
        }

        if (!flag) {
            this.runUpdates(tasks1);
        }

    }

    //protected void runUpdates(Deque<NeighborUpdates> tasks1) {
//
    //    while (!tasks1.isEmpty()){
    //        NeighborUpdates task = tasks1.peek();
    //        int size = tasks1.size();
    //        while (tasks1.size() == size){
    //            if (!task.runNext(this.level)) {
    //                tasks1.pop();
    //                break;
    //            }
    //        }
    //    }
    //    tasks1.clear();
    //}
//
//
    //@Override
    //protected void addAndRun(BlockPos p_230661_, CollectingNeighborUpdater.NeighborUpdates p_230662_) {
    //    Deque<NeighborUpdates> tasks1 = getTasks();
    //    int size = tasks1.size();
    //    if (this.maxChainedNeighborUpdates >= 0 && size >= this.maxChainedNeighborUpdates) {
    //        tasks1.add(p_230662_);
    //    } else if (size - 1 == this.maxChainedNeighborUpdates) {
    //        LOGGER.error("Too many chained neighbor updates. Skipping the rest. First skipped position: " + p_230661_.toShortString());
    //    }
//
    //    if (size < 1) {
    //        this.runUpdates(tasks1);
    //    }
    //}

}
