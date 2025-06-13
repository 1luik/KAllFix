package asm.n1luik.KAllFix.asm.util;

public class BetterGC extends Thread{

    public BetterGC() {
        setDaemon(true);
        start();
    }
    @Override
    public void run() {
        super.run();
    }
}
