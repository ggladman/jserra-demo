package server.service;

public interface RandomIntegerGenerator {

    /**
     * Generates a random integer.
     *
     * @param bound the upper bound (exclusive).  Must be positive.
     * @return the next pseudorandom, uniformly distributed {@code int}
     * value between zero (inclusive) and {@code bound} (exclusive)
     */
    int nextInt(int bound);

}
