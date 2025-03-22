package repository;

import model.Account;
import java.util.List;
import java.util.ArrayList;
import  java.util.Currency;
import java.util.Objects;


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

}
