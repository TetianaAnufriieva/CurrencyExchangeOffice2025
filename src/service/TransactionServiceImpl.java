package service;

import model.*;
import repository.AccountRepository;
import repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionServiceImpl implements TransactionService {

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private UserService userService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    @Override
    public Transaction createTransaction(TypeTransaction type, int accountId, String currency, double amount, LocalDateTime date) {
        if (type == null) {
            throw new IllegalArgumentException("Тип транзакции не может быть null.");
        }
        if (currency == null || currency.isEmpty()) {
            throw new IllegalArgumentException("Код валюты не может быть пустым.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма транзакции должна быть больше 0.");
        }
        if (accountRepository.findById(accountId) == null) {
            throw new IllegalArgumentException("Счет с ID " + accountId + " не найден.");
        }
        if (date == null) {
            date = LocalDateTime.now(); // Устанавливаем текущее время, если оно не передано
        }

        return transactionRepository.createTransaction(type, accountId, currency, amount, date);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Просмотр истории операций по всем счетам
    @Override
    public List<Transaction> getUserTransactions(int userId) {
        User user = userService.getUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null.");
        }

        if (user.getRole() == Role.BLOCKED) {
            throw new IllegalStateException("Доступ запрещен. Ваш аккаунт заблокирован.");
        }

        return transactionRepository.findByUserId(userId);
    }

    @Override
    public List<Transaction> getAllTransactionsByCurrency(String currency) {
        String upperCurrencyCode = currency.trim().toUpperCase();

        return transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getCurrency().equalsIgnoreCase(upperCurrencyCode))
                .collect(Collectors.toList());
    }

    // Просмотр истории операций по конкретной валюте
    @Override
    public List<Transaction> getUserTransactionsByCurrency(int userId, String currency) {
        User user = userService.getUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null.");
        }

        // Проверка на заблокированного пользователя
        if (user.getRole() == Role.BLOCKED) {
            throw new IllegalStateException("Доступ запрещен. Ваш аккаунт заблокирован.");
        }

        // Приводим валюту к верхнему регистру, чтобы избежать ошибок с регистром
        String upperCurrencyCode = currency.trim().toUpperCase();

        // Получаем список аккаунтов пользователя
        List<Account> userAccounts = accountRepository.findByUser(user.getUserId());

        // Если у пользователя нет аккаунтов, возвращаем пустой список
        if (userAccounts.isEmpty()) {
            return List.of();
        }

        return transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getCurrency().equalsIgnoreCase(upperCurrencyCode) &&
                        userAccounts.stream().anyMatch(account -> account.getAccountId() == transaction.getAccountId()))
                .collect(Collectors.toList());
    }

    // Пополнение счета в выбранной валюте (проверка существования такого счета
    // у пользователя. Если отсутствует - открыть соответствующий счет)
    @Override
    public void deposit(int userId, String currencyCode, double amount) {
        User user = userService.getUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null.");
        }

        // Проверка на заблокированного пользователя
        if (user.getRole() == Role.BLOCKED) {
            throw new IllegalStateException("Доступ запрещен. Ваш аккаунт заблокирован.");
        }

        // Проверка на валидность валюты
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Валюта не может быть null или пустой.");
        }

        // Проверка на положительную сумму депозита
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма депозита должна быть положительной.");
        }

        // Приводим валюту к верхнему регистру
        String upperCurrencyCode = currencyCode.trim().toUpperCase();

        // Проверка на наличие счета в указанной валюте у пользователя
        List<Account> userAccounts = accountRepository.findByUser(user.getUserId());
        Account account = userAccounts.stream()
                .filter(acc -> acc.getCurrency().getCode().equalsIgnoreCase(upperCurrencyCode))
                .findFirst()
                .orElse(null);

        if (account == null) {
            // Если счета нет, то создаем новый
            account = accountRepository.createAccount(user.getUserId(), currencyCode, 0.0);
            System.out.println("Счёт не найден. Открыт новый счёт в валюте: " + currencyCode);
        }

        // Пополнение счета
        account.setBalance(account.getBalance() + amount);

        transactionRepository.createTransaction(TypeTransaction.DEPOSIT, account.getAccountId(),
                currencyCode, amount, LocalDateTime.now());

        System.out.println("Счет пополнен на сумму: " + amount + " " + currencyCode);
    }

    // Снятие средств со счета (с соответствующими проверками возможности операции)
    @Override
    public boolean withdraw(int userId, String currency, double amount) {
        User user = userService.getUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("Пользователь не может быть null.");
        }

        // Проверка на заблокированного пользователя
        if (user.getRole() == Role.BLOCKED) {
            throw new IllegalStateException("Доступ запрещен. Ваш аккаунт заблокирован.");
        }

        // Проверка на валидность валюты
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Валюта не может быть null или пустой.");
        }

        // Проверка на положительную сумму снятия
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма снятия должна быть положительной.");
        }

        // Приводим валюту к верхнему регистру
        String upperCurrencyCode = currency.trim().toUpperCase();

        // Получаем все аккаунты пользователя
        List<Account> userAccounts = accountRepository.findByUser(user.getUserId());

        // Ищем счёт в нужной валюте
        Account account = userAccounts.stream()
                .filter(acc -> acc.getCurrency().getCode().equalsIgnoreCase(upperCurrencyCode))
                .findFirst()
                .orElse(null);

        // Если счёт не найден, возвращаем false
        if (account == null) {
            System.out.println("Счёт в валюте " + currency + " не найден.");
            return false;
        }

        // Проверка на достаточность средств
        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Недостаточно средств на счёте для выполнения операции.");
        }

        // Снимаем средства с счёта
        account.setBalance(account.getBalance() - amount);

        transactionRepository.createTransaction(TypeTransaction.WITHDRAW, account.getAccountId(),
                currency, amount, LocalDateTime.now());

        System.out.println("Средства успешно сняты: " + amount + " " + currency);

        return true; // Операция выполнена успешно
    }

}
