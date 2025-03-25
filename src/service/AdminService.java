package service;

public interface AdminService {
    // Повысить пользователя до администратора.
    void promoteToAdmin(String email);
    // Блокировать пользователя
    void blockUser(String email);

}
