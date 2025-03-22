package service;

import model.Currency;
import model.Role;
import model.User;

import java.util.HashMap;
import java.util.Map;

public class CurrencyServiceImpl implements CurrencyService {
    private final Map<String, Currency> currencies = new HashMap<>();
    private final AccountService accountService; // сервис для работы со счетами пользователей

    public CurrencyServiceImpl(AccountService accountService) {
        this.accountService = accountService;
    }

//    @Override
//    public void addCurrency(String code, double exchangeRate) {
//
//    }
//
//    @Override
//    public void updateCurrency(String code, double exchangeRate) {
//
//    }
//
//    // При удалении должна быть проверка, есть ли открытые счета
//    // у пользователей в этой валюте? Если есть - что делать?
//    @Override
//    public void removeCurrency(String code) {
//
//    }
//
//    @Override
//    public Map<String, Currency> getAllCurrencies() {
//        return Map.of();
//    }

    @Override
    public void addCurrency(String code, double exchangeRate) {
        if (currencies.containsKey(code)) {
            throw new IllegalArgumentException("Валюты с таким кодом уже существует: " + code);
        }
        currencies.put(code, new Currency(code, exchangeRate));

    }

    @Override
    public void updateCurrency(String code, double exchangeRate) {
        Currency currency = currencies.get(code);
        if (currency == null) {
            throw new IllegalArgumentException("Валюты не существует: " + code);
        }
        currency.setExchangeRate(exchangeRate);

    }

    // При удалении должна быть проверка, есть ли открытые счета
    // у пользователей в этой валюте? Если есть - что делать?
    @Override
    public void removeCurrency(String code) {
        // Проверяем, есть ли открытые счета у пользователей в данной валюте
        if (accountService.hasOpenAccountsInCurrency(code)) {
            throw new IllegalStateException("Невозможно удалить валюту: Открытые счета существуют в валюте " + code);
        }
        currencies.remove(code);

    }

    @Override
    public Map<String, Currency> getAllCurrencies() {
        return new HashMap<>(currencies);
    }

//    // Метод для добавления открытого счета
//    public void addOpenAccount(String currencyCode) {
//        openAccountsByCurrency.put(currencyCode, openAccountsByCurrency.getOrDefault(currencyCode, 0) + 1);
//    }
//
//    // Метод для закрытия счета
//    public void closeAccount(String currencyCode) {
//        Integer openAccounts = openAccountsByCurrency.get(currencyCode);
//        if (openAccounts != null && openAccounts > 0) {
//            openAccountsByCurrency.put(currencyCode, openAccounts - 1);
//        }

}
