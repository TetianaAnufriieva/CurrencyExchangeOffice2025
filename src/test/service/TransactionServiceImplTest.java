package test.service;

import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import repository.*;
import service.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceImplTest {

    private static CurrencyRepository currencyRepository = new CurrencyRepositoryImpl();
    private static UserRepository userRepository = new UserRepositoryImpl();
    private static UserService userService = new UserServiceImpl(userRepository);
    private static AccountRepository accountRepository = new AccountRepositoryImpl(userRepository, currencyRepository);
    private static TransactionRepository transactionRepository = new TransactionRepositoryImpl(accountRepository);
    private static TransactionService transactionService = new TransactionServiceImpl(transactionRepository, accountRepository, userService);

    private static int userId = 0;
    private static int accountId = 0;

    @BeforeAll
    static void setupTest() {
        User user = userRepository.createUser("test@email.com", "P@ssword1!");
        userId = user.getUserId();

        Account account = accountRepository.createAccount(user.getUserId(), "EUR", 0);
        accountId = account.getAccountId();
    }

    @Test
    void testCurrencyCreation() {
        CurrencyService currencyService = new CurrencyServiceImpl(currencyRepository, accountRepository);

        User admin = new User(100, null, null, Role.ADMIN, null);
        currencyService.addCurrency(admin, "UAH", 20);

        Optional<Currency> currency = currencyRepository.findByCode("UAH");
        assertTrue(currency.isPresent());
        assertTrue(currency.get().getExchangeRate() == 20);
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
    void createTransaction() {
        Transaction transaction = transactionService.createTransaction(TypeTransaction.DEPOSIT, accountId, "EUR", 10, null);

        assertNotNull(transaction);
        assertEquals(TypeTransaction.DEPOSIT, transaction.getType());
        assertEquals(accountId, transaction.getAccountId());
        assertEquals("EUR", transaction.getCurrency());
        assertEquals(10, transaction.getAmount());
    }

    @Test
    void getAllTransactions() {
        transactionService.createTransaction(TypeTransaction.DEPOSIT, accountId, "EUR", 10, null);
        transactionService.createTransaction(TypeTransaction.WITHDRAW, accountId, "USD", 10, null);
        transactionService.createTransaction(TypeTransaction.EXCHANGE, accountId, "PLN", 10, null);

        List<Transaction> allTransactions = transactionService.getAllTransactions();

        assertTrue(allTransactions.stream().anyMatch(transaction -> transaction.getType() == TypeTransaction.DEPOSIT));
        assertTrue(allTransactions.stream().anyMatch(transaction -> transaction.getType() == TypeTransaction.WITHDRAW));
        assertTrue(allTransactions.stream().anyMatch(transaction -> transaction.getType() == TypeTransaction.EXCHANGE));
    }

    @Test
    void getUserTransactionsWithoutUser(){
        RuntimeException exception = assertThrowsExactly(
                RuntimeException.class,
                () -> transactionService.getUserTransactions(0)
        );

        assertEquals(exception.getMessage(), "ПОЛЬЗОВАТЕЛЬ НЕ НАЙДЕН ПО ID!!!");
    }

    @Test
    void depositWithoutUser(){
        RuntimeException exception = assertThrowsExactly(
                RuntimeException.class,
                () -> transactionService.deposit(0, "EUR", 200)
        );

        assertEquals(exception.getMessage(), "ПОЛЬЗОВАТЕЛЬ НЕ НАЙДЕН ПО ID!!!");
    }

    @Test
    void getUserTransactionsWithBlockedUser(){
        IllegalStateException exception = assertThrowsExactly(
                IllegalStateException.class,
                () -> transactionService.getUserTransactions(3)
        );

        assertEquals(exception.getMessage(), "Доступ запрещен. Ваш аккаунт заблокирован.");
    }

    @Test
    void getUserTransactions(){
        List<Transaction> userTransactions = transactionService.getUserTransactions(userId);
        int initialTransactionsCount = userTransactions.size();

        transactionService.createTransaction(TypeTransaction.DEPOSIT, accountId, "EUR", 10, null);
        transactionService.createTransaction(TypeTransaction.WITHDRAW, accountId, "EUR", 10, null);
        transactionService.createTransaction(TypeTransaction.EXCHANGE, accountId, "EUR", 10, null);

        userTransactions = transactionService.getUserTransactions(userId);
        assertTrue(userTransactions.size() == initialTransactionsCount + 3);
    }

    @Test
    void getAllTransactionsByCurrency() {
        transactionService.createTransaction(TypeTransaction.DEPOSIT, accountId, "EUR", 10, null);
        transactionService.createTransaction(TypeTransaction.WITHDRAW, accountId, "USD", 10, null);
        transactionService.createTransaction(TypeTransaction.EXCHANGE, accountId, "PLN", 10, null);

        List<Transaction> allTransactionsByCurrencies = transactionService.getAllTransactionsByCurrency("USD");

        assertTrue(allTransactionsByCurrencies.stream().anyMatch(transaction -> transaction.getCurrency().equals("USD")));
        assertTrue(allTransactionsByCurrencies.stream().noneMatch(transaction -> transaction.getCurrency().equals("EUR")));
        assertTrue(allTransactionsByCurrencies.stream().noneMatch(transaction -> transaction.getCurrency().equals("PLN")));
    }

    @Test
    void getUserTransactionsByCurrency() {
        transactionService.createTransaction(TypeTransaction.DEPOSIT, accountId, "EUR", 10, null);
        transactionService.createTransaction(TypeTransaction.WITHDRAW, accountId, "EUR", 10, null);
        transactionService.createTransaction(TypeTransaction.EXCHANGE, accountId, "EUR", 10, null);

        List<Transaction> userTransactionsByCurrencies = transactionService.getUserTransactionsByCurrency(userId, "EUR");

        assertTrue(userTransactionsByCurrencies.stream().anyMatch(transaction -> transaction.getCurrency().equals("EUR")));
        assertTrue(userTransactionsByCurrencies.stream().noneMatch(transaction -> transaction.getCurrency().equals("USD")));
        assertTrue(userTransactionsByCurrencies.stream().noneMatch(transaction -> transaction.getCurrency().equals("PLN")));
    }

    @Test
    void getUserTransactionsByCurrencyWithoutUser(){
        RuntimeException exception = assertThrowsExactly(
                RuntimeException.class,
                () -> transactionService.getUserTransactions(0)
        );

        assertEquals(exception.getMessage(), "ПОЛЬЗОВАТЕЛЬ НЕ НАЙДЕН ПО ID!!!");
    }

    @Test
    void testDeposit() {
        accountRepository.findById(accountId).setBalance(0);

        transactionService.deposit(userId, "EUR", 100);

        Account account = accountRepository.findById(accountId);
        assertEquals(100, account.getBalance());
    }

    @Test
    void testWithdraw() {
        accountRepository.findById(accountId).setBalance(0);

        transactionService.deposit(userId, "EUR", 100);

        boolean withdraw = transactionService.withdraw(userId, "EUR", 100);
        assertTrue(withdraw);

        Account account = accountRepository.findById(accountId);
        assertEquals(0, account.getBalance());
    }

    @Test
    void testNoWithdrawWhenNoMoney() {
        transactionService.deposit(userId, "EUR", 90);

        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> transactionService.withdraw(userId, "EUR", 100)
        );

        assertEquals(exception.getMessage(), "Недостаточно средств на счёте для выполнения операции.");
    }

    @Test
    void depositWithBlockedUser(){
        IllegalStateException exception = assertThrowsExactly(
                IllegalStateException.class,
                () -> transactionService.deposit(3, "EUR",600)
        );

        assertEquals(exception.getMessage(), "Доступ запрещен. Ваш аккаунт заблокирован.");
    }

    @Test
    void depositWithoutCurrency() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> transactionService.deposit(userId,null,1000)
        );

        assertEquals(exception.getMessage(), "Валюта не может быть null или пустой.");
    }

    @Test
    void depositWithoutAmount0() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> transactionService.deposit(userId, "EUR",0.0)
        );

        assertEquals(exception.getMessage(), "Сумма депозита должна быть положительной.");
    }

    @Test
    void depositWithoutAmountMinus() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> transactionService.deposit(userId, "EUR",-700.50)
        );

        assertEquals(exception.getMessage(), "Сумма депозита должна быть положительной.");
    }

    @Test
    void withdrawWithoutUser(){
        RuntimeException exception = assertThrowsExactly(
                RuntimeException.class,
                () -> transactionService.withdraw(0, "EUR", 200)
        );

        assertEquals(exception.getMessage(), "ПОЛЬЗОВАТЕЛЬ НЕ НАЙДЕН ПО ID!!!");
    }

    @Test
    void withdrawWithBlockedUser(){
        IllegalStateException exception = assertThrowsExactly(
                IllegalStateException.class,
                () -> transactionService.withdraw(3, "EUR",600)
        );

        assertEquals(exception.getMessage(), "Доступ запрещен. Ваш аккаунт заблокирован.");
    }

    @Test
    void withdrawWithoutCurrencyNull() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> transactionService.withdraw(userId,null,1000)
        );

        assertEquals(exception.getMessage(), "Валюта не может быть null или пустой.");
    }

    @Test
    void withdrawWithoutCurrencyEmpty() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> transactionService.withdraw(userId," ",1000)
        );

        assertEquals(exception.getMessage(), "Валюта не может быть null или пустой.");
    }

    @Test
    void withdrawWithoutAmount0() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> transactionService.withdraw(userId, "EUR",0.0)
        );

        assertEquals(exception.getMessage(), "Сумма снятия должна быть положительной.");
    }

    @Test
    void withdrawWithoutAmountMinus() {
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> transactionService.withdraw(userId, "EUR",-700.50)
        );

        assertEquals(exception.getMessage(), "Сумма снятия должна быть положительной.");
    }

    @Test
    void insufficientFundsForWithdraw() {
        transactionService.deposit(userId, "EUR", 100);
        IllegalArgumentException exception = assertThrowsExactly(
                IllegalArgumentException.class,
                () -> transactionService.withdraw(userId,"EUR",1000)
        );

        assertEquals(exception.getMessage(), "Недостаточно средств на счёте для выполнения операции.");
    }

}