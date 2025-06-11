package n1luik.K_multi_threading.debug.mixin;

import com.mojang.brigadier.CommandDispatcher;
import n1luik.K_multi_threading.debug.ex.data.KMTDebugCommand;
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
        KMTDebugCommand.register(this.dispatcher);
    }
}
