package server.service;

import java.util.List;

public class BalanceServiceImpl implements BalanceService {

    @Override
    public boolean isEvenlyBalanced(final List<Integer> balances) {
        if (balances.size() <= 1) {
            return true;
        }

        for (int i = 1; i < balances.size(); i++) {
            final Integer balance = balances.get(i);
            final Integer previousBalance = balances.get(i - 1);

            if (!balance.equals(previousBalance)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int average(final List<Integer> balances) {
        if (balances.isEmpty()) {
            return 0;
        }

        int sum = 0;

        for (final Integer balance : balances) {
            sum += balance;
        }

        return sum / balances.size();
    }

}
