package n1luik.K_multi_threading.core.mixin.debug;

import com.mojang.datafixers.util.Either;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;

@Mixin(value = TrackGraph.class,priority = 1100)
public class TrackGraphDebug {

    @Inject(method = "addNode",at = @At("HEAD"),remap = false)
    public void debug1(TrackNode node, CallbackInfo ci){
        Objects.requireNonNull(node);
    }
}
