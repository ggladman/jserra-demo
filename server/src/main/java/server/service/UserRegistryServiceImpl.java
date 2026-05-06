package server.service;

import org.springframework.util.Assert;
import server.model.RegisteredUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserRegistryServiceImpl implements UserRegistryService {

    private final RandomBalanceGenerator randomBalanceGenerator;
    private final List<RegisteredUser> registeredUsers = Collections.synchronizedList(new ArrayList<RegisteredUser>());

    UserRegistryServiceImpl(final RandomBalanceGenerator randomBalanceGenerator) {
        this.randomBalanceGenerator = randomBalanceGenerator;
    }

    @Override
    public synchronized List<RegisteredUser> getRegisteredUsers() {
        return new ArrayList<RegisteredUser>(registeredUsers);
    }

    @Override
    public synchronized RegisteredUser addUser(final String username) {
        assertUserDoesNotAlreadyExist(username);

        final BigDecimal randomBalance = generateRandomBalance();

        final RegisteredUser newRegisteredUser = new RegisteredUser();
        newRegisteredUser.setUsername(username);
        newRegisteredUser.setBalance(randomBalance);

        registeredUsers.add(newRegisteredUser);

        return newRegisteredUser;
    }

    @Override
    public synchronized RegisteredUser findByUsername(final String username) {
        for (final RegisteredUser user : registeredUsers) {
            if (username.equals(user.getUsername())) {
                return user;
            }
        }

        return null;
    }

    @Override
    public synchronized boolean transferBalance(final String sender, final String recipient, final BigDecimal amount) {
        final RegisteredUser senderUser = findByUsername(sender);
        final RegisteredUser recipientUser = findByUsername(recipient);

        if (senderUser == null || recipientUser == null) {
            return false;
        }

        senderUser.setBalance(senderUser.getBalance().subtract(amount));
        recipientUser.setBalance(recipientUser.getBalance().add(amount));
        return true;
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
