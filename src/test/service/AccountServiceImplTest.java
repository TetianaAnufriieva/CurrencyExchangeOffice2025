package test.service;

import org.junit.jupiter.api.Test;
import repository.*;
import service.*;

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
    }

    @Test
    void createAccount() {
        IllegalArgumentException exception = assertThrowsExactly(IllegalArgumentException.class, () -> accountService.createAccount(0, null));
        assertEquals(exception.getMessage(), "Пользователь не найден.");

        int userId = 1;
        String currency = "USD";
        accountService.createAccount(userId, currency);
        assertNotNull(accountRepository.findByUser(userId));
    }

    @Test
    void close() {
        int userId = 1;
        String currency = "USD";
        accountService.createAccount(userId, currency);
        boolean result = accountService.close(userId, currency);
        assertTrue(result);
    }
}