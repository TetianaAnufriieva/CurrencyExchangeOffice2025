package service;

import model.Currency;
import model.User;

import java.util.Map;

public interface CurrencyService {
    // Добавление новой валюты
    void addCurrency(User currentUser, String code, double exchangeRate);

    void updateCurrency(User currentUser, String code, double exchangeRate);
    // Удаление валюты
    void removeCurrency(User currentUser, String code);
    // Получить список всех валют
    Map<String, Currency> getAllCurrencies(User currentUser);

}
