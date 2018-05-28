package server.service;

import org.junit.Test;

import java.util.Random;

public class RandomBalanceGeneratorImplTestManual {

    private final RandomIntegerGenerator randomIntegerGenerator = new RandomIntegerGeneratorImpl(new Random());
    private final RandomBalanceGenerator randomBalanceGenerator = new RandomBalanceGeneratorImpl(20, 200, randomIntegerGenerator);

    @Test
    public void generateRandomBalance() {
        int currentSum = 0;

        for (int i = 0; i < 10; i++) {
            final int numberOfElements = i + 1;
            final int randomBalance = randomBalanceGenerator.generateRandomBalance(currentSum, numberOfElements);
            final int newSum = currentSum + randomBalance;
            System.out.println("numberOfElements, currentSum, randomBalance, newSum = " + numberOfElements + " " + currentSum + " " + randomBalance + " " + newSum);
            currentSum = newSum;
        }
    }
}
