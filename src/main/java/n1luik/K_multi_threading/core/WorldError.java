package n1luik.K_multi_threading.core;

public class WorldError extends Exception{

    public WorldError() {
        super();
    }

    public WorldError(String message) {
        super(message);
    }

    public WorldError(String message, Throwable cause) {
        super(message, cause);
    }

    public WorldError(Throwable cause) {
        super(cause);
    }
}
