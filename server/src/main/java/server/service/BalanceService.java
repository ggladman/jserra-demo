package server.service;

import java.math.BigDecimal;
import java.util.List;

public interface BalanceService extends RandomBalanceGenerator {

    boolean isEvenlyBalanced(List<BigDecimal> balances);

}
