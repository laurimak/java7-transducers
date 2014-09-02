package transducers;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;

public class Transducers {
    public interface Transducer<P, R> extends Function<Iterable<P>, Iterable<R>> {
    }

    public static <T> Transducer<T, T> filter(final Predicate<? super T> predicate) {
        return new Transducer<T, T>() {
            @Nullable @Override public Iterable<T> apply(@Nullable Iterable<T> input) {
                return Iterables.filter(input, predicate);
            }
        };
    }

    public static <T> Iterable<T> filter(Predicate<? super T> predicate, Iterable<T> iterable) {
        return filter(predicate).apply(iterable);
    }

    public static <P, R> Transducer<P, R> map(final Function<? super P, ? extends R> mapping) {
        return new Transducer<P, R>() {
            @Nullable @Override public Iterable<R> apply(@Nullable Iterable<P> input) {
                return Iterables.transform(input, mapping);
            }
        };
    }

    public static <P, R> Iterable<R> map(Function<? super P, ? extends R> mapping, Iterable<P> iterable) {
        return map(mapping).apply(iterable);
    }

    public static <P, R, S> Transducer<P, R> compose(final Transducer<P, S> t1, final Transducer<S, R> t2) {
        return new Transducer<P, R>() {
            @Nullable @Override public Iterable<R> apply(@Nullable Iterable<P> input) {
                return t2.apply(t1.apply(input));
            }
        };
    }
}