package n1luik.K_multi_threading.core.base;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import asm.n1luik.K_multi_threading.asm.ForgeAsm;
import asm.n1luik.K_multi_threading.asm.util.AsmApi;
import asm.n1luik.K_multi_threading.asm.util.AsmApi2;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.Getter;
import n1luik.KAllFix.util.AsyncWait;
import n1luik.KAllFix.util.ChunkStatusSwap;
import n1luik.KAllFix.util.TaskRun;
import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.Imixin.IMainThreadExecutor;
import n1luik.K_multi_threading.core.Imixin.IWorldChunkLockedConfig;
import n1luik.K_multi_threading.core.util.*;
import n1luik.K_multi_threading.core.util.concurrent.VolatileLong2ObjectOpenHashMap;
import net.minecraft.core.IdMap;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.Ticket;
import net.minecraft.util.SortedArraySet;
import net.minecraft.world.level.chunk.*;
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
    private static final int SHARD_COUNT = 4;
    public static final Field currentlyLoading;
    public static final Thread generatorAllThread;
    protected static final TaskRun generatorAllRun = new TaskRun("generatorAllThread", () -> {
        Base.regThread("generatorAllThread", Thread.currentThread());
    });
    public static void generatorAllRunStop(){
        generatorAllRun.stop();
    }


    static {
        try {

            Class<?> chunkHolderClass = AsmApi.mcVersion.startsWith("1.21") ? Class.forName("net.minecraft.server.level.GenerationChunkHolder") : ChunkHolder.class;
            currentlyLoading = chunkHolderClass.getDeclaredField("currentlyLoading");
            currentlyLoading.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
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
    //protected final Map<ChunkCacheAddress, ChunkAccess> chunkCache = new FixNullConcurrentHashMap<>();
    public final LockLong2ObjectAVLTreeMap<ChunkAccess>[] chunkCacheShards;//Long2ObjectOpenHashMap<ChunkAccess>[][] chunkCacheShards;
    //  protected Map<ChunkCacheAddress, GeneratorNode> chunkTask = new ConcurrentHashMap<>();
    //protected final AtomicInteger access = new AtomicInteger(Integer.MIN_VALUE);
    protected final AtomicInteger lockGenLock = new AtomicInteger();
    protected final AtomicInteger lockGenLock2 = new AtomicInteger();
    protected volatile int lockGenLock2Size = 0;
    protected final AtomicReference<Thread> lockGenLock3 = new AtomicReference<>();
    //protected final LongOpenHashSet lockGenKey = new LongOpenHashSet();
    protected final ArrayDeque<LockObj//VOB3_OOI_LockC<ChunkAccess, Throwable>
            > locks = new ArrayDeque<>(64);
    protected final LockLong2ObjectAVLTreeMap<LockObj//VOB3_OOI_LockC<ChunkAccess, Throwable>
                > lockGen = new LockLong2ObjectAVLTreeMap<>();
    protected volatile Thread managedBlockThread = null;
    protected final Queue<OB2F<BooleanSupplier, Thread>> managedBlockTest = new ConcurrentLinkedQueue<>();
    //protected long clearTime = 0;
    protected final Object lock = new Object();
    protected final Object lock2 = new Object();
    protected final Object lock3 = new Object();
    //这样不需要new lock
    //protected final ReentrantLock lock4 = new ReentrantLock();
    //protected final ReentrantLock lock5 = new ReentrantLock();
    //protected final Condition condition4 = lock4.newCondition();
    //protected final Condition condition5 = lock5.newCondition();
    protected final Object tasksRunLock = new Object();
    protected final Object tasksRunLock2 = new Object();
    protected volatile boolean isCallTick = false;
    protected volatile boolean isCallGeneratorTick = false;
    protected final List<Runnable> tasks = new CopyOnWriteArrayList<>();
    protected final List<Runnable> tickTasks = new CopyOnWriteArrayList<>();
    protected final List<Runnable> generatorTasks = new CopyOnWriteArrayList<>();
    //protected final List<Object> locks = new CopyOnWriteArrayList<>();
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
                initId.put(declaredField.getName(), Unsafe.getFieldAddress(declaredField));
            }

        }
    }

    public static ParaServerChunkProvider toPara(ServerChunkCache chunkSource) {
        ParaServerChunkProvider clone;
        //try {
            if (chunkSource instanceof ParaServerChunkProvider) {
                clone = (ParaServerChunkProvider) chunkSource;
            } else {
                clone = ParaServerChunkProvider.UnsafeClone.clone(chunkSource);
                //Unsafe.setfinal(ServerLevel.class.getDeclaredField("f_8547_"), chunkSource.level, clone);
            }

        //} catch (InstantiationException e) {
        //    throw new RuntimeException(e);
        //}
        clone.UnsafeInit();
        return clone;
    }

    public ParaServerChunkProvider(ServerLevel worldIn, LevelStorageSource.LevelStorageAccess worldDirectory, DataFixer dataFixer, StructureTemplateManager templateManagerIn, Executor executorIn, ChunkGenerator chunkGeneratorIn, int viewDistance, int simDistance, boolean spawnHostiles, ChunkProgressListener p_143236_, ChunkStatusUpdateListener p_143237_, Supplier<DimensionDataStorage> p_143238_) {
        super(worldIn, worldDirectory, dataFixer, templateManagerIn, executorIn, chunkGeneratorIn, viewDistance,
                simDistance, spawnHostiles, p_143236_, p_143237_, p_143238_);

        //iMainThreadExecutor = (IMainThreadExecutor) mainThreadProcessor;
        //cacheThread = new Thread(this::chunkCacheCleanup, "Chunk Cache Cleaner " + worldIn.dimension().location().getPath());
        //cacheThread.start();

        int size = getStatusSize();
        chunkCacheShards = new LockLong2ObjectAVLTreeMap[size];
        initchunkCacheShards(size);
        if (mainThreadProcessor instanceof IMainThreadExecutor me) {
            me.KMT$setParaServerChunkProvider(this);
        }
    }

    /**
     * UnsafeInit顾名思义跟他需要实现跟ParaServerChunkProvider完全一样的功能
     */
    public void UnsafeInit() {
        //access = new AtomicInteger(Integer.MIN_VALUE);
        //clearTime = 0;
        managedBlockThread = null;

        int size = getStatusSize();

        //Unsafe.setFinal(this, initId.getLong("chunkCache"), new FixNullConcurrentHashMap<ChunkCacheAddress, ChunkAccess>());
        Unsafe.setFinal(initId.getLong("lock"), this, new Object());
        Unsafe.setFinal(initId.getLong("lock2"), this, new Object());
        Unsafe.setFinal(initId.getLong("lock3"), this, new Object());
        Unsafe.setFinal(initId.getLong("tasksRunLock"), this, new Object());
        Unsafe.setFinal(initId.getLong("tasksRunLock2"), this, new Object());
        //Unsafe.setFinal(initId.getLong("lock4"), new ReentrantLock());
        //Unsafe.setFinal(initId.getLong("lock5"), new ReentrantLock());
        isCallTick = false;
        //thisGenerator = false;
        isCallGeneratorTick = false;
        lightChunk = null;
        Unsafe.setFinal( initId.getLong("tasks"), this, new CopyOnWriteArrayList<>());
        Unsafe.setFinal(initId.getLong("tickTasks"), this, new CopyOnWriteArrayList<>());
        Unsafe.setFinal(initId.getLong("generatorTasks"), this, new CopyOnWriteArrayList<>());
        Unsafe.setFinal(initId.getLong("threadBlacklist"), this, new ConcurrentHashMap<>());
        Unsafe.setFinal(initId.getLong("waitList"), this, new ConcurrentHashMap<>());
        Unsafe.setFinal(initId.getLong("generatorThread1"), this, new CopyOnWriteArrayList<>());
        //Unsafe.setFinal(initId.getLong("condition4"), lock4.newCondition());
        //Unsafe.setFinal(initId.getLong("condition5"), lock5.newCondition());
        //Unsafe.setFinal(initId.getLong("locks"), new ArrayList<>(256));
        //Unsafe.setFinal(initId.getLong("chunkTask"), new ConcurrentHashMap<>());
        LockLong2ObjectAVLTreeMap[] x = new LockLong2ObjectAVLTreeMap[size];
        for (int i = 0; i < size; i++) {
            x[i] = new LockLong2ObjectAVLTreeMap<>();
        }
        Unsafe.setFinal(initId.getLong("chunkCacheShards"), this, x);//new Long2ObjectOpenHashMap[size][SHARD_COUNT]);
        Unsafe.setFinal(initId.getLong("lockGenLock"), this, new AtomicInteger(0));
        Unsafe.setFinal(initId.getLong("lockGenLock2"), this, new AtomicInteger(0));
        Unsafe.setFinal(initId.getLong("lockGenLock3"), this, new AtomicReference<>(null));
        //Unsafe.setFinal(initId.getLong("lockGenKey"), this, new LongOpenHashSet());
        Unsafe.setFinal(initId.getLong("locks"), this, new ArrayDeque<>(64));
        Unsafe.setFinal(initId.getLong("lockGen"), this, new LockLong2ObjectAVLTreeMap<>());
        Unsafe.setFinal(initId.getLong("managedBlockTest"), this, new ConcurrentLinkedQueue<>());
        //Unsafe.setFinal(initId.getLong("ChunkGeneratorTest"), new AtomicInteger());
        chunkCleaner = MarkerManager.getMarker("ChunkCleaner");
        lockGenLock2Size = 0;

        //try{
        //    //BuiltInRegistries.CHUNK_STATUS
        //    Class<?> aClass = Class.forName("net.minecraft.core.registries.BuiltInRegistries", true, ParaServerChunkProvider.class.getClassLoader());
        //    initLocks(((IdMap<?>)aClass.getField("f_256940_").get(aClass)).size());
        //} catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
        //    try{
        //        //net.minecraft.core.Registry#CHUNK_STATUS
        //        Class<?> aClass = Class.forName("net.minecraft.core.Registry", true, ParaServerChunkProvider.class.getClassLoader());
        //        initLocks(((IdMap<?>)aClass.getField("f_122833_").get(aClass)).size());
        //    } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e2) {
        //        throw new RuntimeException(e2);
        //    }
//
        //}

        ChunkGeneratorTest = 0;
        //initchunkCacheShards(size);//jvm会在设置前读取
        if (mainThreadProcessor instanceof IMainThreadExecutor me) {
            me.KMT$setParaServerChunkProvider(this);
        }


    }

    public <T> T KMT$LockChunk(long pos, BiFunction<LockObj, LockObj//VOB3_OOI_LockC<ChunkAccess, Throwable>
                , T> f) {
        while (!lockGenLock.compareAndSet(0, 1)) ;
        LockObj//VOB3_OOI_LockC<ChunkAccess, Throwable>
                lock = lockGen.get(pos);
        boolean get = lock == null;
        if (lock == null) {
            lock = locks.poll();
            if (lock == null) {
                lock = new LockObj();//VOB3_OOI_LockC<>();
            }
            lockGen.put(pos, lock);
        }
        lockGenLock.set(0);
        try {
            return f.apply(lock, get ? null : lock);
        } finally {
            if (get) {
                while (!lockGenLock.compareAndSet(0, 1)) ;
                LockObj//VOB3_OOI_LockC<ChunkAccess, Throwable>
                        lock2 = lockGen.remove(pos);
                if (locks.size() < 64) {
                    locks.add(lock);
                }
                if (lock2 != lock && lock2 != null) {
                    lockGen.put(pos, lock2);
                }
                lockGenLock.set(0);
                if (lock2 != lock) {
                    if (lock2 != null) {
                        log.warn("Unlock chunk failed, lock not match [{}, {}]", ChunkPos.getX(pos), ChunkPos.getZ(pos));
                    } else {
                        log.warn("Unlock chunk failed, lock is null [{}, {}]", ChunkPos.getX(pos), ChunkPos.getZ(pos));
                    }
                }
            }
        }
    }

    public static int getStatusSize() {
        try {
            //BuiltInRegistries.CHUNK_STATUS
            Class<?> aClass = Class.forName("net.minecraft.core.registries.BuiltInRegistries", true, ParaServerChunkProvider.class.getClassLoader());
            return ((IdMap<?>) aClass.getField(ForgeAsm.minecraft_map.mapField(
                    "net.minecraft.core.registries.BuiltInRegistries".replace(".", "/")+".CHUNK_STATUS"
            )[1]).get(aClass)).size();
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            try {
                //net.minecraft.core.Registry#CHUNK_STATUS
                Class<?> aClass = Class.forName("net.minecraft.core.Registry", true, ParaServerChunkProvider.class.getClassLoader());
                return ((IdMap<?>) aClass.getField(ForgeAsm.minecraft_map.mapField(
                        "net/minecraft/core/Registry.CHUNK_STATUS"
                )[1]).get(aClass)).size();
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e2) {
                throw new RuntimeException("Failed to get CHUNK_STATUS size", e2);
            }

        }
    }

    //protected void initchunkCacheShards(int size){
    //    for (int i = 0; i < size; i++) {
    //        for (int i2 = 0; i2 < SHARD_COUNT; i2++) {
    //            chunkCacheShards[i][i2] = new Long2ObjectOpenHashMap<>(512, 0.4f);
    //        }
    //    }
    //}
    protected void initchunkCacheShards(int size) {
        for (int i = 0; i < size; i++) {
            chunkCacheShards[i] = new LockLong2ObjectAVLTreeMap<>();
        }
    }

    //protected void initLocks(int size){
    //    for (int i = 0; i < size; i++) {
    //        locks.add(new Object());
    //    }
    //}


    private static int getShardIndex(long chunkPos) {
        return Math.abs((int) (chunkPos % 4));
    }

    // 修改查询方法
    public ChunkAccess lookupChunk(long chunkPos, ChunkStatus status) {
        //int shard = getShardIndex(chunkPos);
        return chunkCacheShards[status.getIndex()]//[shard]
                .get(chunkPos);
    }
    // 修改查询方法
    public ChunkAccess lookupChunkFull(long chunkPos) {
        //int shard = getShardIndex(chunkPos);
        return chunkCacheShards[ChunkStatusSwap.FULL.getIndex()]//[shard]
                .get(chunkPos);
    }

    // 修改缓存方法
    public void cacheChunk(long chunkPos, ChunkAccess chunk, ChunkStatus status) {
        //int shard = getShardIndex(chunkPos);
        chunkCacheShards[status.getIndex()]//[shard]
                .put(chunkPos, chunk);
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

    protected ChunkAccess KMT$basePush(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load, Consumer<ChunkAccess> out, Consumer<Throwable> err) {
        //    synchronized (lock3) {
        //        ChunkGeneratorTest++;//.getAndAdd(1);
        //        if (Thread.currentThread() != generatorAllThread) {
        //            Runnable runnable = () -> KMT$baseGetChunk(chunkX, chunkZ, requiredStatus, load, out, err);
//
        //            if (mainThreadProcessor instanceof IMainThreadExecutor iMainThreadExecutor){
        //                boolean b;
        //                synchronized (iMainThreadExecutor.getLockCall()) {
        //                    if (iMainThreadExecutor.isCall()) {
        //                        mainThreadProcessor.execute(runnable);
        //                        b = false;
        //                    }else {
        //                        b = true;
        //                    }
        //                }
        //                if (b) {
        //                    generatorAllRun.execute(runnable);
        //                }
        //            }else {
        //                if (ChunkGeneratorTest-1 > 0) {
        //                    mainThreadProcessor.tell(runnable);
        //                }else {
        //                    generatorAllRun.execute(runnable);
        //                }
        //            }
        //            return null;
        //        }
        //    }

        return KMT$baseGetChunk(chunkX, chunkZ, requiredStatus, load, out, err);
    }

    protected ChunkAccess KMT$baseGetChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load, Consumer<ChunkAccess> out, Consumer<Throwable> err) {
        Thread thread = Thread.currentThread();
        boolean notManagedBlockThread = managedBlockThread != thread;
        if (notManagedBlockThread)while (!lockGenLock3.compareAndSet(null, thread)) ;

        try {
            ChunkAccess chunk = lookupChunk(ChunkPos.asLong(chunkX, chunkZ), requiredStatus);
            if (chunk == null) {
                chunk = KMT$LockChunk(ChunkPos.asLong(chunkX, chunkZ), (l, l2) -> {
                    long aLong = ChunkPos.asLong(chunkX, chunkZ);
                    if (l2 != null && l.status > requiredStatus.getIndex()){
                        ChunkAccess c = lookupChunk(aLong, requiredStatus);
                        if (c != null) {
                            return c;
                        }
                        CompletableFuture ct = getChunkFutureMainThread(chunkX, chunkZ, requiredStatus, load);
                        mainThreadProcessor.managedBlock(ct::isDone);
                        return readChunk(ct.join());
                    }
                    if (AsmApi2.bootType == AsmApi2.BootType.NEO_FORGE){
                        if (l.lock > 0) {
                            ChunkHolder visibleChunkIfPresent = chunkMap.getVisibleChunkIfPresent(aLong);
                            if (visibleChunkIfPresent != null) {
                                LevelChunk levelChunk = KMT$currentlyLoading(visibleChunkIfPresent);
                                if(levelChunk != null)return levelChunk;
                            }
                        }
                    }
                    synchronized (l) {
                        l.lock++;
                        try{
                            l.status = requiredStatus.getIndex();
                            return super.getChunk(chunkX, chunkZ, requiredStatus, load);
                        }finally {
                            l.lock--;
                        }
                    }
                });
                if (requiredStatus == ChunkStatus.FULL) {
                    if (chunk instanceof ImposterProtoChunk)
                        log.info("FULL ImposterProtoChunk: {} {}", chunkX, chunkZ, new Throwable());
                }
            };
            synchronized (lock3) {
                cacheChunk(ChunkPos.asLong(chunkX, chunkZ), chunk, requiredStatus);
                if (out != null)
                    out.accept(chunk);
                ChunkGeneratorTest--;//.getAndAdd(-1);
            }
            return chunk;
        } catch (Throwable e) {
            synchronized (lock3) {
                if (err != null) {
                    err.accept(e);
                    ChunkGeneratorTest--;//.getAndAdd(-1);
                    return null;
                } else {
                    ChunkGeneratorTest--;//.getAndAdd(-1);
                    throw e;
                }
            }
        } finally {
            if (notManagedBlockThread && lockGenLock3.get() == thread) lockGenLock3.set(null);
        }
    }

    @Override
    @Nullable
    public ChunkAccess getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {

        long i = ChunkPos.asLong(chunkX, chunkZ);

        //log.info("Missed chunk " + i + " on status "  + requiredStatus.toString());
        //log.info("Thread: {}", Thread.currentThread().getName());


        ChunkAccess c = lookupChunk(i, requiredStatus);
        if (c != null) {
            return c;
        }

        Thread thisThread = Thread.currentThread();
        return KMT$basePush(chunkX, chunkZ, requiredStatus, load, null, null);
        //if (waitList.containsValue(thisThread)){
        //    return joinLockGetChunk(chunkX, chunkZ, requiredStatus, load);
        //}
        //if (thisThread == generatorAllThread){
        //    return KMT$basePush(chunkX, chunkZ, requiredStatus, load, null, null);
        //}
        //boolean isBlacklistThread = !generatorThread1.contains(thisThread) && threadBlacklist.containsValue(thisThread);
        //log.info("Thread: {}, threadBlacklist : {}", Thread.currentThread().getName(), Arrays.toString(threadBlacklist.values().stream().map(Thread::getId).toArray()));
        //if (!Base.isThreadPooled() && !isBlacklistThread){
        //    return waitGetChunk(chunkX, chunkZ, requiredStatus, load);
        //}


        //ChunkAccess cl;
        //if (ASMHookTerminator.shouldThreadChunks()) {
        //    // Multithread but still limit to 1 load op per chunk
        //    long[] locks = loadingChunkLock.lock(i, 0);
        //    try {
        //        if ((c = lookupChunk(i, requiredStatus)) != null) {
        //            return c;
        //        }
        //        cl = super.getChunk(chunkX, chunkZ, requiredStatus, load);
        //    } finally {
        //        loadingChunkLock.unlock(locks);
        //    }
        //} else {
        //if (requiredStatus != ChunkStatus.FULL && !iMainThreadExecutor.isCall() && Thread.currentThread() != iMainThreadExecutor.getCallThread()){
        //synchronized (isBlacklistThread ? threadBlacklist : lock) {//代理并委托不能锁this会出现问题的
        //    c = lookupChunk(i, requiredStatus);
        //    if (c != null) {
        //        return c;
        //    }
        //    if (isBlacklistThread){
        //        generatorThread2 = thisThread;
        //    }else {
        //        //generatorThread1 = thisThread;
        //        generatorThread1.add(thisThread);
        //    }
        //    try {
        //        cl = KMT$getChunk_(chunkX, chunkZ, requiredStatus, load, isBlacklistThread);
        //    }finally {
        //        if (isBlacklistThread){
        //            generatorThread2 = null;
        //        }else {
        //            //generatorThread1 = null;
        //            generatorThread1.remove(thisThread);
        //        }
        //    }
        //}
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
        //return cl;
    }

    public ChunkAccess KMT$getChunk_(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load, VOB3_OOI_LockC<ChunkAccess, Throwable> run) {
        synchronized (run) {
            //log.debug("Missed chunk {} {} now", chunkX, chunkZ);
            //synchronized (locks.get(requiredStatus.getIndex())) {
            run.t1 = null;
            run.t2 = null;
            run.t3 = 0;
            Condition condition = run.condition; // 根据锁选择 Condition
            KMT$basePush(chunkX, chunkZ, requiredStatus, load, v -> {
                //因为lootr 会返回null所以不能进行安全检查
                run.setT1_(v);
                run.lock();
                try {
                    synchronized (run) {
                        run.setT3_(2);
                        condition.signal(); // 异步任务完成时发送信号
                    }
                } finally {
                    run.unlock();
                }
            }, e -> {
                run.setT2_(e);
                run.lock();
                try {
                    synchronized (run) {
                        run.setT3_(2);
                        condition.signal(); // 异步任务完成时发送信号
                    }
                } finally {
                    run.unlock();
                }
            });

            run.lock();
            try {
                synchronized (run) {
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
                run.unlock();
            }

            if (run.getT2_() != null) {
                throw new RuntimeException(run.getT2_());
            }
            return run.getT1_();
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
            //}
        }
    }

    @Override
    public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getChunkFuture(int p_8432_, int p_8433_, @NotNull ChunkStatus p_8434_, boolean p_8435_) {
        ChunkAccess chunk = lookupChunk(ChunkPos.asLong(p_8432_, p_8433_), p_8434_);//chunkCache.get(new ChunkCacheAddress(ChunkPos.asLong(p_8432_, p_8433_), p_8434_));
        if (chunk != null) {
            return genTask(chunk);
        }

        Thread value = Thread.currentThread();
        if (value == generatorAllThread) {
            CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture = super.getChunkFutureMainThread(p_8432_, p_8433_, p_8434_, p_8435_);
            this.mainThreadProcessor.managedBlock(completablefuture::isDone);
            return completablefuture;
        }
        synchronized ((!generatorThread1.contains(value) && threadBlacklist.containsValue(value)) ? threadBlacklist : this) {
            CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture = super.getChunkFutureMainThread(p_8432_, p_8433_, p_8434_, p_8435_);
            KMT$genTestTickRun(() -> this.mainThreadProcessor.managedBlock(completablefuture::isDone));
            return completablefuture;
        }
    }

    //@Override
    //public boolean runDistanceManagerUpdates() {
    //    return thisGenerator = super.runDistanceManagerUpdates();
    //}

    public void testChunkCache() {
        //if (Util.getMillis() + 14246622 % 28 == 0) {
        //List<ChunkCacheAddress> remove = new ArrayList<>();
        Long2ObjectOpenHashMap<SortedArraySet<Ticket<?>>> tickets = distanceManager.tickets;
        //for (Long2ObjectOpenHashMap<ChunkAccess>[] chunkCacheShard : chunkCacheShards) {
        //    for (Long2ObjectOpenHashMap<ChunkAccess> chunkCacheShard2 : chunkCacheShard) {
        for (LockLong2ObjectAVLTreeMap<ChunkAccess> chunkCacheShard2 : chunkCacheShards) {
            for (LongIterator iterator = chunkCacheShard2.keySet().iterator(); iterator.hasNext(); ) {
                long l = iterator.nextLong();
                SortedArraySet<Ticket<?>> tickets1 = tickets.get(l);
                if (tickets1 == null) {
                    iterator.remove();
                } else if (tickets1.isEmpty()) {
                    iterator.remove();
                }
            }
        }
        //    }
        //}
        //for (ChunkCacheAddress chunkCacheAddress : chunkCache.keySet()) {
        //    //if (distanceManager.getTickets(chunkCacheAddress.chunk).isEmpty()) {
        //    //    remove.add(chunkCacheAddress);
        //    //}
        //    SortedArraySet<Ticket<?>> tickets1 = tickets.get(chunkCacheAddress.chunk);
        //    if (tickets1 == null) {
        //        remove.add(chunkCacheAddress);
        //    }else if (tickets1.isEmpty()) {
        //        remove.add(chunkCacheAddress);
        //    }
        //}
        //remove.forEach(chunkCache::remove);
        //}
    }

    @Override
    public void clearCache() {
        super.clearCache();
        if (chunkCacheShards != null) {
            /*chunkCache.clear();/*/
            testChunkCache();
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
        KMT$managedBlockRun();

    }

    public void KMT$addTickRun(Runnable runnable) {
        tickTasks.add(runnable);
    }

    public void KMT$addRun(Runnable runnable) {
        tasks.add(runnable);
    }

    /**
     * 用于兼容在生成区块时运行任务
     */
    public void KMT$genTestTickRun(Runnable runnable) {
        if (Thread.currentThread() == generatorAllThread) {
            runnable.run();
            return;
        }
        if (mainThreadProcessor instanceof IMainThreadExecutor iMainThreadExecutor) {
            boolean b;
            synchronized (iMainThreadExecutor.getLockCall()) {
                if (iMainThreadExecutor.isCall()) {
                    mainThreadProcessor.execute(runnable);
                    b = false;
                } else {
                    b = true;
                }
            }
            if (b) {
                runnable.run();
            }
        } else {
            tasks.add(runnable);
        }
    }

    public LevelChunk KMT$currentlyLoading(ChunkHolder chunkholder) {
        try {
            return (LevelChunk) currentlyLoading.get(chunkholder);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    @Nullable
    public LevelChunk getChunkNow(int chunkX, int chunkZ) {
        long i = ChunkPos.asLong(chunkX, chunkZ);

        ChunkAccess c = lookupChunk(i, ChunkStatus.FULL);
        if (c != null) {
            return (LevelChunk) c;
        }

        for (int j = 0; j < 4; ++j) {
            if (i == lastChunkPos[j] && lastChunkStatus[j] == ChunkStatus.FULL) {
                ChunkAccess chunkaccess = lastChunk[j];
                return chunkaccess instanceof LevelChunk ? (LevelChunk) chunkaccess : null;
            }
        }
        ChunkHolder chunkholder = chunkMap.getVisibleChunkIfPresent(i);
        if (chunkholder == null) {
            return null;
        } else {
            var o = KMT$currentlyLoading(chunkholder);
            if (o != null)
                return (LevelChunk) o; // Forge: If the requested chunk is loading, bypass the future chain to prevent a deadlock.
            Object either = ((CompletableFuture)chunkholder.getFutureIfPresent(ChunkStatus.FULL)).getNow(null);
            if (either == null) {
                return null;
            } else {
                ChunkAccess chunkaccess1 = readChunkNull(either);
                if (chunkaccess1 != null) {
                    //this.storeInCache(i, chunkaccess1, ChunkStatus.FULL);
                    if (chunkaccess1 instanceof LevelChunk) {
                        return (LevelChunk) chunkaccess1;
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
        return lookupChunk(chunkPos, status);
    }
    //public ChunkAccess lookupChunk(long chunkPos, ChunkStatus status) {
    //    return chunkCache.get(new ChunkCacheAddress(chunkPos, status));
    //}
//

    /// /因为lootr 会返回null所以不能进行安全检查
    //public void cacheChunk(long chunkPos, ChunkAccess chunk, ChunkStatus status) {
    //    chunkCache.put(new ChunkCacheAddress(chunkPos, status), chunk);
    //}

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
    public boolean isPushThread(long id) {
        return threadBlacklist.containsKey(id);
    }

    @Override
    public boolean isPushThread() {
        return threadBlacklist.containsKey(Thread.currentThread().getId());
    }

    @Override
    public void pushThread(long id) {
        Thread value = Thread.currentThread();
        if (threadBlacklist.containsKey(id))
            throw new IllegalStateException("Thread " + id + " is already blacklisted");
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
        synchronized (lock2) {
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
        generatorTasks.add(task);
        while (isCallGeneratorTick) Thread.onSpinWait();
    }

    @Override
    public boolean isGeneratorWait() {
        return ChunkGeneratorTest > 0;
    }

    public boolean KMT$managedBlockRun() {
        Iterator<OB2F<BooleanSupplier, Thread>> iterator = managedBlockTest.iterator();
        while (iterator.hasNext()) {
            OB2F<BooleanSupplier, Thread> booleanSupplier = iterator.next();
            if (booleanSupplier.t1.getAsBoolean()) {
                iterator.remove();
                LockSupport.unpark(booleanSupplier.t2);
            } else {
                return true;
            }
        }
        return false;
    }

    public void KMT$managedBlockTest() {
        if (KMT$managedBlockRun()){
            //System.out.println("KMT$managedBlockTest1");
            Base.getEx().submit(()-> {
                //managedBlockThread = null;
                mainThreadProcessor.managedBlock(new MyBooleanSupplier());
                KMT$managedBlockTest();
                //System.out.println("KMT$managedBlockTest2");
            });
        }

    }
    public void KMT$managedBlockEnd() {
        while (!lockGenLock2.compareAndSet(0, 1)) ;
        lockGenLock2Size = 0;
        managedBlockThread = null;
        lockGenLock2.set(0);
        KMT$managedBlockTest();
        //else {
        //}
        //System.out.println("KMT$managedBlockTest4");
    }
    public int KMT$managedBlock(BooleanSupplier p18702) {
        Thread thread = Thread.currentThread();
        while (!lockGenLock2.compareAndSet(0, 1)) ;
        Thread managedBlockThread1 = managedBlockThread;
        //System.out.println("KMT$managedBlockTest3 "+thread+managedBlockThread1);
        if (lockGenLock2Size > 0 && managedBlockThread1 == thread) {
            lockGenLock2.set(0);
            return 2;
        }
        if (lockGenLock3.get() == thread) lockGenLock3.set(null);
        if (managedBlockThread1 == null) {
            managedBlockThread = thread;
            lockGenLock2Size++;
            lockGenLock2.set(0);
            return 0;
        } else {
            if (p18702.getClass() == MyBooleanSupplier.class){
                lockGenLock2.set(0);
                return 1;
            }
            managedBlockTest.add(new OB2F<>(p18702, thread));
            lockGenLock2.set(0);
            while (!p18702.getAsBoolean()) {
                LockSupport.parkNanos(500*1000000L);
                //System.out.println("KMT$managedBlockTest5"+managedBlockThread1);
            }
            return 1;
        }
    }

    public boolean isChunkLoaded(int x, int z) {//可能对不上
        SortedArraySet<Ticket<?>> tickets = distanceManager.tickets.get(ChunkPos.asLong(x, z));
        return tickets != null && !tickets.isEmpty();
    }
    public ChunkAccess getBufMax(int x, int z, ChunkStatus status) {//可能无法获取
        return getBufMax(ChunkPos.asLong(x, z), status);
    }
    public ChunkAccess getBufMax(long pos, ChunkStatus status) {//可能无法获取
        ChunkAccess ret = null;
        while (status != null) {
            ret = lookupChunk(pos, status);
            if (ret != null) break;
            ChunkStatus parent = status.getParent();
            if (parent == status) break;
            status = parent;

        }
        return ret;
    }
    public Stream<ChunkPos> getLoadedChunks() {//可能对不上
        return distanceManager.tickets.keySet().longStream().mapToObj(ChunkPos::new);
    }
    public LongStream getLoadedChunkPos() {//可能对不上
        return distanceManager.tickets.keySet().longStream();
    }
    public ChunkAccess getChunkOnDisk(int x, int z) {
        return getChunk(x, z, ChunkStatus.EMPTY,true);
    }
    public ChunkAccess getChunkMinOnDisk(int x, int z) {
        ChunkAccess chunk = getBufMax(x, z, ChunkStatus.FULL);
        if (chunk != null) return chunk;
        return getChunkOnDisk(x, z);
    }
    public ChunkStatus getStatus(int x, int z) {// TODO 内存开销问题
        return getChunkMinOnDisk(x, z).getStatus();
    }
    public ChunkAccess getBufMax(int x, int z) {
        return getBufMax(ChunkPos.asLong(x, z), ChunkStatus.FULL);
    }
    public ChunkAccess getBufMax(long pos) {
        return getBufMax(pos, ChunkStatus.FULL);
    }
    public void KMT$managedBlockRun(BooleanSupplier p_18702_){
        mainThreadProcessor.managedBlock(p_18702_);
    }
    public void KMT$pollTaskRun(){
        mainThreadProcessor.pollTask();
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

    /**
     * 保证jvm始终可以内连而不是激活重写
     */
    public static final class LockLong2ObjectAVLTreeMap<V> extends Long2ObjectAVLTreeMap<V> {
    }

    public static final class LockObj {
        public volatile int status;
        public volatile int lock;
    }

    private class MyBooleanSupplier implements BooleanSupplier {
        volatile boolean b = !ParaServerChunkProvider.this.KMT$managedBlockRun();

        @Override
        public boolean getAsBoolean() {
            if (b) {
                return true;
            } else {
                return b = !ParaServerChunkProvider.this.KMT$managedBlockRun();
            }
        }
    }

    @Override
    public IMainThreadExecutor KMTIMainThreadExecutor() {
        return (IMainThreadExecutor)mainThreadProcessor;
    }

    private ChunkAccess readChunk(Object o){
        return ((Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>)o).left().get();
    }
    private ChunkAccess readChunkNull(Object o){
        return ((Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>)o).left().orElse(null);
    }
    private CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> genTask(ChunkAccess o){
        return CompletableFuture.completedFuture(Either.left(o));
    }
}
