package repository;

import model.Currency;

import java.util.Map;
import java.util.Optional;

public interface CurrencyRepository {

    void create(String code, double exchangeRate);

    // Добавить валюту
    void create(Currency currency);
    // Получить валюту по коду
    Optional<Currency> findByCode(String code);
    // Удалить валюту
    void delete(String code);
    // Получить все валюты
    Map<String, Currency> findAll();

  // Сохранить валюту
    void save(Currency currency);

}
