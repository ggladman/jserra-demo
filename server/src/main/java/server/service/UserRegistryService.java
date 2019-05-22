package server.service;

import server.model.RegisteredUser;

import java.util.List;

public interface UserRegistryService {

    List<RegisteredUser> getRegisteredUsers();

    RegisteredUser addUser(String username);

    RegisteredUser findByUsername(String username);

}
