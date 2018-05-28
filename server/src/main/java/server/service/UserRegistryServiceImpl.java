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
        // TODO (JJ) [now] Pass in sum of all balances (maybe just pass in the whole registered users list)
        final int randomBalance = randomBalanceGenerator.generateRandomBalance(1, registeredUsers.size() + 1);

        return new BigDecimal(randomBalance);
    }

}
