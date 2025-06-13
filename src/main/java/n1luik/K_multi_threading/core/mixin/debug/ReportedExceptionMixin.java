package n1luik.K_multi_threading.core.mixin.debug;

import com.mojang.logging.LogUtils;
import n1luik.K_multi_threading.core.Base;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@Mixin(ReportedException.class)
public abstract class ReportedExceptionMixin extends RuntimeException{

    @Shadow public abstract String getMessage();

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(CrashReport p_134760_, CallbackInfo ci){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.printStackTrace(new PrintStream(byteArrayOutputStream));
        Base.LOGGER.error("未报错的Reported \n{}", byteArrayOutputStream.toString());
    }
}
