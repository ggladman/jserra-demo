package server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

public class RegisteredUser {
    private String username;
    private BigDecimal balance;
    private boolean newlyRegistered;

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(final BigDecimal balance) {
        this.balance = balance;
    }

    @JsonIgnore
    public boolean isNewlyRegistered() {
        return newlyRegistered;
    }

    public void setNewlyRegistered(final boolean newlyRegistered) {
        this.newlyRegistered = newlyRegistered;
    }
}
