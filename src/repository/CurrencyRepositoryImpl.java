package repository;

import model.Currency;

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
    public void create (String code, double exchangeRate) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Код валюты не может быть пустым");
        }
        if (exchangeRate <= 0) {
            throw new IllegalArgumentException("Курс валюты должен быть положительным");
        }
        currencies.put(code, new Currency(code, exchangeRate));

    }

    @Override
    public void create(Currency currency) {

    }

    @Override
    public Optional<Currency> findByCode(String code) {
        return Optional.ofNullable(currencies.get(code));
    }



    @Override
    public void delete(String code) {
        if (!currencies.containsKey(code)) {
            throw new IllegalArgumentException("Валюта с кодом " + code + " не найдена");
        }
        currencies.remove(code);

    }

    @Override
    public Map<String, Currency> findAll() {
        return new HashMap<>(currencies);
    }

    @Override
    public void save(Currency currency) {

    }


}
