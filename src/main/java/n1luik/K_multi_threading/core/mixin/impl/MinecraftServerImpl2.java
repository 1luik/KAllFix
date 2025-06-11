package n1luik.K_multi_threading.core.mixin.impl;

import com.mojang.datafixers.DataFixer;
import n1luik.K_multi_threading.core.UnsafeEnable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.concurrent.locks.LockSupport;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerImpl2 {
    @Shadow private PlayerList playerList;

    @Shadow public abstract boolean isRunning();

    @Unique
    private Thread K_multi_threading$threadIndependencePlayer;
    @Unique
    private volatile boolean K_multi_threading$threadIndependencePlayerTest;
    @Unique
    private volatile Throwable K_multi_threading$threadIndependencePlayerThrowable;
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(Thread p_236723_, LevelStorageSource.LevelStorageAccess p_236724_, PackRepository p_236725_, WorldStem p_236726_, Proxy p_236727_, DataFixer p_236728_, Services p_236729_, ChunkProgressListenerFactory p_236730_, CallbackInfo ci) {
        if (UnsafeEnable.INSTANCE.IndependencePlayer) {
            K_multi_threading$threadIndependencePlayer = new Thread(() -> {
                while (isRunning()){
                    K_multi_threading$threadIndependencePlayerTest = true;
                    try{
                        this.playerList.tick();
                    }catch (Throwable t){
                        K_multi_threading$threadIndependencePlayerThrowable = t;
                    }
                    K_multi_threading$threadIndependencePlayerTest = false;
                    LockSupport.park();
                }
            });
            K_multi_threading$threadIndependencePlayer.start();
        }
    }

    @Redirect(method = "tickChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;tick()V"))
    public void tickChildren(PlayerList playerList) {
        if (K_multi_threading$threadIndependencePlayerTest) throw new RuntimeException("K_multi_threading$threadIndependencePlayerTest");
        LockSupport.unpark(K_multi_threading$threadIndependencePlayer);
    }
    @Inject(method = "tickChildren", at = @At("RETURN"))
    public void tickChildren(CallbackInfo ci) {
        while (K_multi_threading$threadIndependencePlayerTest) Thread.onSpinWait();
        if (K_multi_threading$threadIndependencePlayerThrowable != null) throw new RuntimeException(K_multi_threading$threadIndependencePlayerThrowable);
    }

}
