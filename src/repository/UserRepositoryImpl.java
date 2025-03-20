package repository;

import model.User;
import model.Role;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UserRepositoryImpl implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();

    // Объект, отвечающий за генерацию уникальных id
    private final AtomicInteger currentId = new AtomicInteger(1);
    private User activeUser;

    // Поле перемесщено в метод под названием createUser
    // private User user;


    public UserRepositoryImpl() {
        users.put(currentId.get(), new User(currentId.getAndIncrement(), "user1@example.com", "Qwerty1!", Role.USER, new HashMap<>()));
        users.put(currentId.get(), new User(currentId.getAndIncrement(), "admin2@example.com", "Qwerty2!", Role.ADMIN, new HashMap<>()));
        users.put(currentId.get(), new User(currentId.getAndIncrement(), "banned3@example.com", "Qwerty3!", Role.BLOCKED, new HashMap<>()));
    }


    @Override
    public User createUser(String email, String password){
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("EMAIL И PASSWORD НЕ МОЖЕТ БЫТЬ ПУСТЫМ!!!");
        }

        if (existsByEmail(email)) {
            throw new RuntimeException("EMAIL УЖЕ ЕСТЬ!!!");
        }

        // Я переместил параметр выше сюда
        User user = new User(currentId.getAndIncrement(),
                email, password,
                Role.USER, new HashMap<>());

        users.put(user.getUserId(), user);
        return user;
    }

    @Override
    public User findById(int userId) {
        return users.get(userId);
    }

    @Override
    public User findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean delete(int userId) {
        if (activeUser == null || activeUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Вы не администратор!");
        }
        return users.remove(userId) != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }
}
