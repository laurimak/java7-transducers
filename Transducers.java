import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class Transducers {
    public interface Transducer<P, R> extends Function<Iterable<P>, Iterable<R>> {
        Function<P, Optional<R>> function();
    }

    public static <T> Transducer<T, T> filter(Predicate<T> predicate) {
        return new FilterTransducer<>(predicate);
    }

    public static <T> Iterable<T> filter(Predicate<T> predicate, Iterable<T> iterable) {
        return filter(predicate).apply(iterable);
    }

    public static <P, R> Transducer<P, R> map(Function<P, R> mapping) {
        return new MappingTransducer(mapping);
    }

    public static <P, R> Iterable<R> map(Function<P, R> mapping, Iterable<P> iterable) {
        return map(mapping).apply(iterable);
    }

    public static <P, R, S> Transducer<P, R> compose(Transducer<P, S> t1, Transducer<S, R> t2) {
        return new Composition<>(t1.function(), t2.function());
    }

    private static final Object NULL_VALUE = new Object();

    private static <T> T nullValue() {
        return (T) NULL_VALUE;
    }


    private static <T> T value(Optional<T> optional) {
        return optional.get() == nullValue() ? null : optional.get();
    }

    private static <T> Optional<T> optional(T value) {
        if (value == null) {
            return nullValue();
        } else {
            return Optional.of(value);
        }
    }
    static class FilterTransducer<T> implements Transducer<T, T> {

        private final Predicate<T> predicate;


        private final Function<T, Optional<T>> function;

        FilterTransducer(final Predicate<T> predicate) {
            this.predicate = predicate;
            this.function = new Function<T, Optional<T>>() {
                @Override
                public Optional<T> apply(@Nullable T input) {
                    if (predicate.apply(input)) {
                        return Optional.of(input == null ? (T) nullValue() : input);
                    } else {
                        return Optional.absent();
                    }
                }
            };
        }
        @Override
        public Iterable<T> apply(Iterable<T> input) {
            return Iterables.filter(input, predicate);
        }


        @Override
        public Function<T, Optional<T>> function() {
            return function;
        }

    }
    static class MappingTransducer<P, R> implements Transducer<P, R> {

        private final Function<P, R> mappingFunction;


        private final Function<P, Optional<R>> function;

        MappingTransducer(final Function<P, R> mappingFunction) {
            this.mappingFunction = mappingFunction;
            this.function = new Function<P, Optional<R>>() {
                @Override
                public Optional<R> apply(@Nullable P input) {
                    return optional(mappingFunction.apply(input));
                }
            };
        }
        @Override
        public Iterable<R> apply(Iterable<P> input) {
            return Iterables.transform(input, mappingFunction);
        }



        @Override
        public Function<P, Optional<R>> function() {
            return function;
        }

    }

    static class Composition<P, R, S> implements Function<Iterable<P>, Iterable<R>>, Transducer<P, R> {
        private Function<P, Optional<R>> function;

        Composition(final Function<P, Optional<S>> f1, final Function<S, Optional<R>> f2) {
            this.function = new Function<P, Optional<R>>() {
                @Override
                public Optional<R> apply(@Nullable P p) {
                    Optional<S> r1 = f1.apply(p);

                    if (r1.isPresent()) {
                        return f2.apply(r1.get());
                    }

                    return Optional.absent();
                }
            };
        }

        public Iterable<R> apply(Iterable<P> param) {
            ArrayList<R> result = new ArrayList<>();
            for (P p : param) {
                Optional<R> itemToAdd = function().apply(p);

                if (itemToAdd.isPresent()) {
                    result.add(value(itemToAdd));
                }
            }

            return result;
        }

        @Override
        public Function<P, Optional<R>> function() {
            return function;
        }
    }

}
