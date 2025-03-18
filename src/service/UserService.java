package service;

import model.User;

public interface UserService {
    // Регистрация пользователя
    User registerUser(String email, String password);
    // Вход пользователя
    User loginUser(String email, String password);
    // Получение пользователя по ID
    User getUserById(int UserId);
    // существует ли такой email
    boolean isEmailExist(String email);
    // пользователь заблокирован
    boolean isUserBlocked(String email);

}
