package service;

public interface AdminService {
    // Повысить пользователя до администратора.
    void promoteToAdmin(String userId);
    // Блокировать пользователя (опционально)
    void blockUser(int userId);

}
