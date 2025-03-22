package service;

public interface AccountService {

    double checkBalance(int userId, String currency);

    // Открытие нового счета
    void createAccount(int userId, String currency);

    boolean close(int userId, String currency);
}
