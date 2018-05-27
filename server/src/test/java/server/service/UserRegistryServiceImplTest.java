package server.service;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import server.model.RegisteredUser;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

@RunWith(Theories.class)
public class UserRegistryServiceImplTest {

    @DataPoints
    public static final String[] STRINGS = {"foo", "bar"};

    @Rule
    public final JUnitRuleMockery mockery = new JUnitRuleMockery();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final RandomBalanceGenerator randomBalanceGenerator = mockery.mock(RandomBalanceGenerator.class);
    private final UserRegistryServiceImpl registeredUsersContext = new UserRegistryServiceImpl(randomBalanceGenerator);

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

        mockery.checking(new Expectations() {{
            oneOf(randomBalanceGenerator).generateRandomBalance(1);
            will(returnValue(111));
        }});

        registeredUsersContext.addUser(username1);

        mockery.checking(new Expectations() {{
            oneOf(randomBalanceGenerator).generateRandomBalance(2);
            will(returnValue(222));
        }});

        registeredUsersContext.addUser(username2);

        final List<RegisteredUser> registeredUsers = registeredUsersContext.getRegisteredUsers();
        assertThat(registeredUsers.size(), is(2));

        final RegisteredUser registeredUser1 = registeredUsers.get(0);
        assertThat(registeredUser1.getUsername(), is(username1));
        assertThat(registeredUser1.getBalance(), is(notNullValue()));
        assertThat(registeredUser1.getBalance().compareTo(new BigDecimal(111)), is(0));

        final RegisteredUser registeredUser2 = registeredUsers.get(1);
        assertThat(registeredUser2.getUsername(), is(username2));
        assertThat(registeredUser2.getBalance(), is(notNullValue()));
        assertThat(registeredUser2.getBalance().compareTo(new BigDecimal(222)), is(0));
    }

    @Theory
    public void testAddUser_duplicate(final String username) {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("User with username [" + username + "] already exists");

        mockery.checking(new Expectations() {{
            ignoring(randomBalanceGenerator);
        }});

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
        mockery.checking(new Expectations() {{
            ignoring(randomBalanceGenerator);
        }});

        final RegisteredUser registeredUserAdded = registeredUsersContext.addUser(username);
        final RegisteredUser registeredUserFound = registeredUsersContext.findByUsername(username);
        assertThat(registeredUserFound, is(sameInstance(registeredUserAdded)));
    }
}
