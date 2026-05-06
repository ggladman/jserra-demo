package server.service;

import server.model.RegisteredUser;

import java.math.BigDecimal;
import java.util.List;

public interface UserRegistryService {

    List<RegisteredUser> getRegisteredUsers();

    RegisteredUser addUser(String username);

    RegisteredUser findByUsername(String username);

    RegisteredUser findOrAddUser(String username);

    boolean transferBalance(String sender, String recipient, BigDecimal amount);

}
