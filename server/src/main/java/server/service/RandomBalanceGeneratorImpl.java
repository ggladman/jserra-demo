package server.service;

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
    public int generateRandomBalance(final int commonDivisor) {
        final int randomInt = randomIntegerGenerator.nextInt(maximumInitialBalance + 1);
        int randomBalance = randomInt + minimumInitialBalance;

        while (randomBalance % commonDivisor != 0) {
            ++randomBalance;
        }

        return randomBalance;
    }

}
