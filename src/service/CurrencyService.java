package service;

import model.Currency;

import java.util.Map;

public interface CurrencyService {
    // Добавление новой валюты
    void addCurrency(String code, double exchangeRate);

    void updateCurrency(String code, double exchangeRate);
    // Удаление валюты
    void removeCurrency(String code);
    // Получить список всех валют
    Map<String, Currency> getAllCurrencies();
    //
}
