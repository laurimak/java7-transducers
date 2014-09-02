package transducers;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Test;

import javax.annotation.Nullable;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static transducers.Transducers.compose;
import static transducers.Transducers.filter;
import static transducers.Transducers.map;

public class TransducersTest {

    @Test
    public void functions_are_applied_lazily() {
        final List<String> callValues = new ArrayList<>();
        Predicate<String> p = new Predicate<String>() {
            @Override public boolean apply(@Nullable String input) {
                callValues.add(input);
                return true;
            }
        };

        Iterable<String> filtered = compose(filter(p), filter(p)).apply(ImmutableList.of("1", "2"));

        assertThat(callValues.isEmpty(), is(true));

        Lists.newArrayList(filtered);

        assertThat(callValues, is(list("1", "1", "2", "2")));
    }

    @Test
    public void map_and_filter_compose() {
        Function<Integer, Boolean> intToBool = new Function<Integer, Boolean>() {
            @Override public Boolean apply(Integer integer) {
                return !integer.equals(0);
            }
        };

        Predicate<Boolean> allTrue = new Predicate<Boolean>() {
            @Override public boolean apply(@Nullable Boolean input) {
                return input;
            }
        };

        ImmutableList<Boolean> result = ImmutableList.copyOf(compose(map(intToBool), filter(allTrue)).apply(list(1, 0, 1, 0)));

        assertThat(result, is(ImmutableList.of(true, true)));
    }

    private <T> List<T> list(T... items) {
        return ImmutableList.copyOf(items);
    }

}