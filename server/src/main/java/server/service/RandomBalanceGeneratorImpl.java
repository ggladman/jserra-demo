package server.service;

import java.util.List;

public class RandomBalanceGeneratorImpl implements RandomBalanceGenerator {

    private final int minimumInitialBalance;
    private final int maximumInitialBalance;
    private final RandomIntegerGenerator randomIntegerGenerator;

    RandomBalanceGeneratorImpl(final int minimumInitialBalance, final int maximumInitialBalance, final RandomIntegerGenerator randomIntegerGenerator) {
        this.minimumInitialBalance = minimumInitialBalance;
        this.maximumInitialBalance = maximumInitialBalance;
        this.randomIntegerGenerator = randomIntegerGenerator;
    }

    @Override
    public int generateRandomBalance(final List<Integer> currentBalances) {
        final int currentSum = getSum(currentBalances);
        final int commonDivisor = currentBalances.size() + 1;

        int randomBalance = randomIntegerGenerator.nextInt(maximumInitialBalance - minimumInitialBalance + 1) + minimumInitialBalance;

        while ((currentSum + randomBalance) % commonDivisor != 0) {
            ++randomBalance;
        }

        return randomBalance;
    }

    private int getSum(final List<Integer> balances) {
        int sum = 0;

        for (final Integer balance : balances) {
            sum += balance;
        }

        return sum;
    }

}
