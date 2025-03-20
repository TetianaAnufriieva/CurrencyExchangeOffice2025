package service;

public interface AccountService {

    double checkBalance(int userId, String currency);

    void createAccount(int userId, String currency);

    // Открытие нового счета
    void createAccount(int userId, String currency, double balance);

    boolean close(int userId, String currency);
}
