package n1luik.KAllFix.mixin.unsafe.path.PlainTextSearchTreeMultiThreading;

import n1luik.KAllFix.impl.PlainTextSearchTreeMultiThreadingUtil;
import net.minecraft.Util;
import net.minecraft.client.searchtree.FullTextSearchTree;
import net.minecraft.client.searchtree.PlainTextSearchTree;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(FullTextSearchTree.class)
public class FullTextSearchTreeMixin {
    @Redirect(method = "refresh", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/searchtree/PlainTextSearchTree;create(Ljava/util/List;Ljava/util/function/Function;)Lnet/minecraft/client/searchtree/PlainTextSearchTree;"))
    public <T> PlainTextSearchTree<T> impl1(List<T> t, Function<T, Stream<String>> suffixarray) {
        if (Util.backgroundExecutor() instanceof ForkJoinPool) {
            return PlainTextSearchTreeMultiThreadingUtil.src(t, suffixarray);
        }else {
            return PlainTextSearchTree.create(t, suffixarray);
        }
    }
}
