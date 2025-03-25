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
    private AtomicInteger idGenerator = new AtomicInteger(1);
    private UserRepository userRepository;
    private CurrencyRepository currencyRepository;

    public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository,
                              CurrencyRepository currencyRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.currencyRepository = currencyRepository;
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
                    .filter(account -> account.getCurrency().getCode().equals(currency))
                    .findFirst();
            // Если такой счет найден, возвращаем его баланс
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                return account.getBalance();
            } else {
                return 0; // Если не нашли счет с такой валютой, возвращаем 0
            }
        } else {
            throw new IllegalArgumentException("Код валюты не может быть null");
        }
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


        List<Account> accounts = accountRepository.findByUser(userId);
        if (accounts.stream().anyMatch(acc -> acc.getCurrency().getCode().equals(currency))) {
            throw new IllegalStateException("Пользователь уже имеет счет в этой валюте.");

        }
        Account newAccount = new Account(idGenerator.getAndIncrement(), userId, currencyOpt.get(), 0.0, new ArrayList<>());
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

        List<Account> accounts = accountRepository.findByUser(userId);
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
            throw new IllegalStateException("Невозможно закрыть счет: баланс больше 0");
        }

        accounts.remove(accountToClose);
        return true;
    }

}






