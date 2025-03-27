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
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> accountService.createAccount(0, null)
        );
        assertNotEquals(exception.getMessage(), "Код валюты не может быть пустым.");
    }

    @Test
    void createAccount() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> accountService.createAccount(0, null)
        );
        assertEquals(exception.getMessage(), "Пользователь не найден.");
/////////////////////////////////////////////////
        int blockedUserId = 3;
        String currency = "USD";

        IllegalStateException exception3 = assertThrowsExactly(
                IllegalStateException.class,
                () -> accountService.createAccount(blockedUserId, currency)
        );
        assertNotEquals(exception3.getMessage(), "Валюта с кодом " + currency + "не найдена в репозитории");    }

    @Test
    void close() {

        int userId = 1;
        String currency = "USD";

        accountService.createAccount(userId, currency);
        double balance = 100.0;
        boolean result = accountService.close(userId, currency);
        assertNotEquals(balance, 0.0);
        assertTrue(result);

////////////////////////////////////////////////
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> accountService.close(0, null)
        );
        assertEquals(exception.getMessage(), "Пользователь не найден.");
////////////////////////////////////////////////
        IllegalStateException exception2 = assertThrowsExactly(
                IllegalStateException.class,
                () -> accountService.close(userId, currency)
        );
        assertNotEquals(exception2.getMessage(), "Заблокированным пользователям запрещено закрывать счета." );
////////////////////////////////////////////////
        if (balance < 0) {
            IllegalStateException exception3 = assertThrowsExactly(
                    IllegalStateException.class,
                    () -> accountService.close(userId, currency)
            );
            assertNotEquals(exception3.getMessage(), "Невозможно закрыть счет: баланс больше 0.");

        }

    }
}