package service;

public interface ExchangeService {

    // Обмен валют
    void exchange(int userId, String fromCurrency, String toCurrency, double amount);
}
