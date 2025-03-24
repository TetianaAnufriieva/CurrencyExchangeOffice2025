package repository;

import model.Currency;

import java.util.Map;
import java.util.Optional;

public interface CurrencyRepository {


    // Добавить валюту
    void create(String code, double exchangeRate);

    // Получить валюту по коду
    Optional<Currency> findByCode(String code);
    // Удалить валюту
    void delete(String code);
    // Получить все валюты
    Map<String, Currency> findAll();


}
