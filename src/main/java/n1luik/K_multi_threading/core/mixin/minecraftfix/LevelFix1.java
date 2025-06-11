package n1luik.K_multi_threading.core.mixin.minecraftfix;

import n1luik.KAllFix.util.VoidAsyncWait;
import n1luik.K_multi_threading.core.base.ParaServerChunkProvider;
import n1luik.K_multi_threading.core.util.concurrent.LockArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraftforge.common.util.BlockSnapshot;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(Level.class)
public abstract class LevelFix1 {
    @Shadow @Final private Thread thread;

    @Shadow public ArrayList<BlockSnapshot> capturedBlockSnapshots;
    @Shadow public boolean tickingBlockEntities;
    @Unique
    protected final Lock K_multi_threading$lock_FreshBlockEntities = new java.util.concurrent.locks.ReentrantLock();
    //@Unique
    //protected final Lock K_multi_threading$lock_FreshBlockEntities2 = new java.util.concurrent.locks.ReentrantLock();//用于保证tickBlockEntities是唯一的
    @Unique
    private ReentrantLock K_multi_threading$lock;
    @Unique
    private Condition K_multi_threading$condition;
    //通过IndependenceAddSynchronized_Asm保证安全
    @Unique
    private boolean K_multi_threading$isLock = false;//用于保证tickBlockEntities是唯一的

    @Redirect(method = "getBlockEntity",at = @At(value = "INVOKE",target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
    public Thread fix1(){
        return thread;
    }
    @Inject(method = "getBlockEntity",at = @At("HEAD"), cancellable = true)
    public void fix11(BlockPos p_46716_, CallbackInfoReturnable<BlockEntity> cir){
        if (((Object)this) instanceof ServerLevel serverLevel){
            if (serverLevel.getChunkSource() instanceof ParaServerChunkProvider paraServerChunkProvider) {
                if (paraServerChunkProvider.lightChunk == Thread.currentThread()) {
                    cir.setReturnValue(null);
                }
            }
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void fix2(WritableLevelData p_270739_, ResourceKey p_270683_, RegistryAccess p_270200_, Holder p_270240_, Supplier p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_, CallbackInfo ci){
        capturedBlockSnapshots = new LockArrayList<>();
        K_multi_threading$lock = new java.util.concurrent.locks.ReentrantLock();
        K_multi_threading$condition = K_multi_threading$lock.newCondition();
    }

    @Inject(method = "addFreshBlockEntities",at = @At("HEAD"), remap = false)
    public void fix3(Collection<BlockEntity> beList, CallbackInfo ci){
        if (K_multi_threading$lock_FreshBlockEntities.tryLock()){
            K_multi_threading$isLock = true;
        }
    }
    @Redirect(method = "addFreshBlockEntities",at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Level;tickingBlockEntities:Z"), remap = false)
    public boolean fix10(Level instance){
        return tickingBlockEntities || K_multi_threading$isLock;
    }

    @Inject(method = "addFreshBlockEntities",at = @At("RETURN"), remap = false)
    public void fix4(Collection<BlockEntity>  beList, CallbackInfo ci){
        if (K_multi_threading$isLock){
            K_multi_threading$lock_FreshBlockEntities.unlock();
            K_multi_threading$isLock = false;
        }
    }

    @Inject(method = "tickBlockEntities",at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
    public void fix5(CallbackInfo ci){
        //if (K_multi_threading$isLock) {
            K_multi_threading$lock_FreshBlockEntities.unlock();
        //}
    }
    //待调整
    @Redirect(method = "tickBlockEntities",at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;forEach(Ljava/util/function/Consumer;)V"))
    public <E> void fix9(ArrayList<E> instance, Consumer<? super E> consumer){
        //if (!K_multi_threading$isLock){

            if (((Object)this) instanceof ServerLevel serverLevel) {
                if (serverLevel.getChunkSource() instanceof ParaServerChunkProvider p) {
                    if (p.isGeneratorWait()) {
                        VoidAsyncWait task = new VoidAsyncWait(K_multi_threading$lock, K_multi_threading$condition, ()->instance.forEach(consumer));
                        p.KMT$genTestTickRun(task);
                        task.waitTask();
                    }else {
                        //K_multi_threading$lock_FreshBlockEntities.lock();
                        //K_multi_threading$isLock = true;
                        instance.forEach(consumer);
                    }
                }else {
                    //K_multi_threading$lock_FreshBlockEntities.lock();
                    //K_multi_threading$isLock = true;
                    instance.forEach(consumer);
                }
            }else {
                //K_multi_threading$lock_FreshBlockEntities.lock();
                //K_multi_threading$isLock = true;
                instance.forEach(consumer);
            }
        //}else {
        //    K_multi_threading$lock_FreshBlockEntities.lock();
        //    K_multi_threading$isLock = true;
        //    instance.forEach(consumer);
        //}
    }
    @Inject(method = "tickBlockEntities",at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;isEmpty()Z", ordinal = 0))
    public void fix6(CallbackInfo ci){
        //if (((Object)this) instanceof ServerLevel serverLevel) {
        //    if (serverLevel.getChunkSource() instanceof ParaServerChunkProvider p) {
        //        if (!p.isGeneratorWait()) {
        //            K_multi_threading$lock_FreshBlockEntities.lock();
        //            K_multi_threading$isLock = true;
        //        }
        //    }else {
        //        K_multi_threading$lock_FreshBlockEntities.lock();
        //        K_multi_threading$isLock = true;
        //    }
        //}else {
            K_multi_threading$lock_FreshBlockEntities.lock();
        //    K_multi_threading$isLock = true;
        //}
    }
    //@Inject(method = "tickBlockEntities",at = @At("RETURN"))
    //public void fix7(CallbackInfo ci){
    //    K_multi_threading$lock_FreshBlockEntities2.unlock();
    //}
    //@Inject(method = "tickBlockEntities",at = @At("HEAD"))
    //public void fix8(CallbackInfo ci){
    //    K_multi_threading$lock_FreshBlockEntities2.lock();
    //}

}
