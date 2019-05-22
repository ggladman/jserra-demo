package server.service;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class RandomBalanceGeneratorImplTest {

    @DataPoint
    public static final FixtureData FIXTURE_DATA_1 = new FixtureData()
            .minimumInitialBalance(0)
            .maximumInitialBalance(2)
            .nextRandomIntToReturn(0) // Initial random balance will be 0 + 0 = 0
            .currentBalances(Collections.<Integer>emptyList()) // 0; Need to get to multiple of 2 (0)
            .expectedRandomBalance(0);

    @DataPoint
    public static final FixtureData FIXTURE_DATA_2 = new FixtureData()
            .minimumInitialBalance(0)
            .maximumInitialBalance(5)
            .nextRandomIntToReturn(2) // Initial random balance will be 2 + 0 = 2
            .currentBalances(singletonList(3)) // 3; Need to get to multiple of 2 (6)
            .expectedRandomBalance(3); // Add 1 to initial random balance (2) to get to multiple of 2 (6)

    @DataPoint
    public static final FixtureData FIXTURE_DATA_3 = new FixtureData()
            .minimumInitialBalance(2)
            .maximumInitialBalance(5)
            .nextRandomIntToReturn(2) // Initial random balance will be 2 + 2 = 4
            .currentBalances(singletonList(3)) // 3; Need to get to multiple of 2 (8)
            .expectedRandomBalance(5); // Add 1 to initial random balance (4) to get to multiple of 2 (8)

    @DataPoint
    public static final FixtureData FIXTURE_DATA_4 = new FixtureData()
            .minimumInitialBalance(3)
            .maximumInitialBalance(5)
            .nextRandomIntToReturn(4) // Initial random balance will be 4 + 3 = 7
            .currentBalances(singletonList(3)) // 3; Need to get to multiple of 2 (10)
            .expectedRandomBalance(7); // Initial random balance (7) is good

    @DataPoint
    public static final FixtureData FIXTURE_DATA_5 = new FixtureData()
            .minimumInitialBalance(3)
            .maximumInitialBalance(5)
            .nextRandomIntToReturn(4) // Initial random balance will be 4 + 3 = 7
            .currentBalances(asList(2, 3, 4, 5)) // 14; Need to get to multiple of 5 (25)
            .expectedRandomBalance(11); // Add 4 to initial random balance (7) to get to multiple of 5 (25)

    @Rule
    public final JUnitRuleMockery mockery = new JUnitRuleMockery();

    private final RandomIntegerGenerator randomIntegerGenerator = mockery.mock(RandomIntegerGenerator.class);

    @Theory
    public void testGenerateRandomBalance(final FixtureData fixtureData) {
        final RandomBalanceGenerator randomBalanceGenerator
                = new RandomBalanceGeneratorImpl(fixtureData.minimumInitialBalance, fixtureData.maximumInitialBalance, randomIntegerGenerator);

        mockery.checking(new Expectations() {{
            oneOf(randomIntegerGenerator).nextInt(fixtureData.maximumInitialBalance - fixtureData.minimumInitialBalance + 1);
            will(returnValue(fixtureData.nextRandomIntToReturn));
        }});

        final int randomBalance = randomBalanceGenerator.generateRandomBalance(fixtureData.currentBalances);
        assertThat(randomBalance, is(fixtureData.expectedRandomBalance));
    }
}

class FixtureData {
    int minimumInitialBalance;
    int maximumInitialBalance;
    int nextRandomIntToReturn;
    List<Integer> currentBalances;
    int expectedRandomBalance;

    FixtureData minimumInitialBalance(final int minimumInitialBalance) {
        this.minimumInitialBalance = minimumInitialBalance;
        return this;
    }

    FixtureData maximumInitialBalance(final int maximumInitialBalance) {
        this.maximumInitialBalance = maximumInitialBalance;
        return this;
    }

    FixtureData nextRandomIntToReturn(final int nextRandomIntToReturn) {
        this.nextRandomIntToReturn = nextRandomIntToReturn;
        return this;
    }

    FixtureData currentBalances(final List<Integer> currentBalances) {
        this.currentBalances = currentBalances;
        return this;
    }

    FixtureData expectedRandomBalance(final int expectedRandomBalance) {
        this.expectedRandomBalance = expectedRandomBalance;
        return this;
    }
}


