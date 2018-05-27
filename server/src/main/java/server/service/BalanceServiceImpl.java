package server.service;

import java.math.BigDecimal;
import java.util.List;

public class BalanceServiceImpl implements BalanceService {

    @Override
    public boolean isEvenlyBalanced(final List<BigDecimal> balances) {
        if (balances.size() <= 1) {
            return true;
        }

        for (int i = 1; i < balances.size(); i++) {
            final BigDecimal balance = balances.get(i);
            final BigDecimal previousBalance = balances.get(i - 1);

            if (balance.compareTo(previousBalance) != 0) {
                return false;
            }
        }

        return true;
    }

}
