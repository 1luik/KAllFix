package n1luik.K_multi_threading.core.util;

import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.base.CalculateTask;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventListener;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

public class EventUtil {
    public static void runEvent(ForkJoinPool pool, IEventBus eventBus, Event event){
        runEvent(pool, eventBus, event, true);
    }

    public static void runEvent(ForkJoinPool pool, IEventBus eventBus, Event event, boolean wait){
        IEventListener[] listeners;
        try {
            listeners = event.getListenerList().getListeners(Base.busID.getInt(eventBus));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        //Base.WaitInt waitInt = new Base.WaitInt();
        //Base.ForkJoinPool_ ex = Base.getEx();

        //Base.getEx().getDataMap().put(Base.thisRunTaskName,name);
        //waitInt.size = listeners.length;

        int length = listeners.length;
        CalculateTask submit = (new CalculateTask(()->"Event["+event.getClass().getName()+"]", 0, length, (i) -> {
            if (i < length && !Objects.equals(listeners[i].getClass(), EventPriority.class)) listeners[i].invoke(event);
            //synchronized (waitInt) {
            //    waitInt.size--;
            //}
        }));

        if (wait) {
            submit.call(pool);
        }else {
            pool.submit(submit);//网上没有查到资料
        }


        //if (wait) {
        //    Base.getEx().getDataMap().put(Base.thisRunTaskName, null);
        //}
    }
}
