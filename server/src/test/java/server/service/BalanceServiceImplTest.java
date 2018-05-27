package server.service;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeThat;

@RunWith(Theories.class)
public class BalanceServiceImplTest {

    @DataPoints
    public static BigDecimal[] AMOUNTS = {ONE, TEN};

    @Rule
    public final JUnitRuleMockery mockery = new JUnitRuleMockery();

    private final BalanceService balanceService = new BalanceServiceImpl();

    @Test
    public void testIsEvenlyBalanced_empty() {
        final boolean isEvenlyBalanced = balanceService.isEvenlyBalanced(Collections.<BigDecimal>emptyList());
        assertThat(isEvenlyBalanced, is(true));
    }

    @Test
    public void testIsEvenlyBalanced_oneElement() {
        final boolean isEvenlyBalanced = balanceService.isEvenlyBalanced(singletonList(ONE));
        assertThat(isEvenlyBalanced, is(true));
    }

    @Theory
    public void testIsEvenlyBalanced_true(final BigDecimal amount1, final BigDecimal amount2, final BigDecimal amount3) {
        assumeThat(amount1.compareTo(amount2), is(0));
        assumeThat(amount2.compareTo(amount3), is(0));

        final boolean isEvenlyBalanced = balanceService.isEvenlyBalanced(Arrays.asList(amount1, amount2, amount3));
        assertThat(isEvenlyBalanced, is(true));
    }

    @Theory
    public void testIsEvenlyBalanced_false(final BigDecimal amount1, final BigDecimal amount2, final BigDecimal amount3) {
        assumeThat(amount1.compareTo(amount2), is(not(0)));

        final boolean isEvenlyBalanced = balanceService.isEvenlyBalanced(Arrays.asList(amount1, amount2, amount3));
        assertThat(isEvenlyBalanced, is(false));
    }

}
