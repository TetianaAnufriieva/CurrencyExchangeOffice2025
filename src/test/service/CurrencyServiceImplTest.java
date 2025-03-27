package test.service;

import model.Account;
import model.Currency;
import model.Role;
import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import repository.*;
import service.CurrencyService;
import service.CurrencyServiceImpl;
import service.UserService;
import service.UserServiceImpl;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyServiceImplTest {

    private static CurrencyRepository currencyRepository = new CurrencyRepositoryImpl();
    private static UserRepository userRepository = new UserRepositoryImpl();
    private static UserService userService = new UserServiceImpl(userRepository);
    private static AccountRepository accountRepository = new AccountRepositoryImpl(userRepository, currencyRepository);
    private static CurrencyService currencyService = new CurrencyServiceImpl(currencyRepository, accountRepository);
    private static int userId = 0;
    private static int accountId = 0;
    private static User blockedUser;
    private static User adminUser;

    @BeforeAll
    static void setup() {
        User user = userRepository.createUser("test@email.com", "P@ssword1!");
        userId = user.getUserId();
        Account account = accountRepository.createAccount(user.getUserId(), "EUR", 0);
        accountId = account.getAccountId();
        blockedUser = new User(3, "blocked@example.com", "Qwertz3@",
                Role.BLOCKED, Collections.emptyMap());
        adminUser = new User(1, "admin@example.com", "Qwertzy3@",
                Role.ADMIN, Collections.emptyMap());

    }

    @Test
    void createCurrencyWithBlockedUser() {
        SecurityException exception = assertThrowsExactly(
                SecurityException.class,
                () -> currencyService.addCurrency(blockedUser, "EUR", 0.5)
        );
        assertEquals(exception.getMessage(), "Только администратор может добавлять валюты.");
    }

    @Test
    void ExistingCurrency() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> currencyService.addCurrency(adminUser, "EUR", 0.5)
        );
        assertEquals(exception.getMessage(), "Валюта с кодом EUR уже существует.");
    }

    @Test
    public void addCurrency() {
        currencyService.addCurrency(adminUser, "UAH", 0.5);
        Optional<Currency> currency = currencyRepository.findByCode("UAH");
        assertTrue(currency.isPresent());
        assertTrue(currency.get().getExchangeRate() == 0.5);
    }


    @Test
    void updateCurrency_AdminCanUpdateCurrency() {
        currencyService.updateCurrency(adminUser, "USD", 1.2);
        assertEquals(1.2, currencyRepository.findByCode("USD").get().getExchangeRate());
    }

    @Test
    void updateCurrency_NonExistingCurrencyThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                currencyService.updateCurrency(adminUser, "INR", 75.0));
    }

    @Test
    void updateCurrency_NonAdminThrowsException() {
        assertThrows(SecurityException.class, () ->
                currencyService.updateCurrency(regularUser, "USD", 1.2));
    }


    @Test
    void removeCurrency_AdminCanRemoveUnusedCurrency() {
        currencyService.removeCurrency(adminUser, "PLN");
        assertFalse(currencyRepository.findByCode("PLN").isPresent());
    }

    @Test
    void removeCurrency_NonAdminThrowsException() {
        assertThrows(SecurityException.class, () ->
                currencyService.removeCurrency(regularUser, "PLN"));
    }

    @Test
    void removeCurrency_CurrencyInUseDoesNotDelete() {
        accountRepository.createAccount(1, "PLN", 100.0);
        currencyService.removeCurrency(adminUser, "PLN");
        assertTrue(currencyRepository.findByCode("PLN").isPresent());
    }


    @Test
    void getAllCurrencies_ReturnsAllCurrencies() {
        Map<String, Currency> currencies = currencyService.getAllCurrencies();
        assertEquals(3, currencies.size());
        assertTrue(currencies.containsKey("USD"));
        assertTrue(currencies.containsKey("EUR"));
        assertTrue(currencies.containsKey("PLN"));
    }
}

