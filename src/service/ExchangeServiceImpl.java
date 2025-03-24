package service;

import model.Account;
import model.Currency;
import model.TypeTransaction;
import repository.AccountRepository;
import repository.TransactionRepository;

import java.time.LocalDateTime;

public class ExchangeServiceImpl implements ExchangeService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public ExchangeServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void exchange(int userId, Currency fromCurrency, Currency toCurrency, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма для обмена должна быть больше 0.");
        }

        double exchangeRate = toCurrency.getExchangeRate() / fromCurrency.getExchangeRate();
        double exchangedAmount = amount * exchangeRate;

        String fromCurrencyCode = fromCurrency.getCode();
        String toCurrencyCode = toCurrency.getCode();

        Account fromAccount = accountRepository.findByUser(userId).stream()
                .filter(account -> account.getCurrency().getCode().equals(fromCurrencyCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("У пользователя отсутствует счет в валюте " + fromCurrencyCode + "."));

        Account toAccount = accountRepository.findByUser(userId).stream()
                .filter(account -> account.getCurrency().getCode().equals(toCurrencyCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("У пользователя отсутствует счет в валюте " + toCurrencyCode + "."));

        if (fromAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Недостаточно средств для обмена.");
        }

        // Обновляем балансы
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + exchangedAmount);

        // Создаем транзакции
        transactionRepository.createTransaction(TypeTransaction.EXCHANGE, fromAccount.getAccountId(), fromCurrencyCode, -amount, LocalDateTime.now());
        transactionRepository.createTransaction(TypeTransaction.EXCHANGE, toAccount.getAccountId(), toCurrencyCode, exchangedAmount, LocalDateTime.now());

        System.out.println("Обмен успешно завершен! " + amount + " " + fromCurrencyCode + " -> " + exchangedAmount + " " + toCurrencyCode);

    }

}
