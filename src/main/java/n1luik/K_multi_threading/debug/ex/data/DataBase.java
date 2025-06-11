package n1luik.K_multi_threading.debug.ex.data;

import java.util.ArrayList;
import java.util.List;

public class DataBase {
    public JvmCallLogData data;
    public long time;
    public List<DataBase> next;

    public DataBase() {
        this.next = new ArrayList<>(1);
        this.data = JvmCallLogData.EMPTY;
        this.time = -1;
    }

    public DataBase(JvmCallLogData data, long time, List<DataBase> next) {
        this.data = data;
        this.time = time;
        this.next = next;
    }

    public DataBase copy() {
        return new DataBase(data.copy(), time, new ArrayList<>(next.stream().map(DataBase::copy).toList()));
    }

    public void sum(DataBase data) {
        for (DataBase dataBase : data.next) {
            add(dataBase);
        }
    }
    /**
     * 合并并不会复制next的数据如果是添加其他数据需要{@code copy()}方法
     * */
    public void add(DataBase data) {
        for (DataBase dataBase : next) {
            if (dataBase.data.equals(data.data)) {
                dataBase.time += data.time;
                for (DataBase base : data.next) {
                    dataBase.add(base);
                }
                return;
            }
        }
        next.add(data);
    }


}
