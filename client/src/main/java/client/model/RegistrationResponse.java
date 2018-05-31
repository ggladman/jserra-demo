package client.model;

import java.math.BigDecimal;
import java.util.List;

public class RegistrationResponse {

    private String username;
    private BigDecimal balance;
    private BigDecimal averageBalance;
    private List<RegisteredUser> registeredUsers;

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getAverageBalance() {
        return averageBalance;
    }

    public void setAverageBalance(BigDecimal averageBalance) {
        this.averageBalance = averageBalance;
    }

    public void setBalance(final BigDecimal balance) {
        this.balance = balance;
    }

    public List<RegisteredUser> getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(final List<RegisteredUser> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }
}
