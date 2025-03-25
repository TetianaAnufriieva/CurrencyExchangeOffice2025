package service;

import model.Account;
import model.Currency;
import model.TypeTransaction;
import repository.AccountRepository;
import repository.CurrencyRepository;
import repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public class ExchangeServiceImpl implements ExchangeService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CurrencyRepository currencyRepository;

    public ExchangeServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository, CurrencyRepository currencyRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public void exchange(int userId, String fromCurrencyCode, String toCurrencyCode, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма для обмена должна быть больше 0.");
        }

        Optional<Currency> fromCurrency = currencyRepository.findByCode(fromCurrencyCode);
        if (fromCurrency.isEmpty()) {
            throw new IllegalArgumentException(
                    "Валюта с кодом " + fromCurrencyCode + " не найдена в репозитории");
        }

        Optional<Currency> toCurrency = currencyRepository.findByCode(toCurrencyCode);
        if (toCurrency.isEmpty()) {
            throw new IllegalArgumentException(
                    "Валюта с кодом " + toCurrencyCode + " не найдена в репозитории");
        }

        double exchangeRate = fromCurrency.get().getExchangeRate() / toCurrency.get().getExchangeRate();
        double exchangedAmount = amount * exchangeRate;

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
