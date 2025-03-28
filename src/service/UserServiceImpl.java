package service;

import model.Role;
import model.User;
import repository.UserRepository;

import java.util.HashMap;
import java.util.List;

public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private User activeUser;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Email and password can not be empty");
        }

        if (isEmailExist(email)) {
            throw new RuntimeException("Email already exists");
        }

        User registeredUser = userRepository.createUser(email, password);
        System.out.println("Пользователь с email " + email + " и password " + password + " успешно зарегистрирован.");
        return registeredUser;

    }

    @Override
    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Email and password cannot be empty");
        }

        if (!isEmailExist(email)) {
            throw new RuntimeException("User not found");
        }

        if (isUserBlocked(email)) {
            throw new RuntimeException("User is blocked");
        }

        if (user != null && user.getPassword().equals(password)) {
            activeUser = user;
        }

        return activeUser;
    }

    @Override
    public User getUserById (int userId){
        User user = userRepository.findById(userId);
        if (user != null) {
            return user;
        }
        throw new RuntimeException("ПОЛЬЗОВАТЕЛЬ НЕ НАЙДЕН ПО ID!!!");
        }

        @Override
        public boolean isEmailExist (String email){

            User user = userRepository.findByEmail(email);
            if (user == null) {
                return false;
            }

        return true;
    }

    @Override
    public boolean isUserBlocked(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getRole() == Role.BLOCKED) {
            return true;
        }
        return false;
    }
}