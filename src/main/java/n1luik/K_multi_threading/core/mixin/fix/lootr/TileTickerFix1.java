package n1luik.K_multi_threading.core.mixin.fix.lootr;

import n1luik.K_multi_threading.core.Base;
import noobanidus.mods.lootr.block.entities.TileTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Base64;
import java.util.Iterator;

@Mixin(TileTicker.class)
public class TileTickerFix1 {
    //@Redirect(method = "serverTick", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", ordinal = 0))
    //private static boolean fix1(Iterator<noobanidus.mods.lootr.block.entities.TileTicker.Entry> iterator) {
    //    boolean b = iterator.hasNext();
    //    TileTicker.Entry next = iterator.next();
    //    Base.mcs.getLevel(next.)
    //    next.getPosition()
    //    return b;
    //}
}
