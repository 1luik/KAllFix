package n1luik.K_multi_threading.core;

import asm.n1luik.K_multi_threading.asm.MappingImpl;
import asm.n1luik.K_multi_threading.asm.MappingTsrgImpl;
import com.mojang.logging.LogUtils;
import n1luik.K_multi_threading.core.base.CalculateTask;
import n1luik.K_multi_threading.core.sync.GetterDataMap;
import n1luik.K_multi_threading.core.util.NodeHashMap;
import n1luik.K_multi_threading.fix.FixGetterRoot;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Base {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "k_multi_threading";
    public static final String MOD_ID2 = "k_all_fix";
    /**整个顶性能有影响不建议开*/
    public static final boolean debugAE2Thread = false;
    public static final long ThreadpoolKeepAliveTime =  Long.getLong("KMT-ThreadpoolKeepAliveTime", TimeUnit.SECONDS.toMillis(7));
    public static MinecraftServer mcs;
    public static int threadTaskMax = 80;
    final static ForkJoinPool_ ex;
    //@Nullable
    final static ForkJoinPool_ async;// = null;
    static AtomicBoolean isTicking = new AtomicBoolean();
    static AtomicInteger threadID = new AtomicInteger();

    public static ForkJoinPool_ setupThreadpool(int parallelism, int threadSize, boolean asyncMode) {
        //if (ex == null){
            threadID = new AtomicInteger();
            final ClassLoader cl = Base.class.getClassLoader();
            ForkJoinPool.ForkJoinWorkerThreadFactory fjpf = p -> {
                ForkJoinWorkerThread fjwt = new ForkJoinWorkerThread_(p/*,ex*/) {
                    protected void onTermination(Throwable p_211561_) {
                        super.onTermination(p_211561_);
                    }
                };
                fjwt.setName("MCMT-Pool-Thread-" + threadID.getAndIncrement());
                regThread("MCMT", fjwt);
                fjwt.setContextClassLoader(cl);
                return fjwt;
            };
            return new ForkJoinPool_(
                    parallelism,
                    fjpf,
                    null, asyncMode, threadSize);
       // }
    }
    public static ForkJoinPool_ createThreadpool2(int parallelism, int threadSize, boolean asyncMode, Map<Object, Object> dataMap) {
            final ClassLoader cl = Base.class.getClassLoader();
            ForkJoinPool.ForkJoinWorkerThreadFactory fjpf = p -> {
                ForkJoinWorkerThread fjwt = new ForkJoinWorkerThread_(p/*,ex*/) {
                    protected void onTermination(Throwable p_211561_) {
                        super.onTermination(p_211561_);
                    }
                };
                fjwt.setName("MCMT-Pool-Thread-" + threadID.getAndIncrement());
                regThread("MCMT", fjwt);
                fjwt.setContextClassLoader(cl);
                return fjwt;
            };
            return new ForkJoinPool_(
                    parallelism,
                    fjpf,
                    null, asyncMode, threadSize, dataMap);
    }

    static Map<String, Set<Thread>> mcThreadTracker = new ConcurrentHashMap<String, Set<Thread>>();
    static Set<Thread> mcThreads = new HashSet<>();

    // Statistics
    public static AtomicInteger currentWorlds = new AtomicInteger();
    public static AtomicInteger currentEnts = new AtomicInteger();
    public static AtomicInteger currentTEs = new AtomicInteger();
    public static AtomicInteger currentEnvs = new AtomicInteger();

    //Operation logging
    public static Set<String> currentTasks = ConcurrentHashMap.newKeySet();


    public static void regThread(String poolName, Thread thread) {
        mcThreadTracker.computeIfAbsent(poolName, s -> ConcurrentHashMap.newKeySet()).add(thread);
        mcThreads.add(thread);
    }

    public static boolean isThreadPooled(String poolName, Thread t) {
        return mcThreadTracker.containsKey(poolName) && mcThreadTracker.get(poolName).contains(t);
    }

    public static boolean isThreadPooled(Thread t) {
        return mcThreads.contains(t);
    }

    public static boolean isThreadPooled() {/*
        for (Thread mcThread : mcThreads) {
            System.out.println("Thread: " + mcThread.getName());
        }*/
        return mcThreads.contains(Thread.currentThread());
    }

    public static ForkJoinPool_ getEx() {
        return ex;
    }

    public static class ForkJoinPool_ extends ForkJoinPool implements GetterDataMap {
        protected final Map<Object, Object> dataMap;

        public ForkJoinPool_(int parallelism,
                            ForkJoinWorkerThreadFactory factory,
                            Thread.UncaughtExceptionHandler handler,
                            boolean asyncMode,
                            int threadSize) {
            super(parallelism, factory, handler, asyncMode,0, threadSize, 1, null, ThreadpoolKeepAliveTime, TimeUnit.MILLISECONDS);//20秒
            dataMap = new ConcurrentHashMap<>(){
                @Override
                public synchronized Object put(@NotNull Object key, @NotNull Object value) {
                    return value != null ? super.put(key, value) : null;
                }

                @Override
                public synchronized boolean remove(Object key, Object value) {
                    return super.remove(key, value);
                }

                @Override
                public Object get(Object key) {
                    return super.get(key);
                }
            };
        }
        public ForkJoinPool_(int parallelism,
                            ForkJoinWorkerThreadFactory factory,
                            Thread.UncaughtExceptionHandler handler,
                            boolean asyncMode,
                            int threadSize, Map<Object, Object> dataMap) {
            super(parallelism, factory, handler, asyncMode,0, threadSize, 1, null, ThreadpoolKeepAliveTime, TimeUnit.MILLISECONDS);//20秒
            this.dataMap = dataMap;
        }


        @Override
        public Map<Object, Object> getDataMap() {
            return dataMap;
        }

    }
    public static class ForkJoinWorkerThread_ extends ForkJoinWorkerThread implements GetterDataMap {
        protected final ForkJoinPool_ base;
        public final int hash = Long.hashCode(System.nanoTime() ^ System.currentTimeMillis());//防止哈希一样

        /**
         * Creates a ForkJoinWorkerThread operating in the given pool.
         *
         * @param pool the pool this thread works in
         * @throws NullPointerException if pool is null
         */
        protected ForkJoinWorkerThread_(ForkJoinPool pool/*,ForkJoinPool_ base*/) {
            super(pool);
            this.base = ex;//base;
        }

        @Override
        public Map<Object, Object> getDataMap() {
            return ex.getDataMap();//base.getDataMap();
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public void run() {//关闭服务器
            try {
                super.run();
            }catch (ReportedException reportedexception) {
                CrashReport crashReport = MinecraftServer.constructOrExtractCrashReport(reportedexception);
                mcs.fillSystemReport(crashReport.getSystemReport());
                File file1 = new File(new File(mcs.getServerDirectory(), "crash-reports"), "crash-" + Util.getFilenameFormattedDateTime() + "-mcmt-server.txt");
                if (crashReport.saveToFile(file1)) {
                    LOGGER.error("This crash report has been saved to: {}", (Object)file1.getAbsolutePath());
                } else {
                    LOGGER.error("We were unable to save this crash report to disk.");
                }

                net.minecraftforge.server.ServerLifecycleHooks.expectServerStopped(); // Forge: Has to come before MinecraftServer#onServerCrash to avoid race conditions
                mcs.onServerCrash(crashReport);
                try {
                    mcs.stopped = true;
                    mcs.stopServer();
                } catch (Throwable throwable) {
                    LOGGER.error("Exception stopping the server", throwable);
                } finally {
                    if (mcs.services.profileCache() != null) {
                        mcs.services.profileCache().clearExecutor();
                    }

                    net.minecraftforge.server.ServerLifecycleHooks.handleServerStopped(mcs);
                    mcs.onServerExit();
                }
            }
        }

        @Override
        public String toString() {
            return getName();
        }
    }
    public static class WaitInt  {
        public volatile int size;
    }

    public static final Field busID;
    public static final int threadMax;
    public static final String thisRunTaskName = "Base.thisRunTaskName";

    public static ForkJoinPool getAsync() {
        return async;
    }

    static {
        int max = threadMax = Integer.getInteger("KMT-threadMax", Math.max(2, (int)(Runtime.getRuntime().availableProcessors() * 0.9)));
        CalculateTask.callMax = Integer.getInteger("KMT-callMax", Math.max(1, (int)(Runtime.getRuntime().availableProcessors() * 0.9)));
        ex = Base.setupThreadpool(threadMax, threadMax, Boolean.getBoolean("KMT-threadpool-async"));
        //if (Boolean.getBoolean("KMT-asyncEx")) {
            async = createThreadpool2(threadMax, threadMax, true, ex.getDataMap());
        //}
        Base.LOGGER.info("threadMax {}",threadMax);
        try {
            busID = EventBus.class.getDeclaredField("busID");
            busID.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        FixGetterRoot.cinit();
    }
}
