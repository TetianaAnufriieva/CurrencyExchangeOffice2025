package service;

import model.Currency;

public interface ExchangeService {

    // Обмен валют
    void exchange(int userId, Currency fromCurrency, Currency toCurrency, double amount);
}
