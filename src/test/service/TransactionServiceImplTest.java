package test.service;

import model.Account;
import model.Transaction;
import model.TypeTransaction;
import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import repository.*;
import service.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionServiceImplTest {

    private static CurrencyRepository currencyRepository = new CurrencyRepositoryImpl();
    private static UserRepository userRepository = new UserRepositoryImpl();
    private static UserService userService = new UserServiceImpl(userRepository);
    private static AccountRepository accountRepository = new AccountRepositoryImpl(userRepository, currencyRepository);
    private static TransactionRepository transactionRepository = new TransactionRepositoryImpl(accountRepository);
    private static TransactionService transactionService = new TransactionServiceImpl(transactionRepository, accountRepository, userService);

    private static int accountId = 0;

    @BeforeAll
    static void setupTest() {
//        User user = userRepository.createUser("test@email.com", "P@ssword1!");
//        Account account = accountRepository.createAccount(user.getUserId(), "EUR", 0);
//        accountId = account.getAccountId();
    }

    @Test
    void unableToCreateTransactionWithoutType() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> transactionService.createTransaction(null, 0, null, 0, null)
        );

        assertEquals(exception.getMessage(), "Тип транзакции не может быть null.");
    }

    @Test
    void unableToCreateTransactionWithoutCurrency() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> transactionService.createTransaction(TypeTransaction.DEPOSIT, 0, null, 0, null)
        );

        assertEquals(exception.getMessage(), "Код валюты не может быть пустым.");
    }

    @Test
    void unableToCreateTransactionWithoutAmount() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> transactionService.createTransaction(TypeTransaction.DEPOSIT, 0, "EUR", 0, null)
        );

        assertEquals(exception.getMessage(), "Сумма транзакции должна быть больше 0.");
    }

    @Test
    void unableToCreateTransactionWithoutAccount() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> transactionService.createTransaction(TypeTransaction.DEPOSIT, 0, "EUR", 10, null)
        );

        assertEquals(exception.getMessage(), "Счет с ID 0 не найден.");
    }

    @Test
    @Disabled
    void createTransaction() {
        Transaction transaction = transactionService.createTransaction(TypeTransaction.DEPOSIT, accountId, "EUR", 10, null);

        assertNotNull(transaction);
        assertEquals(TypeTransaction.DEPOSIT, transaction.getType());
        assertEquals(accountId, transaction.getAccountId());
        assertEquals("EUR", transaction.getCurrency());
        assertEquals(10, transaction.getAmount());
    }

    @Test
    @Disabled
    void getAllTransactions() {
        transactionService.createTransaction(TypeTransaction.DEPOSIT, accountId, "EUR", 10, null);
        transactionService.createTransaction(TypeTransaction.WITHDRAW, accountId, "USD", 10, null);
        transactionService.createTransaction(TypeTransaction.EXCHANGE, accountId, "UAH", 10, null);

        List<Transaction> allTransactions = transactionService.getAllTransactions();

        assertTrue(allTransactions.stream().anyMatch(transaction -> transaction.getType() == TypeTransaction.DEPOSIT));
        assertTrue(allTransactions.stream().anyMatch(transaction -> transaction.getType() == TypeTransaction.WITHDRAW));
        assertTrue(allTransactions.stream().anyMatch(transaction -> transaction.getType() == TypeTransaction.EXCHANGE));
    }

}