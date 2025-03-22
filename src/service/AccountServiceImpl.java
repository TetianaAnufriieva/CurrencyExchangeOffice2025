package service;

import model.Account;
import model.Role;
import model.User;
import repository.AccountRepository;
import repository.CurrencyRepository;
import repository.TransactionRepository;
import repository.UserRepository;
import model.Currency;
import java.util.ArrayList;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;



public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private CurrencyRepository currencyRepository;
    private final Map<Integer, List<Account>> userAccounts = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);



    public AccountServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository, UserRepository userRepository, CurrencyRepository currencyRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.currencyRepository = currencyRepository;

    }

    // Просмотр баланса (остатка на всех счетах или каком-то конкретном счете)
    @Override
    public double checkBalance(int userId, String currency) {
        return 0;
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