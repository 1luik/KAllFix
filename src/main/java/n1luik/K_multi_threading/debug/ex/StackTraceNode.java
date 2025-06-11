package n1luik.K_multi_threading.debug.ex;

import n1luik.K_multi_threading.debug.ex.data.DataBase;
import n1luik.K_multi_threading.debug.ex.data.JvmCallLogData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class StackTraceNode {
    public long time = 0;
    private final HashMap<StackTraceNode.Description, StackTraceNode> children = new HashMap<>();

    public StackTraceNode(){

    }

    public StackTraceNode(Description description) {
    }

    public DataBase save(){
        List<DataBase> list = new ArrayList<>(children.size());
        Map.Entry<Description, StackTraceNode>[] array = children.entrySet().toArray(new Map.Entry[0]);
        for (Map.Entry<Description, StackTraceNode> descriptionStackTraceNodeEntry : array) {
            DataBase save = descriptionStackTraceNodeEntry.getValue().save();
            save.data = descriptionStackTraceNodeEntry.getKey().save();
            list.add(save);
        }
        return new DataBase(JvmCallLogData.EMPTY, time, list);
    }

    public StackTraceNode resolveChild(StackTraceNode.Description description) {
        StackTraceNode result = this.children.get(description);
        return result != null ? result : this.children.computeIfAbsent(description, StackTraceNode::new);
    }

    public static final class Description {
        private final String className;
        private final String methodName;
        //private final String methodDescription;
        private final int lineNumber;
        private final int parentLineNumber;
        private final int hash;

        public Description(String className, String methodName, int lineNumber, int parentLineNumber) {
            this.className = className;
            this.methodName = methodName;
            //this.methodDescription = null;
            this.lineNumber = lineNumber;
            this.parentLineNumber = parentLineNumber;
            this.hash = Objects.hash(new Object[]{this.className, this.methodName, this.lineNumber, this.parentLineNumber});
        }

        //public Description(String className, String methodName, String methodDescription) {
        //    this.className = className;
        //    this.methodName = methodName;
        //    this.methodDescription = methodDescription;
        //    this.lineNumber = -1;
        //    this.parentLineNumber = -1;
        //    this.hash = Objects.hash(new Object[]{this.className, this.methodName, this.methodDescription});
        //}

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o != null && this.getClass() == o.getClass()) {
                Description description = (Description)o;
                return this.hash == description.hash && this.lineNumber == description.lineNumber && this.parentLineNumber == description.parentLineNumber && this.className.equals(description.className) && this.methodName.equals(description.methodName);// && Objects.equals(this.methodDescription, description.methodDescription);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.hash;
        }

        public JvmCallLogData save() {
            return new JvmCallLogData(this.className, this.methodName, this.lineNumber, this.parentLineNumber);
        }
    }
}
