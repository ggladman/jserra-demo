package server.service;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Collections;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

@RunWith(Theories.class)
public class BalanceServiceImplTest {

    @DataPoints
    public static Integer[] AMOUNTS = {1, 2};

    @Rule
    public final JUnitRuleMockery mockery = new JUnitRuleMockery();

    private final BalanceService balanceService = new BalanceServiceImpl();

    @Test
    public void testIsEvenlyBalanced_empty() {
        final boolean isEvenlyBalanced = balanceService.isEvenlyBalanced(Collections.<Integer>emptyList());
        assertThat(isEvenlyBalanced, is(true));
    }

    @Test
    public void testIsEvenlyBalanced_oneElement() {
        final boolean isEvenlyBalanced = balanceService.isEvenlyBalanced(singletonList(1));
        assertThat(isEvenlyBalanced, is(true));
    }

    @Theory
    public void testIsEvenlyBalanced_true(final Integer amount1, final Integer amount2, final Integer amount3) {
        assumeThat(amount1, is(amount2));
        assumeThat(amount2, is(amount3));

        final boolean isEvenlyBalanced = balanceService.isEvenlyBalanced(asList(amount1, amount2, amount3));
        assertThat(isEvenlyBalanced, is(true));
    }

    @Theory
    public void testIsEvenlyBalanced_false(final Integer amount1, final Integer amount2, final Integer amount3) {
        assumeThat(amount1, is(not(amount2)));

        final boolean isEvenlyBalanced = balanceService.isEvenlyBalanced(asList(amount1, amount2, amount3));
        assertThat(isEvenlyBalanced, is(false));
    }

    @Test
    public void testAverage_empty() {
        final int average = balanceService.average(Collections.<Integer>emptyList());
        assertThat(average, is(0));
    }

    @Test
    public void testAverage_twoIntegers() {
        final int average = balanceService.average(asList(1, 3));
        assertThat(average, is(2));
    }

    @Test
    public void testAverage_threeIntegers() {
        final int average = balanceService.average(asList(1, 3, 5));
        assertThat(average, is(3));
    }

    @Test
    public void testAverage_fourIntegers() {
        final int average = balanceService.average(asList(1, 3, 5, 11));
        assertThat(average, is(5));
    }

    @Test
    public void testAverage_twoIntegers_withFractionFloor() {
        final int average = balanceService.average(asList(1, 4));
        assertThat(average, is(2));
    }

}
