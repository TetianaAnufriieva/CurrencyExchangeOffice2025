package service;

public interface CurrencyService {
    // Добавление новой валюты
    void addCurrency(String code, double exchangeRate);

    void updateCurrency(String code, double exchangeRate);
    // Удаление валюты
    void removeCurrency(String code);
    // Получить список всех валют
    int[] getAllCurrencies();
}
