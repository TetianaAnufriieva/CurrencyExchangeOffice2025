package repository;

import model.Account;
import model.Currency;
import model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AccountRepositoryImpl implements AccountRepository {

    private AtomicInteger idGenerator = new AtomicInteger(1);

//    private static final String FILE_NAME = "accounts.dat";
    private final Map<Integer, List<Account>> userAccounts = new HashMap<>();    //
    private UserRepository userRepository;
    private CurrencyRepository currencyRepository;

    public AccountRepositoryImpl(UserRepository userRepository, CurrencyRepository currencyRepository) {
        this.userRepository = userRepository;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public Account createAccount(int userId, String currencyCode, double balance) {
        // TODO: код, отвечающий за проверку, должен быть в сервисе
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User с ID " + userId + " не существует.");
        }

        // Получаем список аккаунтов пользователя, если он есть
        List<Account> accounts = userAccounts.computeIfAbsent(userId, k -> new ArrayList<>());

        // Если передана валюта и её нет в репозитории, добавляем
        if (currencyCode == null) {
            throw new IllegalArgumentException("Код валюты не может быть null");
        }

        Optional<Currency> currency = currencyRepository.findByCode(currencyCode);

        if (currency.isEmpty()) {
            throw new IllegalArgumentException("Валюта с кодом " + currencyCode + " не найдена в репозитории");
        }

        // Создаём аккаунт
        Account account = new Account(idGenerator.getAndIncrement(), userId, currency.get(), balance, new ArrayList<>());
        accounts.add(account);

        return account;
    }

    @Override
    public Account findById(int accountId) {
        for (List<Account> accounts : userAccounts.values()) {
            for (Account account : accounts) {
                if (account.getAccountId() == accountId) {
                return account;
                }
            }
        }   return null;
    }

    @Override
    public List<Account> findByUser(int userId) {
        return userAccounts.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public boolean delete(int accountId) {
        Account accountToRemove = findById(accountId);

        if (accountToRemove == null) {
            return false;
        }

        // Удаляем аккаунт
        List<Account> userAccounts = this.userAccounts.get(accountToRemove.getUserId());
        userAccounts.removeIf(account -> account.getAccountId() == accountToRemove.getAccountId());

        return true;
    }

    @Override
    public boolean close(int accountId) {
        Account account = findById(accountId);
        if (account != null && account.getBalance() == 0) {
            return delete(accountId);
        }
        return false;
    }

    @Override
    public boolean exists(int accountId) {
        return findById(accountId) != null;
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(userAccounts.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList()));
    }

}
