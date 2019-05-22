package server.service;

import java.util.List;

public interface RandomBalanceGenerator {

    int generateRandomBalance(final List<Integer> currentBalances);

}
