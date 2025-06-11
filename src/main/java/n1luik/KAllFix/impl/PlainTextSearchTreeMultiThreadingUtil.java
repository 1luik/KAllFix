package n1luik.KAllFix.impl;

import mezz.jei.api.IModPlugin;
import n1luik.KAllFix.util.Args;
import n1luik.K_multi_threading.core.base.CalculateTask2;
import net.minecraft.Util;
import net.minecraft.client.searchtree.PlainTextSearchTree;
import net.minecraft.client.searchtree.SuffixArray;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class PlainTextSearchTreeMultiThreadingUtil {
    //public static BiFunction<List<?>, Function<?, Stream<String>>, PlainTextSearchTree<?>> call;
    //private static class Call{
    //    static {
    //        BiFunction<List<Object>, Function<Object, Stream<String>>, PlainTextSearchTree<Object>> kAllFix$create = Call::KAllFix$create;
    //        call = (BiFunction)kAllFix$create;
    //    }
    //    public static <T> PlainTextSearchTree<T> KAllFix$create(List<T> p_235198_, Function<T, Stream<String>> p_235199_) {
    //        return null;
    //    }
    //}
//
    ////通过asm生成
    //public static <T> PlainTextSearchTree<T> call(List<T> p_235198_, Function<T, Stream<String>> p_235199_) {
    //    if (call == null){
    //        try {
    //            Class.forName("n1luik.KAllFix.impl.PlainTextSearchTreeMultiThreadingUtil$Call", true, PlainTextSearchTreeMultiThreadingUtil.class.getClassLoader());
    //        } catch (ClassNotFoundException e) {
    //            throw new RuntimeException(e);
    //        }
    //    }
    //    return (PlainTextSearchTree<T>)call.apply(p_235198_, p_235199_);
    //}
    ////0, 1
    public static <T> Iterator<Stream<String>> mt(List<T> p_235198_, Function<T, Stream<String>> p_235199_) {
        Stream<String>[] ret = new Stream[p_235198_.size()];
        if (Util.backgroundExecutor() instanceof ForkJoinPool pool) {
            int length = ret.length;
            CalculateTask2 submit = new CalculateTask2(()->"PlainTextSearchTreeMultiThreading", 0, length, (i) -> {
                if (i < length) {
                    ret[i] = p_235199_.apply(p_235198_.get(i));
                }
            }, Args.PlainTextSearchTreeTaskMax);

            submit.call(pool);
        }else {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = p_235199_.apply(p_235198_.get(i));
            }
        }
        return  List.of(ret).iterator();
    }
    ////在应用mixin之后
    public static <T> PlainTextSearchTree<T> src(List<T> p_235198_, Function<T, Stream<String>> p_235199_) {
        if (p_235198_.isEmpty()) {
            return PlainTextSearchTree.empty();
        } else {

            SuffixArray<T> suffixarray = new SuffixArray<>();
            Iterator<T> iterator = p_235198_.iterator();
            Iterator<Stream<String>> mt = mt(p_235198_, p_235199_);//add
            while (iterator.hasNext()){
                mt.hasNext();//add
                T t = iterator.next();
                mt.next().forEach((p_235194_) -> {//add
                    suffixarray.add(t, p_235194_.toLowerCase(Locale.ROOT));
                });
            }

            suffixarray.generate();
            return suffixarray::search;
        }
    }
}
