package n1luik.KAllFix.mixin.ex.debug;

import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, priority = Integer.MIN_VALUE)
@Deprecated
public class FatalDebug1 {
    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;resetStatusCache(Lnet/minecraft/network/protocol/status/ServerStatus;)V"))
    private void debug1(CallbackInfo ci) {

        if (!Blocks.STONE.defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            throw new RuntimeException("Fatal error: Stone is not mineable with a pickaxe");
        }
    }
}
