package service;

public interface AccountService {

    double checkBalance(int userId, String currency);

    void createAccount(int userId, String currency);

    boolean close(int userId, String currency);
}
