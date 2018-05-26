package server.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import server.model.RegisteredUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RegisteredUsersContext {

    @Value("${minimumInitialBalance:20.00}")
    private BigDecimal minimumInitialBalance;

    @Value("${maximumInitialBalance:200.00}")
    private BigDecimal maximumInitialBalance;

    private final List<RegisteredUser> registeredUsers = new ArrayList<RegisteredUser>();

    public List<RegisteredUser> getRegisteredUsers() {
        return registeredUsers;
    }

    public RegisteredUser addUser(final String username) {
        assertUserDoesNotAlreadyExist(username);

        final RegisteredUser newRegisteredUser = new RegisteredUser();
        newRegisteredUser.setUsername(username);
        newRegisteredUser.setBalance(BigDecimal.TEN);

        registeredUsers.add(newRegisteredUser);

        return newRegisteredUser;
    }

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

}
