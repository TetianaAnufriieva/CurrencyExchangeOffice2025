package test.service;

import model.Role;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.UserRepository;
import repository.UserRepositoryImpl;
import service.AdminService;
import service.AdminServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

class AdminServiceImplTest {

    private UserRepository userRepository;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl();
        adminService = new AdminServiceImpl(userRepository);
    }

    @Test
    void testPromoteToAdmin_Success() {
        User user = userRepository.createUser("test@example.com", "Qwerty1!");
        assertEquals(Role.USER, user.getRole());

        adminService.promoteToAdmin("test@example.com");

        User updatedUser = userRepository.findByEmail("test@example.com");
        assertNotNull(updatedUser);
        assertEquals(Role.ADMIN, updatedUser.getRole());
    }

    @Test
    void testPromoteToAdmin_UserNotFound() {
        Exception exception = assertThrows(RuntimeException.class, () ->
                adminService.promoteToAdmin("nonexistent@example.com"));
        assertEquals("Пользователь с таким email не найден.", exception.getMessage());
    }

    @Test
    void testPromoteToAdmin_AlreadyAdmin() {
        Exception exception = assertThrows(RuntimeException.class, () ->
                adminService.promoteToAdmin("admin2@example.com"));
        assertEquals("Пользователь уже является администратором.", exception.getMessage());
    }

    @Test
    void testBlockUser_Success() {
        User user = userRepository.createUser("blockme@example.com", "Qwerty1!");
        assertEquals(Role.USER, user.getRole());

        adminService.blockUser("blockme@example.com");

        User updatedUser = userRepository.findByEmail("blockme@example.com");
        assertNotNull(updatedUser);
        assertEquals(Role.BLOCKED, updatedUser.getRole());
    }

    @Test
    void testBlockUser_UserNotFound() {
        Exception exception = assertThrows(RuntimeException.class, () ->
                adminService.blockUser("nonexistent@example.com"));
        assertEquals("Пользователь с таким email не найден.", exception.getMessage());
    }

    @Test
    void testBlockUser_AlreadyBlocked() {
        Exception exception = assertThrows(RuntimeException.class, () ->
                adminService.blockUser("banned3@example.com"));
        assertEquals("Пользователь уже является заблокированным.", exception.getMessage());
    }
}