package server.service;

import java.util.Random;

public class RandomIntegerGeneratorImpl implements RandomIntegerGenerator {

    private final Random random;

    RandomIntegerGeneratorImpl(final Random random) {
        this.random = random;
    }

    @Override
    public int nextInt(final int bound) {
        return random.nextInt(bound);
    }

}
