package server.service;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomBalanceGeneratorImplTestManual {

    private final RandomIntegerGenerator randomIntegerGenerator = new RandomIntegerGeneratorImpl(new Random());
    private final RandomBalanceGenerator randomBalanceGenerator = new RandomBalanceGeneratorImpl(20, 200, randomIntegerGenerator);

    @Test
    public void generateRandomBalance() {
        final List<Integer> balances = new ArrayList<Integer>();

        for (int i = 0; i < 10; i++) {
            final int randomBalance = randomBalanceGenerator.generateRandomBalance(balances);
            balances.add(randomBalance);
            final int newSum = getSum(balances);
            System.out.println("\nsize, randomBalance, newSum = " + balances.size() + ", " + randomBalance + ", " + newSum);
            System.out.println("\t" + balances);
        }
    }

    private int getSum(final List<Integer> balances) {
        int sum = 0;

        for (final Integer balance : balances) {
            sum += balance;
        }

        return sum;
    }
}
