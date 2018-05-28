package server.service;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class RandomBalanceGeneratorImplTest {

    @DataPoint
    public static final FixtureData FIXTURE_DATA_1 = new FixtureData()
            .minimumInitialBalance(0)
            .maximumInitialBalance(2)
            .nextRandomIntToReturn(0)
            .currentSum(0)
            .commonDivisor(1)
            .expectedRandomBalance(0);

    @DataPoint
    public static final FixtureData FIXTURE_DATA_2 = new FixtureData()
            .minimumInitialBalance(0)
            .maximumInitialBalance(5)
            .nextRandomIntToReturn(2)
            .currentSum(3)
            .commonDivisor(2)
            .expectedRandomBalance(3);

    @DataPoint
    public static final FixtureData FIXTURE_DATA_3 = new FixtureData()
            .minimumInitialBalance(2)
            .maximumInitialBalance(5)
            .nextRandomIntToReturn(2)
            .currentSum(3)
            .commonDivisor(2)
            .expectedRandomBalance(5);

    @DataPoint
    public static final FixtureData FIXTURE_DATA_4 = new FixtureData()
            .minimumInitialBalance(3)
            .maximumInitialBalance(5)
            .nextRandomIntToReturn(4)
            .currentSum(3)
            .commonDivisor(2)
            .expectedRandomBalance(7);

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

        final int randomBalance = randomBalanceGenerator.generateRandomBalance(fixtureData.currentSum, fixtureData.commonDivisor);
        assertThat(randomBalance, is(fixtureData.expectedRandomBalance));
    }
}

class FixtureData {
    int minimumInitialBalance;
    int maximumInitialBalance;
    int nextRandomIntToReturn;
    int currentSum;
    int commonDivisor;
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

    FixtureData currentSum(final int currentSum) {
        this.currentSum = currentSum;
        return this;
    }

    FixtureData commonDivisor(final int commonDivisor) {
        this.commonDivisor = commonDivisor;
        return this;
    }

    FixtureData expectedRandomBalance(final int expectedRandomBalance) {
        this.expectedRandomBalance = expectedRandomBalance;
        return this;
    }
}


