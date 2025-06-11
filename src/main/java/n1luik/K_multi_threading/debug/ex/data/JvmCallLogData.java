package n1luik.K_multi_threading.debug.ex.data;

import java.util.Objects;

public class JvmCallLogData {
    public static final JvmCallLogData EMPTY = new JvmCallLogData("", "", -1);
    public String declaringClass;
    public String method;
    public int lineNumber;
    public int parentLineNumber;
    public JvmCallLogData(String declaringClass, String method, int lineNumber) {
        this.declaringClass = declaringClass;
        this.method = method;
        this.lineNumber = lineNumber;
        this.parentLineNumber = -1;
    }
    public JvmCallLogData(String declaringClass, String method, int lineNumber, int parentLineNumber) {
        this.declaringClass = declaringClass;
        this.method = method;
        this.lineNumber = lineNumber;
        this.parentLineNumber = parentLineNumber;
    }
    public JvmCallLogData(){

    }

    public JvmCallLogData copy() {
        return new JvmCallLogData(declaringClass, method, lineNumber, parentLineNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JvmCallLogData that)) return false;
        return lineNumber == that.lineNumber && Objects.equals(declaringClass, that.declaringClass) && Objects.equals(method, that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(declaringClass, method, lineNumber);
    }
}
