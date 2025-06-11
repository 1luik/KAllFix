package n1luik.K_multi_threading.core.base;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.Getter;
import n1luik.KAllFix.util.AsyncWait;
import n1luik.KAllFix.util.TaskRun;
import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.Imixin.IMainThreadExecutor;
import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import n1luik.K_multi_threading.core.util.*;
import n1luik.K_multi_threading.core.util.concurrent.FixNullConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.mojang.datafixers.DataFixer;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.jetbrains.annotations.NotNull;
/* */

/* 1.15.2 code; AKA the only thing that changed
import java.io.File;
/* */
//从mcmt复制并修改 1.18.2原码
public class ParaServerChunkProvider extends ServerChunkCache implements IWorldChunkLockedConfig {
    public static final Field currentlyLoading;
    public static final Thread generatorAllThread;
    protected static final TaskRun generatorAllRun = new TaskRun("generatorAllThread", ()->{
        Base.regThread("generatorAllThread", Thread.currentThread());
    });


    static {
        try {
            currentlyLoading = ChunkHolder.class.getDeclaredField("currentlyLoading");
            currentlyLoading.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        generatorAllThread = generatorAllRun.TaskRun;
        generatorAllThread.start();
    }

    private static final Object2LongMap<String> initId = new Object2LongOpenHashMap<>();
    public static final UnsafeClone<ServerChunkCache, ParaServerChunkProvider> UnsafeClone = new UnsafeClone<>(ServerChunkCache.class, ParaServerChunkProvider.class);
    protected static final int CACHE_SIZE = 4096;
    //protected static final boolean level_lock_mode = false;
    //protected Thread cacheThread;
    //因为lootr 会返回null所以不能进行安全检查
    protected final Map<ChunkCacheAddress, ChunkAccess> chunkCache = new FixNullConcurrentHashMap<>();
    //  protected Map<ChunkCacheAddress, GeneratorNode> chunkTask = new ConcurrentHashMap<>();
    //protected final AtomicInteger access = new AtomicInteger(Integer.MIN_VALUE);
    protected long clearTime = 0;
    protected final Object lock = new Object();
    protected final Object lock2 = new Object();
    protected final Object lock3 = new Object();
    //这样不需要new lock
    protected final ReentrantLock lock4 = new ReentrantLock();
    protected final ReentrantLock lock5 = new ReentrantLock();
    protected final Condition condition4 = lock4.newCondition();
    protected final Condition condition5 = lock5.newCondition();
    protected final Object tasksRunLock = new Object();
    protected final Object tasksRunLock2 = new Object();
    protected volatile boolean isCallTick = false;
    protected volatile boolean isCallGeneratorTick = false;
    protected final List<Runnable> tasks = new CopyOnWriteArrayList<>();
    protected final List<Runnable> tickTasks = new CopyOnWriteArrayList<>();
    protected final List<Runnable> generatorTasks = new CopyOnWriteArrayList<>();
    protected final List<Object> locks = new CopyOnWriteArrayList<>();
    protected final Map<Long, Thread> threadBlacklist = new ConcurrentHashMap<>();
    protected final Map<Long, Thread> waitList = new ConcurrentHashMap<>();
    public Thread lightChunk = null;
    //protected final AtomicInteger ChunkGeneratorTest = new AtomicInteger();
    @Getter
    protected volatile int ChunkGeneratorTest = 0;
    //@Getter
    //protected volatile boolean thisGenerator = false;

    //protected final IMainThreadExecutor iMainThreadExecutor;
    //protected ChunkLock loadingChunkLock = new ChunkLock();
    @Getter
    protected final List<Thread> generatorThread1 = new CopyOnWriteArrayList<>();
    @Getter
    protected Thread generatorThread2;
    static Logger log = LogManager.getLogger();
    Marker chunkCleaner = MarkerManager.getMarker("ChunkCleaner");

    static {
        for (Field declaredField : ParaServerChunkProvider.class.getDeclaredFields()) {
            if (!Modifier.isStatic(declaredField.getModifiers())) {
                initId.put(declaredField.getName(), Unsafe.unsafe.objectFieldOffset(declaredField));
            }

        }
    }

    public static ParaServerChunkProvider toPara(ServerChunkCache chunkSource){
        ParaServerChunkProvider clone;
        try {
            if (chunkSource instanceof ParaServerChunkProvider) {
                clone = (ParaServerChunkProvider)chunkSource;
            }else {
                clone = ParaServerChunkProvider.UnsafeClone.clone(chunkSource);
                //Unsafe.setfinal(ServerLevel.class.getDeclaredField("f_8547_"), chunkSource.level, clone);
            }

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
        clone.UnsafeInit();
        return clone;
    }

    public ParaServerChunkProvider(ServerLevel worldIn, LevelStorageSource.LevelStorageAccess worldDirectory, DataFixer dataFixer, StructureTemplateManager templateManagerIn, Executor executorIn, ChunkGenerator chunkGeneratorIn, int viewDistance, int simDistance, boolean spawnHostiles, ChunkProgressListener p_143236_, ChunkStatusUpdateListener p_143237_, Supplier<DimensionDataStorage> p_143238_) {
        super(worldIn, worldDirectory, dataFixer, templateManagerIn, executorIn, chunkGeneratorIn, viewDistance,
                simDistance, spawnHostiles, p_143236_, p_143237_, p_143238_);

        //iMainThreadExecutor = (IMainThreadExecutor) mainThreadProcessor;
        //cacheThread = new Thread(this::chunkCacheCleanup, "Chunk Cache Cleaner " + worldIn.dimension().location().getPath());
        //cacheThread.start();

        //这里出现过locks没有数据
        for (int i = 0; i < BuiltInRegistries.CHUNK_STATUS.size(); i++) {
            locks.add(new Object());
        }
    }

    /**
     * UnsafeInit顾名思义跟他需要实现跟ParaServerChunkProvider完全一样的功能
     *
     * */
    public void UnsafeInit() {
        //access = new AtomicInteger(Integer.MIN_VALUE);
        clearTime = 0;

        Unsafe.unsafe.putObject(this, initId.getLong("chunkCache"), new FixNullConcurrentHashMap<ChunkCacheAddress, ChunkAccess>());
        Unsafe.unsafe.putObject(this, initId.getLong("lock"), new Object());
        Unsafe.unsafe.putObject(this, initId.getLong("lock2"), new Object());
        Unsafe.unsafe.putObject(this, initId.getLong("lock3"), new Object());
        Unsafe.unsafe.putObject(this, initId.getLong("lock4"), new ReentrantLock());
        Unsafe.unsafe.putObject(this, initId.getLong("lock5"), new ReentrantLock());
        Unsafe.unsafe.putObject(this, initId.getLong("tasksRunLock"), new Object());
        Unsafe.unsafe.putObject(this, initId.getLong("tasksRunLock2"), new Object());
        isCallTick = false;
        //thisGenerator = false;
        isCallGeneratorTick = false;
        lightChunk = null;
        Unsafe.unsafe.putObject(this, initId.getLong("tasks"), new CopyOnWriteArrayList<>());
        Unsafe.unsafe.putObject(this, initId.getLong("tickTasks"), new CopyOnWriteArrayList<>());
        Unsafe.unsafe.putObject(this, initId.getLong("generatorTasks"), new CopyOnWriteArrayList<>());
        Unsafe.unsafe.putObject(this, initId.getLong("locks"), new ArrayList<>(256));
        Unsafe.unsafe.putObject(this, initId.getLong("threadBlacklist"), new ConcurrentHashMap<>());
        Unsafe.unsafe.putObject(this, initId.getLong("waitList"), new ConcurrentHashMap<>());
        //Unsafe.unsafe.putObject(this, initId.getLong("chunkTask"), new ConcurrentHashMap<>());
        Unsafe.unsafe.putObject(this, initId.getLong("generatorThread1"), new CopyOnWriteArrayList<>());
        Unsafe.unsafe.putObject(this, initId.getLong("condition4"), lock4.newCondition());
        Unsafe.unsafe.putObject(this, initId.getLong("condition5"), lock5.newCondition());
        //Unsafe.unsafe.putObject(this, initId.getLong("ChunkGeneratorTest"), new AtomicInteger());
        chunkCleaner = MarkerManager.getMarker("ChunkCleaner");

        for (int i = 0; i < BuiltInRegistries.CHUNK_STATUS.size(); i++) {
            locks.add(new Object());
        }
        ChunkGeneratorTest = 0;
    }

    /*@Override
    public int getTickingGenerated() {
        return Math.min(super.getTickingGenerated(), 441);
    }*/

    @Nullable
    public ChunkAccess waitGetChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {
        //synchronized (locks.get(requiredStatus.getIndex())) {
        //    CompletableFuture<ChunkAccess> chunkAccessCompletableFuture = CompletableFuture.supplyAsync(() -> {
        //        //return this.getChunk(chunkX, chunkZ, requiredStatus, load);
        //        return this.KMT$basePush(chunkX, chunkZ, requiredStatus, load, null, null);
        //    }, tasks::add);
        //    while (isCallTick) Thread.onSpinWait();
        //    return chunkAccessCompletableFuture.join();
        //}
        return getChunk(chunkX, chunkZ, requiredStatus, load);
    }

    //根waitGetChunk的区别是等待getChunk结束和tick运行
    @Nullable
    public ChunkAccess lockGetChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {
        AsyncWait<ChunkAccess> wait = new AsyncWait<>(() -> {
            //return this.getChunk(chunkX, chunkZ, requiredStatus, load);
            return this.KMT$basePush(chunkX, chunkZ, requiredStatus, load, null, null);
        });
        KMT$addTickRun(wait);
        wait.waitTask();
        return wait.getRet();
    }
    //就是正常的不多线程非main线程执行的效果lockGetChunk是修复锁过多问题的
    @Nullable
    public ChunkAccess joinLockGetChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {
        CompletableFuture<ChunkAccess> chunkAccessCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //return this.getChunk(chunkX, chunkZ, requiredStatus, load);
            return this.getChunk(chunkX, chunkZ, requiredStatus, load);
        }, tickTasks::add);
        return chunkAccessCompletableFuture.join();
    }

    @Nullable
    public ChunkAccess generatorGetChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {
        CompletableFuture<ChunkAccess> chunkAccessCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //return this.getChunk(chunkX, chunkZ, requiredStatus, load);
            return this.getChunk(chunkX, chunkZ, requiredStatus, load);
        }, mainThreadProcessor);
        return chunkAccessCompletableFuture.join();
    }
    /*@Nullable
    public ChunkAccess LevelGetChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {

        long i = ChunkPos.asLong(chunkX, chunkZ);

        //log.info("Missed chunk " + i + " on status "  + requiredStatus.toString());
        //log.info("Thread: {}", Thread.currentThread().getName());


        ChunkAccess c = lookupChunk(i, requiredStatus, false);
        if (c != null) {
            return c;
        }

        boolean isBlacklistThread = threadBlacklist.containsValue(Thread.currentThread());
        if (!Base.isThreadPooled() && !isBlacklistThread){
            return waitGetChunk(chunkX, chunkZ, requiredStatus, load);
        }


        ChunkAccess cl;
        //if (ASMHookTerminator.shouldThreadChunks()) {
        //    // Multithread but still limit to 1 load op per chunk
        //    long[] locks = loadingChunkLock.lock(i, 0);
        //    try {
        //        if ((c = lookupChunk(i, requiredStatus, false)) != null) {
        //            return c;
        //        }
        //        cl = super.getChunk(chunkX, chunkZ, requiredStatus, load);
        //    } finally {
        //        loadingChunkLock.unlock(locks);
        //    }
        //} else {
            synchronized (locks.get(requiredStatus.getIndex())) {
                if (chunkCache.containsKey(new ChunkCacheAddress(i, requiredStatus)) && (c = lookupChunk(i, requiredStatus, false)) != null) {
                    return c;
                }
                cl = super.getChunk(chunkX, chunkZ, requiredStatus, load);
                cacheChunk(i, cl, requiredStatus);
            }
        //}
        return cl;
    }*/

    @Override
    protected CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFutureMainThread(int p_8457_, int p_8458_, ChunkStatus p_8459_, boolean p_8460_) {
        Thread value = Thread.currentThread();
        if (value == generatorAllThread) return super.getChunkFutureMainThread(p_8457_, p_8458_, p_8459_, p_8460_);
        synchronized ((!generatorThread1.contains(value) && threadBlacklist.containsValue(value)) ? threadBlacklist : this) {
            return super.getChunkFutureMainThread(p_8457_, p_8458_, p_8459_, p_8460_);
        }
    }

    protected ChunkAccess KMT$basePush(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load, Consumer<ChunkAccess> out, Consumer<Throwable> err){
        synchronized (lock3) {
            ChunkGeneratorTest++;//.getAndAdd(1);
            if (Thread.currentThread() == generatorAllThread) {
                return KMT$baseGetChunk(chunkX, chunkZ, requiredStatus, load, out, err);
            }else {
                Runnable runnable = () -> KMT$baseGetChunk(chunkX, chunkZ, requiredStatus, load, out, err);
                if (ChunkGeneratorTest-1 > 0) {
                    mainThreadProcessor.tell(runnable);
                }else {
                    generatorAllRun.execute(runnable);
                }
                return null;
            }
        }
    }

    protected ChunkAccess KMT$baseGetChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load, Consumer<ChunkAccess> out, Consumer<Throwable> err){
        try {
            ChunkAccess chunk = super.getChunk(chunkX, chunkZ, requiredStatus, load);
            synchronized (lock3) {
                cacheChunk(ChunkPos.asLong(chunkX, chunkZ), chunk, requiredStatus);
                if (out != null)
                    out.accept(chunk);
                ChunkGeneratorTest--;//.getAndAdd(-1);
            }
            return chunk;
        }catch (Throwable e){
            synchronized (lock3) {
                if (err != null) {
                    err.accept(e);
                    ChunkGeneratorTest--;//.getAndAdd(-1);
                    return null;
                }else {
                    ChunkGeneratorTest--;//.getAndAdd(-1);
                    throw e;
                }
            }
        }
    }

    @Override
    @Nullable
    public ChunkAccess getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {

        long i = ChunkPos.asLong(chunkX, chunkZ);

        //log.info("Missed chunk " + i + " on status "  + requiredStatus.toString());
        //log.info("Thread: {}", Thread.currentThread().getName());


        ChunkAccess c = lookupChunk(i, requiredStatus, false);
        if (c != null) {
            return c;
        }

        Thread thisThread = Thread.currentThread();
        if (waitList.containsValue(thisThread)){
            return joinLockGetChunk(chunkX, chunkZ, requiredStatus, load);
        }
        if (thisThread == generatorAllThread){
            return KMT$basePush(chunkX, chunkZ, requiredStatus, load, null, null);
        }
        boolean isBlacklistThread = !generatorThread1.contains(thisThread) && threadBlacklist.containsValue(thisThread);
        //log.info("Thread: {}, threadBlacklist : {}", Thread.currentThread().getName(), Arrays.toString(threadBlacklist.values().stream().map(Thread::getId).toArray()));
        //if (!Base.isThreadPooled() && !isBlacklistThread){
        //    return waitGetChunk(chunkX, chunkZ, requiredStatus, load);
        //}


        ChunkAccess cl;
        //if (ASMHookTerminator.shouldThreadChunks()) {
        //    // Multithread but still limit to 1 load op per chunk
        //    long[] locks = loadingChunkLock.lock(i, 0);
        //    try {
        //        if ((c = lookupChunk(i, requiredStatus, false)) != null) {
        //            return c;
        //        }
        //        cl = super.getChunk(chunkX, chunkZ, requiredStatus, load);
        //    } finally {
        //        loadingChunkLock.unlock(locks);
        //    }
        //} else {
        //if (requiredStatus != ChunkStatus.FULL && !iMainThreadExecutor.isCall() && Thread.currentThread() != iMainThreadExecutor.getCallThread()){
            synchronized (isBlacklistThread ? threadBlacklist : lock) {//代理并委托不能锁this会出现问题的
                //log.info("Missed chunk {} {} now", chunkX, chunkZ);
                //synchronized (locks.get(requiredStatus.getIndex())) {
                    if (chunkCache.containsKey(new ChunkCacheAddress(i, requiredStatus)) && (c = lookupChunk(i, requiredStatus, false)) != null) {
                        return c;
                    }
                    if (isBlacklistThread){
                        generatorThread2 = thisThread;
                    }else {
                        //generatorThread1 = thisThread;
                        generatorThread1.add(thisThread);
                    }
                    VOB3_OOI<ChunkAccess, Throwable> run = new VOB3_OOI<>(null, null, 0);
                    ReentrantLock lock = isBlacklistThread ? lock4 : lock5;
                    Condition condition = isBlacklistThread ? condition4 : condition5; // 根据锁选择 Condition
                    try {
                        KMT$basePush(chunkX, chunkZ, requiredStatus, load, v -> {
                            //因为lootr 会返回null所以不能进行安全检查
                            run.setT1_(v);
                            lock.lock();
                            try {
                                synchronized (run){
                                    run.setT3_(2);
                                    condition.signal(); // 异步任务完成时发送信号
                                }
                            } finally {
                                lock.unlock();
                            }
                        }, e -> {
                            run.setT2_(e);
                            lock.lock();
                            try {
                                synchronized (run){
                                    run.setT3_(2);
                                    condition.signal(); // 异步任务完成时发送信号
                                }
                            } finally {
                                lock.unlock();
                            }
                        });

                        lock.lock();
                        try {
                            synchronized (run){
                                if (run.getT3_() == 0) {
                                    run.setT3_(1);
                                }
                            }
                            while (run.getT3_() < 2) {
                                condition.await(10, TimeUnit.MILLISECONDS); // 带超时的等待
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } finally {
                            lock.unlock();
                        }

                        if (run.getT2_() != null) {
                            throw new RuntimeException(run.getT2_());
                        }
                        cl = run.getT1_();
                        //run.t3 = 1;
                        //while (run.getT3_() < 2){
                        //    Unsafe.unsafe.park(true, 10);
                        //    //Base.LOGGER.info("KMT$basePushEnd$Debug {}", run.getT3_());
                        //}
                        //lock.unlock();
                        ////这里直接访问变量只能读到null
                        //if (run.getT2_() != null) {
                        //    throw new RuntimeException(run.getT2_());
                        //}else {
                        //    cl = run.getT1_();//Objects.requireNonNull(run.getT1_());
                        //}
                    }finally {
                        if (isBlacklistThread){
                            generatorThread2 = null;
                        }else {
                            //generatorThread1 = null;
                            generatorThread1.remove(thisThread);
                        }
                    }
                //}
            }
            ////测试
            //if (isBlacklistThread){
            //    synchronized(threadBlacklist) {
            //        cl = KMT$baseGetChunk(chunkX, chunkZ, requiredStatus, load, true);
            //    }
            //}else {
            //    cl = KMT$baseGetChunk(chunkX, chunkZ, requiredStatus, load, false);
            //}
        //}else {
        //    return waitGetChunk(chunkX, chunkZ, requiredStatus, load);
        //}
        //}
        return cl;
    }

    @Override
    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFuture(int p_8432_, int p_8433_, @NotNull ChunkStatus p_8434_, boolean p_8435_) {
        ChunkAccess chunk = chunkCache.get(new ChunkCacheAddress(ChunkPos.asLong(p_8432_, p_8433_), p_8434_));
        if (chunk != null) {
            return CompletableFuture.completedFuture(Either.left(chunk));
        }
        return super.getChunkFuture(p_8432_, p_8433_, p_8434_, p_8435_);
    }

    //@Override
    //public boolean runDistanceManagerUpdates() {
    //    return thisGenerator = super.runDistanceManagerUpdates();
    //}

    public void testChunkCache(){
        //if (Util.getMillis() + 14246622 % 28 == 0) {
        List<ChunkCacheAddress> remove = new ArrayList<>();
        for (ChunkCacheAddress chunkCacheAddress : chunkCache.keySet()) {
            if (distanceManager.getTickets(chunkCacheAddress.chunk).isEmpty()) {
                remove.add(chunkCacheAddress);
            }
        }
        remove.forEach(chunkCache::remove);
        //}
    }

    @Override
    public void clearCache() {
        super.clearCache();
        if (chunkCache != null) {
            /*chunkCache.clear();/*/testChunkCache();
        }
    }

    @Override
    public void tick(BooleanSupplier p_201913_, boolean p_201914_) {
        isCallTick = true;
        super.tick(p_201913_, p_201914_);
        synchronized (tasksRunLock) {
            tasks.forEach(Runnable::run);
            tasks.clear();
        }
        synchronized (tasksRunLock2) {
            tickTasks.forEach(Runnable::run);
            tickTasks.clear();
        }

        isCallTick = false;

    }

    public void KMT$addTickRun(Runnable runnable){
        tickTasks.add(runnable);
    }
    public void KMT$addRun(Runnable runnable){
        tasks.add(runnable);
    }
    /**
     * 用于兼容在生成区块时运行任务
     * */
    public void KMT$genTestTickRun(Runnable runnable){
        if (mainThreadProcessor instanceof IMainThreadExecutor iMainThreadExecutor){
            boolean b;
            synchronized (iMainThreadExecutor.getLockCall()) {
                if (iMainThreadExecutor.isCall()) {
                    mainThreadProcessor.execute(runnable);
                    b = false;
                }else {
                    b = true;
                }
            }
            if (b) {
                runnable.run();
            }
        }else {
            tasks.add(runnable);
        }
    }

    @Override
    @Nullable
    public LevelChunk getChunkNow(int chunkX, int chunkZ) {
        long i = ChunkPos.asLong(chunkX, chunkZ);

        ChunkAccess c = lookupChunk(i, ChunkStatus.FULL, false);
        if (c != null) {
            return (LevelChunk) c;
        }

        for(int j = 0; j < 4; ++j) {
            if (i == lastChunkPos[j] && lastChunkStatus[j] == ChunkStatus.FULL) {
                    ChunkAccess chunkaccess = lastChunk[j];
                return chunkaccess instanceof LevelChunk ? (LevelChunk)chunkaccess : null;
            }
        }
        ChunkHolder chunkholder = chunkMap.getVisibleChunkIfPresent(i);
        if (chunkholder == null) {
            return null;
        } else {
            try {
                Object o = currentlyLoading.get(chunkholder);
                if (o != null) return (LevelChunk)o; // Forge: If the requested chunk is loading, bypass the future chain to prevent a deadlock.
            } catch (IllegalAccessException e) {
                return null;
            }
            Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> either = chunkholder.getFutureIfPresent(ChunkStatus.FULL).getNow(null);
            if (either == null) {
                return null;
            } else {
                ChunkAccess chunkaccess1 = either.left().orElse(null);
                if (chunkaccess1 != null) {
                    //this.storeInCache(i, chunkaccess1, ChunkStatus.FULL);
                    if (chunkaccess1 instanceof LevelChunk) {
                        return (LevelChunk)chunkaccess1;
                    }
                }

                return null;
            }
        }

        /*//log.debug("Missed chunk " + i + " now");
        //synchronized (this){
            LevelChunk cl = super.getChunkNow(chunkX, chunkZ);
            cacheChunk(i, cl, ChunkStatus.FULL);
            return cl;
        //}*/
    }

    /*public ChunkAccess lookupChunk(long chunkPos, ChunkStatus status, boolean compute) {
        int oldaccess = access.getAndIncrement();
        if (access.get() < oldaccess) {
            // Long Rollover so super rare
            chunkCache.clear();
            return null;
        }
        ChunkCacheLine ccl;
        ccl = chunkCache.get(new ChunkCacheAddress(chunkPos, status));
        if (ccl != null) {
            ccl.updateLastAccess();
            return ccl.getChunk();
        }
        return null;

    }

    public void cacheChunk(long chunkPos, ChunkAccess chunk, ChunkStatus status) {
        long oldaccess = access.getAndIncrement();
        if (access.get() < oldaccess) {
            // Long Rollover so super rare
            chunkCache.clear();
        }
        ChunkCacheLine ccl;
        if ((ccl = chunkCache.get(new ChunkCacheAddress(chunkPos, status))) != null) {
            ccl.updateLastAccess();
            ccl.updateChunkRef(chunk);
        }
        ccl = new ChunkCacheLine(chunk);
        chunkCache.put(new ChunkCacheAddress(chunkPos, status), ccl);
    }*/

    public ChunkAccess lookupChunk(long chunkPos, ChunkStatus status, boolean compute) {
        return chunkCache.get(new ChunkCacheAddress(chunkPos, status));
    }

    //因为lootr 会返回null所以不能进行安全检查
    public void cacheChunk(long chunkPos, ChunkAccess chunk, ChunkStatus status) {
        chunkCache.put(new ChunkCacheAddress(chunkPos, status), chunk);
    }

    /*@Deprecated
    public void chunkCacheCleanup() {
        while (getLevel() == null || getLevel().getServer() == null) {
            log.debug(chunkCleaner, "ChunkCleaner Waiting for startup");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (getLevel().getServer().isRunning()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lock){
                long clearTime1 = System.currentTimeMillis();
                if (clearTime < clearTime1 - 5000){
                    clearTime = clearTime1;
                    chunkCache.clear();
                    //我也没什么好办法。。。他老是会缓存2个不同的区块
                }else {
                    int size = chunkCache.size();
                    if (size < CACHE_SIZE)
                        continue;
                    // System.out.println("CacheFill: " + size);
                    long maxAccess = chunkCache.values().stream().mapToInt(ccl -> ccl.lastAccess).max().orElseGet(() -> access.get());
                    long minAccess = chunkCache.values().stream().mapToInt(ccl -> ccl.lastAccess).min()
                            .orElseGet(() -> Integer.MIN_VALUE);
                    long cutoff = minAccess + (long) ((maxAccess - minAccess) / ((float) size / ((float) CACHE_SIZE)));
                    for (Entry<ChunkCacheAddress, ChunkCacheLine> l : chunkCache.entrySet()) {
                        if (l.getValue().getLastAccess() < cutoff | l.getValue().getChunk() == null) {
                            chunkCache.remove(l.getKey());
                        }
                    }
                }
            }
        }
        log.debug(chunkCleaner, "ChunkCleaner terminating");
    }*/

    @Override
    public void pushThread(long id) {
        Thread value = Thread.currentThread();
        if (threadBlacklist.containsKey(id)) throw new IllegalStateException("Thread " + id + " is already blacklisted");
        //if (value != generatorThread1) {
        if (!generatorThread1.contains(value)) {
            threadBlacklist.put(id, value);
        }
    }

    @Override
    public long pushThread() {
        Thread thread = Thread.currentThread();
        long l = thread.getId();
        if (!threadBlacklist.containsKey(l)) {
            threadBlacklist.put(l, thread);
            return l;
        }
        return -1;
    }
    public void lightChunkThread() {
        lightChunk = Thread.currentThread();
    }
    public void lightChunkThreadEnd() {
        lightChunk = null;
    }
    @Override
    public void pushWaitThread(long id) {
        Thread value = Thread.currentThread();
        if (waitList.containsKey(id)) throw new IllegalStateException("Thread " + id + " is already blacklisted");
        //if (value != generatorThread1) {
        if (!generatorThread1.contains(value)) {
            waitList.put(id, value);
        }
    }

    @Override
    public long pushWaitThread() {
        Thread thread = Thread.currentThread();
        long l = thread.getId();
        if (!waitList.containsKey(l)) {
            waitList.put(l, thread);
            return l;
        }
        return -1;
    }

    @Override
    public void pop(long id) {
        threadBlacklist.remove(id);
    }

    @Override
    public void pop() {
        threadBlacklist.remove(Thread.currentThread().getId());
    }

    @Override
    public void popWait(long id) {
        waitList.remove(id);
    }

    @Override
    public void popWait() {
        waitList.remove(Thread.currentThread().getId());
    }

    @Override
    public void execTasks() {
        synchronized (lock2){
            isCallGeneratorTick = true;
            generatorTasks.forEach(Runnable::run);
            generatorTasks.clear();
            isCallGeneratorTick = false;
            synchronized (tasksRunLock) {
                tasks.forEach(Runnable::run);
                tasks.clear();
            }
        }
    }

    @Override
    public void execTask(Runnable task) {
        generatorTasks.add(task);
    }

    @Override
    public void execWaitTask(Runnable task) {
        generatorTasks.add(task);while (isCallGeneratorTick) Thread.onSpinWait();
    }

    @Override
    public boolean isGeneratorWait() {
        return ChunkGeneratorTest > 0;
    }

    //@Override
    //public boolean runDistanceManagerUpdates() {
    //    synchronized (lock){
    //        return super.runDistanceManagerUpdates();
    //    }
    //}

    protected static class ChunkCacheAddress {

        protected long chunk;
        protected ChunkStatus status;

        public ChunkCacheAddress(long chunk, ChunkStatus status) {
            super();
            this.chunk = chunk;
            this.status = status;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(chunk) ^ status.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ChunkCacheAddress) {
                if ((((ChunkCacheAddress) obj).chunk == chunk) && (((ChunkCacheAddress) obj).status.equals(status))) {
                    return true;
                }
            }
            return false;
        }
    }

    //public static class GeneratorNode {
//
    //}
    /*protected class ChunkCacheLine {
        WeakReference<ChunkAccess> chunk;
        int lastAccess;

        public ChunkCacheLine(ChunkAccess chunk) {
            this(chunk, access.get());
        }

        public ChunkCacheLine(ChunkAccess chunk, int lastAccess) {
            this.chunk = new WeakReference<>(chunk);
            this.lastAccess = lastAccess;
        }

        public ChunkAccess getChunk() {
            return chunk.get();
        }

        public int getLastAccess() {
            return lastAccess;
        }

        public void updateLastAccess() {
            lastAccess = access.get();
        }

        public void updateChunkRef(ChunkAccess c) {
            if (chunk.get() == null) {
                chunk = new WeakReference<>(c);
            }
        }
    }*/
}
