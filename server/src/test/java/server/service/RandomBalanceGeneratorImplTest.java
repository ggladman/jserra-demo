package server.service;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

@RunWith(Theories.class)
public class RandomBalanceGeneratorImplTest {

    @DataPoints
    public static final int[] INTEGERS = {1, 2, 3, 4, 5};

    @Rule
    public final JUnitRuleMockery mockery = new JUnitRuleMockery();

    private final RandomIntegerGenerator randomIntegerGenerator = mockery.mock(RandomIntegerGenerator.class);
    private RandomBalanceGenerator randomBalanceGenerator;

    @Theory
    public void testGenerateRandomBalance(final int minimumInitialBalance,
                                          final int maximumInitialBalance,
                                          final int commonDivisor,
                                          final int nextIntToReturn) {
        assumeThat(minimumInitialBalance < maximumInitialBalance, is(true));
        assumeThat(nextIntToReturn >= minimumInitialBalance, is(true));
        assumeThat(nextIntToReturn <= maximumInitialBalance, is(true));

        randomBalanceGenerator = new RandomBalanceGeneratorImpl(minimumInitialBalance, maximumInitialBalance, randomIntegerGenerator);

        mockery.checking(new Expectations() {{
            oneOf(randomIntegerGenerator).nextInt(maximumInitialBalance + 1);
            will(returnValue(nextIntToReturn));
        }});

        final int randomBalance = randomBalanceGenerator.generateRandomBalance(commonDivisor);

//        System.out.println();
//        System.out.println("minimumInitialBalance = " + minimumInitialBalance);
//        System.out.println("maximumInitialBalance = " + maximumInitialBalance);
//        System.out.println("commonDivisor = " + commonDivisor);
//        System.out.println("nextIntToReturn = " + nextIntToReturn);
//        System.out.println("randomBalance = " + randomBalance);

        assertThat(randomBalance >= minimumInitialBalance, is(true));
        assertThat(randomBalance % commonDivisor, is(0));
    }
}
