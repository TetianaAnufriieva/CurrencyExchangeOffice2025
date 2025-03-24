package repository;

import model.Account;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    // Добавление нового счета
    Account createAccount(int userId, String currencyCode, double balance);
    // Получение счета по ID
    Account findById(int accountId);
    // Получение всех счетов пользователя
    List<Account> findByUser(int userId);
    // Удаление счета
    boolean delete(int accountId);
    // Закрытие счета
    boolean close(int accountId);
    // Проверка наличия счета
    boolean exists(int accountId);


    List<Account> findAll();
}
