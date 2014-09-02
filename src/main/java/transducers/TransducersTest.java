package transducers;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static transducers.Transducers.compose;
import static transducers.Transducers.filter;

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

        assertThat(ImmutableList.of("1", "1", "2", "2"), is(callValues));

    }

}