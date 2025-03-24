package service;

import model.*;
import repository.AccountRepository;
import repository.TransactionRepository;
import repository.UserRepository;
import model.Role;
import model.User;
import repository.CurrencyRepository;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private AtomicInteger idGenerator;
    private UserRepository userRepository;
    private CurrencyRepository currencyRepository;
    private UserService userService;
    private TransactionService transactionService;
    private AdminService adminService;
    private HashMap<Integer, List<Account>> userAccounts = new HashMap<>();





    public AccountServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // Просмотр баланса (остатка на всех счетах или каком-то конкретном счете)
    @Override
    public double checkBalance(int userId, String currency) {
        // Получаем все счета пользователя
        List<Account> userAccounts = accountRepository.findByUser(userId);
        // Если у пользователя нет счетов, возвращаем 0
        if (userAccounts == null || userAccounts.isEmpty()) {
            return 0;
        }
        // Если валюта передана, ищем конкретный счет по валюте
        if (currency != null && !currency.isEmpty()) {
            // Ищем счет с нужной валютой
            Optional<Account> accountOpt = userAccounts.stream()
                    .filter(account -> account.getCurrency().equals(currency))
                    .findFirst();
            // Если такой счет найден, возвращаем его баланс
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                double balance = calculateBalanceForAccount(account);
                return balance;
            } else {
                return 0; // Если не нашли счет с такой валютой, возвращаем 0
            }
        }
        // Если валюта не передана, считаем общий баланс для всех счетов
        double totalBalance = 0;
        for (Account account : userAccounts) {
            totalBalance += calculateBalanceForAccount(account);
        }

        return totalBalance;
    }
    // Вспомогательный метод для подсчета баланса по счету, включая все транзакции
    private double calculateBalanceForAccount(Account account) {
        // Получаем все транзакции для конкретного счета
        List<Transaction> transactions = transactionRepository.findByAccountId(account.getAccountId());
        double balance = account.getBalance(); // Начальный баланс счета
        // Рассчитываем баланс, прибавляя или вычитая суммы по транзакциям
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TypeTransaction.DEPOSIT) {
                balance += transaction.getAmount(); // Пополнение
            } else if (transaction.getType() == TypeTransaction.WITHDRAW) {
                balance -= transaction.getAmount(); // Снятие
            }
        }
        return balance;
    }

    // Открытие нового счета
    @Override
    public void createAccount(int userId, String currency) {

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new
                    IllegalArgumentException("Пользователь не найден.");

        }
        if (user.getRole() == Role.BLOCKED) {
            throw new
                    IllegalStateException("Заблокированым пользователям запрещено создание счета.");

        }

        Optional<Currency> currencyOpt = currencyRepository.findByCode(currency);
        if (currencyOpt.isEmpty()) {
            throw new
                    IllegalArgumentException("Валюта с кодом " + currency + " не найдена в репозитории");

        }


        List<Account> accounts = userAccounts.computeIfAbsent(userId, k -> new ArrayList<>());
        if (accounts.stream().anyMatch(acc -> acc.getCurrency().getCode().equals(currency))) {
            throw new IllegalStateException("Пользователь уже имеет счет в этой валюте.");

        }
        Account newAccount = new Account((int) idGenerator.getAndIncrement(), userId, currencyOpt.get(), 0.0, new ArrayList<>());
        accounts.add(newAccount);

    }

    // Закрытие счета (с проверками: если на счету есть средства? что делать?)
    @Override
    public boolean close(int userId, String currency) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new
                    IllegalArgumentException("Пользователь не найден.");

        }
        if (user.getRole() == Role.BLOCKED) {
            throw new
                    IllegalStateException("Заблокированным пользователям запрещено закрывать счета.");
        }

        List<Account> accounts = userAccounts.get(userId);
        if (accounts == null || accounts.isEmpty()) {
            throw new
                    IllegalStateException("Пользователь не имеет счетов.");
        }

        Account accountToClose = accounts.stream()
                .filter(acc -> acc.getCurrency().getCode().equals(currency))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Счет в данной валюте отсутсвует."));

        double balance = accountToClose.getBalance();
        if (balance > 0) {
            Account fallbackAccount = accounts.stream()
                    .filter(acc -> ! acc.getCurrency().getCode().equals(currency))
                    .findFirst()
                    .orElse(null);
            if (fallbackAccount != null) {
                fallbackAccount.setBalance(fallbackAccount.getBalance() + balance);
                System.out.println("Средства переведены на счет в " + fallbackAccount.getCurrency().getCode());
            } else {
                throw new IllegalStateException("Невозможно закрыть счет: нет другого счета для перевода средств");
            }
        }

        accounts.remove(accountToClose);
        return true;
    }

}






