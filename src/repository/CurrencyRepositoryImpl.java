package repository;

import model.Currency;
import model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CurrencyRepositoryImpl implements CurrencyRepository {

    private final Map<String, Currency> currencies = new HashMap<>();
    private Currency currency;



    public CurrencyRepositoryImpl(){
        currencies.put ("EUR", new Currency("EUR", 1));
        currencies.put ("USD", new Currency("USD", 0.92));
        currencies.put ("PLN", new Currency("PLN", 0.23));
    }

    @Override
    public void create (Currency currency) {
        // Добавляем валюту в коллекцию, если её код уникален
        if (!currencies.containsKey(currency.getCode())) {
            currencies.put(currency.getCode(), currency);
        } else {
            throw new IllegalArgumentException("Валюта с кодом " + currency.getCode() + " already exists.");
        }
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        // Находим валюту по коду и возвращаем её в Optional
        return Optional.ofNullable(currencies.get(code));
    }

    @Override
    public void delete(String code) {
        // Удаляем валюту из коллекции по коду
        currencies.remove(code);
    }

    @Override
    public Map<String, Currency> findAll() {
        // Возвращаем неизменяемую карту всех валют
        return Map.copyOf(currencies);
    }
    /*
    Метод create(Currency currency):
Проверяет, существует ли уже валюта с таким же кодом.
Если не существует, добавляет валюту в коллекцию.
 Если уже существует, выбрасывается исключение.

Метод findByCode(String code):
Возвращает Optional, который содержит найденную валюту.
 Если валюта не найдена, возвращает Optional.empty().

Метод delete(String code):
Удаляет валюту из коллекции по указанному коду.

Метод findAll():
Возвращает неизменяемую копию карты всех валют,
 чтобы предотвратить модификацию оригинальной карты извне.
     */
}
