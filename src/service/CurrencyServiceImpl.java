package service;

import model.Currency;
import model.Role;
import model.User;

import java.util.HashMap;
import java.util.Map;

public class CurrencyServiceImpl implements CurrencyService {

    // Хранение валют по коду
    private final Map<String, Currency> currencies = new HashMap<>();
    // Хранение открытых счетов пользователей по валюте
    private final Map<String, Integer> openAccountsByCurrency = new HashMap<>();

    @Override
    public void addCurrency(String code, double exchangeRate) {
        if (currencies.containsKey(code)) {
            throw new IllegalArgumentException("Валюта уже существует: " + code);
        }
        currencies.put(code, new Currency(code, exchangeRate));

    }

    @Override
    public void updateCurrency(String code, double exchangeRate) {
        Currency currency = currencies.get(code);
        if (currency == null) {
            throw new IllegalArgumentException("Валюта не существует: " + code);
        }
        currency.setExchangeRate(exchangeRate);

    }

    // При удалении должна быть проверка, есть ли открытые счета
    // у пользователей в этой валюте? Если есть - что делать?
    @Override
    public void removeCurrency(String code) {
        if (currencies.containsKey(code)) {
            // Проверяем, есть ли открытые счета
            Integer openAccounts = openAccountsByCurrency.get(code);
            if (openAccounts != null && openAccounts > 0) {
                // Логика при наличии открытых счетов
                throw new IllegalStateException("Не удается удалить валюту " + code + " Потому что есть открытые счета.");
            }
            currencies.remove(code);
            openAccountsByCurrency.remove(code);
        } else {
            throw new IllegalArgumentException("Валюты не существует: " + code);
        }

    }

    @Override
    public Map<String, Currency> getAllCurrencies() {
        return new HashMap<>(currencies);
    }

    // Метод для добавления открытого счета
    public void addOpenAccount(String currencyCode) {
        openAccountsByCurrency.put(currencyCode, openAccountsByCurrency.getOrDefault(currencyCode, 0) + 1);
    }

    // Метод для закрытия счета
    public void closeAccount(String currencyCode) {
        Integer openAccounts = openAccountsByCurrency.get(currencyCode);
        if (openAccounts != null && openAccounts > 0) {
            openAccountsByCurrency.put(currencyCode, openAccounts - 1);
        }
        /*
        Описание кода:
Добавление валюты (addCurrency):

Проверка на существование валюты. Если валюта уже есть,
 выбрасывается исключение.
Если не существует, создается новая валюта и добавляется в хранилище.
Обновление валюты (updateCurrency):

Проверяется, существует ли валюта. Если не существует,
выбрасывается исключение.
Если существует, обновляется курс валюты.
Удаление валюты (removeCurrency):

Проверяется наличие открытых счетов. Если есть открытые счета в
этой валюте, выбрасывается исключение, иначе валюта удаляется
из хранилища.
Получение всех валют (getAllCurrencies):

Возвращается новая карта с валютами.
Методы для управления открытыми счетами:

addOpenAccount - увеличивает количество открытых счетов для
указанной валюты.
closeAccount - уменьшает количество открытых счетов для указанной
валюты, если таковые имеются.
Эта реализация обеспечивает основную функциональность для
управления валютами и соблюдает условия по открытым счетам.
         */
    }
}
