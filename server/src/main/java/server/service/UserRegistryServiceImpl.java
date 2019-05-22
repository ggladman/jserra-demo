package server.service;

import org.springframework.util.Assert;
import server.model.RegisteredUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class UserRegistryServiceImpl implements UserRegistryService {

    private final RandomBalanceGenerator randomBalanceGenerator;
    private final List<RegisteredUser> registeredUsers = new ArrayList<RegisteredUser>();

    UserRegistryServiceImpl(final RandomBalanceGenerator randomBalanceGenerator) {
        this.randomBalanceGenerator = randomBalanceGenerator;
    }

    @Override
    public List<RegisteredUser> getRegisteredUsers() {
        return registeredUsers;
    }

    @Override
    public RegisteredUser addUser(final String username) {
        assertUserDoesNotAlreadyExist(username);

        final BigDecimal randomBalance = generateRandomBalance();

        final RegisteredUser newRegisteredUser = new RegisteredUser();
        newRegisteredUser.setUsername(username);
        newRegisteredUser.setBalance(randomBalance);

        registeredUsers.add(newRegisteredUser);

        return newRegisteredUser;
    }

    @Override
    public RegisteredUser findByUsername(final String username) {
        for (final RegisteredUser user : registeredUsers) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }

        return null;
    }

    private void assertUserDoesNotAlreadyExist(final String username) {
        final RegisteredUser registeredUser = findByUsername(username);
        Assert.isNull(registeredUser, "User with username [" + username + "] already exists");
    }

    private BigDecimal generateRandomBalance() {
        final List<Integer> balances = getBalances();
        final int randomBalance = randomBalanceGenerator.generateRandomBalance(balances);

        return new BigDecimal(randomBalance);
    }

    private List<Integer> getBalances() {
        final List<Integer> balances = new ArrayList<Integer>();

        for (final RegisteredUser registeredUser : registeredUsers) {
            balances.add(registeredUser.getBalance().intValue());
        }

        return balances;
    }

}
