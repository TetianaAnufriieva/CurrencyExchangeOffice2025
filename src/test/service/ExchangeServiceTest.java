package test.service;

import model.Account;
import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import repository.*;
import service.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class ExchangeServiceTest {

    private static CurrencyRepository currencyRepository = new CurrencyRepositoryImpl();
    private static UserRepository userRepository = new UserRepositoryImpl();
    private static UserService userService = new UserServiceImpl(userRepository);
    private static AccountRepository accountRepository = new AccountRepositoryImpl(userRepository, currencyRepository);
    private static TransactionRepository transactionRepository = new TransactionRepositoryImpl(accountRepository);
    private static TransactionService transactionService = new TransactionServiceImpl(transactionRepository, accountRepository, userService);
    private static ExchangeService exchangeService = new ExchangeServiceImpl(accountRepository, transactionRepository, currencyRepository);

    private static int userId = 0;
    private static int eurAccountId = 0;
    private static int usdAccountId = 0;

    @BeforeAll
    static void setupTest() {
        User user = userRepository.createUser("test@email.com", "P@ssword1!");
        userId = user.getUserId();

        Account eurAccount = accountRepository.createAccount(user.getUserId(), "EUR", 0);
        eurAccountId = eurAccount.getAccountId();

        Account usdAccount = accountRepository.createAccount(user.getUserId(), "USD", 0);
        usdAccountId = usdAccount.getAccountId();
    }

    @Test
    void exchange() {
        accountRepository.findById(eurAccountId).setBalance(0);
        accountRepository.findById(usdAccountId).setBalance(0);

        transactionService.deposit(userId, "EUR", 100);
        exchangeService.exchange(userId, "EUR", "USD", 75);

        Account eurAccount = accountRepository.findById(eurAccountId);
        assertEquals(25, eurAccount.getBalance());

        Account usdAccount = accountRepository.findById(usdAccountId);
        assertEquals(81.5, usdAccount.getBalance(), 0.1);
    }

    @Test
    void insufficientFundsForExchange() {
        transactionService.deposit(userId, "EUR", 100);

        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> exchangeService.exchange(userId,"EUR", "USD", 1000)
        );

        assertEquals(exception.getMessage(), "Недостаточно средств для обмена.");

    }

    @Test
    void exchangeWithoutAmount0() {
        transactionService.deposit(userId, "EUR", 100);

        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> exchangeService.exchange(userId, "EUR", "USD",0.0)
        );

        assertEquals(exception.getMessage(), "Сумма для обмена должна быть больше 0.");
    }

    @Test
    void exchangeWithoutAmountMinus() {
        transactionService.deposit(userId, "EUR", 100);

        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> exchangeService.exchange(userId, "EUR", "USD",-520)
        );

        assertEquals(exception.getMessage(), "Сумма для обмена должна быть больше 0.");
    }

    @Test
    void exchangeWithEmptyFromCurrency() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> exchangeService.exchange(userId,null, "USD", 100)
        );

        assertEquals(exception.getMessage(), "Валюта с кодом null не найдена в репозитории");
    }

    @Test
    void exchangeWithEmptyToCurrency() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> exchangeService.exchange(userId,"EUR", null, 100)
        );

        assertEquals(exception.getMessage(), "Валюта с кодом null не найдена в репозитории");
    }
}
