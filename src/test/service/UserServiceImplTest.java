package test.service;

import model.*;
import org.junit.jupiter.api.Test;
import repository.*;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

    private static UserRepository userRepository = new UserRepositoryImpl();
    private static UserService userService = new UserServiceImpl(userRepository);

    @Test
    void registerUser() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> userService.registerUser(null, null)
        );

        assertEquals(exception.getMessage(), "Email and password can not be empty");
    }

    @Test
    void loginUser() {
        String email = "test@example.com";
        String password = "password";

        User loginUser = userService.loginUser(email, password);

        assertNotNull(loginUser);

    }

    @Test
    void getUserById() {
        RuntimeException exception = assertThrowsExactly(
                RuntimeException.class,
                () -> userService.getUserById(0)
        );

        assertFalse(Boolean.parseBoolean(exception.getMessage()), "User not found");
    }

    @Test
    void isEmailExistTrue() {
        String email = "test@example.com";
        userRepository.createUser(email, "password");

        boolean emailExist = userService.isEmailExist(email);

        assertTrue(emailExist);
    }

    @Test
    void isEmailExistFalse() {
        String email = "nonexistent@example.com";

        boolean emailExist = userService.isEmailExist(email);

        assertFalse(emailExist);
    }

    @Test
    void isUserBlocked() {

        String email = "test@example.com";
        boolean isBlocked = userService.isUserBlocked(email);
        assertFalse(isBlocked);
    }



}
