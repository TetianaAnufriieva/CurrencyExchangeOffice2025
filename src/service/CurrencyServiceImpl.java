package service;

import model.Currency;
import model.Role;
import model.User;

import java.util.HashMap;
import java.util.Map;

public class CurrencyServiceImpl implements CurrencyService {


    @Override
    public void addCurrency(String code, double exchangeRate) {

    }

    @Override
    public void updateCurrency(String code, double exchangeRate) {

    }

    // При удалении должна быть проверка, есть ли открытые счета
    // у пользователей в этой валюте? Если есть - что делать?
    @Override
    public void removeCurrency(String code) {

    }

    @Override
    public Map<String, Currency> getAllCurrencies() {
        return Map.of();
    }

}
