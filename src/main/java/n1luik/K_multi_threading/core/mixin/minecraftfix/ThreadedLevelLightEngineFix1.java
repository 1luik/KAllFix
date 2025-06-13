package n1luik.K_multi_threading.core.mixin.minecraftfix;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import n1luik.KAllFix.util.AsyncWait;
import n1luik.K_multi_threading.core.Base;
import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Mixin(ThreadedLevelLightEngine.class)
@Deprecated
public abstract class ThreadedLevelLightEngineFix1 extends LevelLightEngine {
    @Shadow @Final private ChunkMap chunkMap;
    @Unique
    private ReentrantLock K_multi_threading$lock;
    @Unique
    private Condition K_multi_threading$condition;

    public ThreadedLevelLightEngineFix1(LightChunkGetter p_75805_, boolean p_75806_, boolean p_75807_) {
        super(p_75805_, p_75806_, p_75807_);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(LightChunkGetter p_9305_, ChunkMap p_9306_, boolean p_9307_, ProcessorMailbox p_9308_, ProcessorHandle p_9309_, CallbackInfo ci){
        K_multi_threading$lock = new ReentrantLock();
        K_multi_threading$condition = K_multi_threading$lock.newCondition();
    }

    @Redirect(method = "runUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/lighting/LevelLightEngine;runLightUpdates()I", opcode = Opcodes.INVOKESPECIAL))
    public int fix1(LevelLightEngine instance){
        if (chunkMap.level.getChunkSource() instanceof ParaServerChunkProvider paraServerChunkProvider && paraServerChunkProvider.isGeneratorWait()) {
            AsyncWait<Integer> integerAsyncWait = new AsyncWait<>(K_multi_threading$lock, K_multi_threading$condition, super::runLightUpdates);
            Base.getEx().execute(integerAsyncWait);
            integerAsyncWait.waitTask(
            //        ()->{
            //
            //}
            );
            return integerAsyncWait.getReturn_();
        }else {
            return super.runLightUpdates();
        }
    }
    //@Inject(method = "runUpdate", at = @At(value = "INVOKE", target = "Ljava/lang/Runnable;run()V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    //public void fix2(CallbackInfo ci, int i, ObjectListIterator objectlistiterator, int j, Pair pair){
    //    objectlistiterator.remove();
    //}
    //@Redirect(method = "runUpdate", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectListIterator;back(I)I"))
    //public int fix3(ObjectListIterator instance, int i){
    //    return i;
    //}
}
