package service;

import model.Account;
import model.Transaction;
import model.TypeTransaction;
import repository.AccountRepository;
import repository.TransactionRepository;

import java.util.List;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

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


    }

    // Закрытие счета (с проверками: если на счету есть средства? что делать?)
    @Override
    public boolean close(int userId, String currency) {
        return false;
    }
}
