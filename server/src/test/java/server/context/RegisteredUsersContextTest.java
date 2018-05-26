package server.context;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import server.model.RegisteredUser;

import java.util.List;

import static java.math.BigDecimal.ONE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class RegisteredUsersContextTest {

    @DataPoints
    public static final String[] STRINGS = {"foo", "bar"};

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final RegisteredUsersContext registeredUsersContext = new RegisteredUsersContext();

    @Test
    public void testGetRegisteredUsers() {
        final List<RegisteredUser> registeredUsers = registeredUsersContext.getRegisteredUsers();
        assertThat(registeredUsers, is(notNullValue()));
        assertThat(registeredUsers.isEmpty(), is(true));
    }

    @Theory
    public void testAddUser(final String username) {
        final String username1 = username + "-1";
        final String username2 = username + "-2";

        registeredUsersContext.addUser(username1);
        registeredUsersContext.addUser(username2);

        final List<RegisteredUser> registeredUsers = registeredUsersContext.getRegisteredUsers();
        assertThat(registeredUsers.size(), is(2));

        final RegisteredUser registeredUser1 = registeredUsers.get(0);
        assertThat(registeredUser1.getUsername(), is(username1));
        assertThat(registeredUser1.getBalance(), is(notNullValue()));
        assertThat(registeredUser1.getBalance().compareTo(ONE), is(greaterThanOrEqualTo(0)));

        final RegisteredUser registeredUser2 = registeredUsers.get(1);
        assertThat(registeredUser2.getUsername(), is(username2));
        assertThat(registeredUser2.getBalance(), is(notNullValue()));
        assertThat(registeredUser2.getBalance().compareTo(ONE), is(greaterThanOrEqualTo(0)));
    }

    @Theory
    public void testAddUser_duplicate(final String username) {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User with username [" + username + "] already exists");

        registeredUsersContext.addUser(username);
        registeredUsersContext.addUser(username);
    }

    @Theory
    public void testFindByUsername_NotFound(final String username) {
        final RegisteredUser registeredUser = registeredUsersContext.findByUsername(username);
        assertThat(registeredUser, is(nullValue()));
    }

    @Theory
    public void testFindByUsername(final String username) {
        final RegisteredUser registeredUserAdded = registeredUsersContext.addUser(username);
        final RegisteredUser registeredUserFound = registeredUsersContext.findByUsername(username);
        assertThat(registeredUserFound, is(sameInstance(registeredUserAdded)));
    }
}
