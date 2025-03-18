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

    }

    @Override
    public Optional<Currency> findByCode(String code) {
        return Optional.empty();
    }

    @Override
    public void delete(String code) {

    }

    @Override
    public Map<String, Currency> findAll() {
        return Map.of();
    }
}
