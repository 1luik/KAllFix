package n1luik.KAllFix.mixin.unsafe;

import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({ServerboundChatPacket.class, ServerboundChatCommandPacket.class})
public class ChatPacket256Mixin {
    @ModifyConstant(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", constant = @Constant(intValue = 256))
    private static int changeMaxChatLength(int original) {
        return 32767;
    }

    @ModifyConstant(method = "write", constant = @Constant(intValue = 256))
    private int changeMaxChatLength2(int original) {
        return 32767;
    }
}
