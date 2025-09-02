package n1luik.K_multi_threading.core.mixin.k;

import com.mojang.brigadier.CommandDispatcher;
import n1luik.KAllFix.debug.KAFGetterChunkTagCommand;
import n1luik.K_multi_threading.debug.GetterClassFileCommand;
import n1luik.K_multi_threading.debug.SetterWorldConfigCommand;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public class CommandsMixin {
    @Shadow @Final private CommandDispatcher<CommandSourceStack> dispatcher;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(Commands.CommandSelection p_230943_, CommandBuildContext p_230944_, CallbackInfo ci){
        KAFGetterChunkTagCommand.register(this.dispatcher);
        GetterClassFileCommand.register(this.dispatcher);
        SetterWorldConfigCommand.register(this.dispatcher, p_230944_);
    }
}
