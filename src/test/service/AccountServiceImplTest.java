package test.service;

import model.Role;
import model.User;
import org.junit.jupiter.api.Test;
import repository.*;
import service.*;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceImplTest {

    AccountRepository accountRepository = new AccountRepositoryImpl(new UserRepositoryImpl(), new CurrencyRepositoryImpl());
    AccountService accountService = new AccountServiceImpl(accountRepository, new UserRepositoryImpl(), new CurrencyRepositoryImpl());

    @Test
    void checkBalance() {
        int userId = 1;
        String currency = "USD";
        double expectedBalance = 100.0;
        double balance = accountService.checkBalance(userId, currency);
        assertNotEquals(expectedBalance, balance);
        if (expectedBalance == balance) throw new IllegalArgumentException("Код валюты не может быть пустым.");
    }

    @Test
    void createAccount() {
        IllegalArgumentException exception = assertThrowsExactly(IllegalArgumentException.class, () -> accountService.createAccount(0, null));
        assertEquals(exception.getMessage(), "Пользователь не найден.");
        User userBlocked = new User(2, "banned3@example.com", "Qwerty3!", Role.BLOCKED, new HashMap<>());

        int userId = 1;
        String currency = "USD";
        accountService.createAccount(userId, currency);
        assertNotNull(accountRepository.findByUser(userId));

        if (accountRepository.findByUser(userId) == null) throw new IllegalArgumentException("Пользователь не найден.");
        if (userBlocked.getRole() == Role.BLOCKED && currency == null) throw new IllegalArgumentException("Валюта с кодом " + currency + " не найдена в репозитории");
    }

    @Test
    void close() {
        User userBlocked = new User(2, "banned3@example.com", "Qwerty3!", Role.BLOCKED, new HashMap<>());
        int userId = 1;
        String currency = "USD";

        accountService.createAccount(userId, currency);
        double balance = accountService.checkBalance(userId, currency);
        boolean result = accountService.close(userId, currency);
        assertTrue(result);

        if (accountRepository.findByUser(userId) == null) throw new IllegalArgumentException("Пользователь не найден.");
        if (userBlocked.getRole() == Role.BLOCKED && currency == null) throw new IllegalArgumentException("Валюта с кодом " + currency + " не найдена в репозитории");
        if (balance > 0) throw new IllegalStateException("Невозможно закрыть счет: баланс больше 0.");
    }
}