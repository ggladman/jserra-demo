package server.service;

import java.util.List;

public interface BalanceService {

    boolean isEvenlyBalanced(List<Integer> balances);

    int average(List<Integer> balances);

}
