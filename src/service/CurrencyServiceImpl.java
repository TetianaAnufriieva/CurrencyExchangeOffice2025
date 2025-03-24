package service;

import model.Currency;
import model.Role;
import model.User;
import repository.AccountRepository;
import repository.CurrencyRepository;
import repository.TransactionRepository;

import java.util.Collections;
import java.util.Optional;

public class CurrencyServiceImpl implements CurrencyService {
  private final CurrencyRepository currencyRepository;
  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final User currentUser;
  private static final String BASE_CURRENCY = "EUR";

  public CurrencyServiceImpl(CurrencyRepository currencyRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, User currentUser) {
    this.currencyRepository = currencyRepository;
    this.accountRepository = accountRepository;
    this.transactionRepository = transactionRepository;
    this.currentUser = currentUser;
  }

  @Override
  public void addCurrency(String code, double exchangeRate) {
    if (currentUser.getRole() != Role.ADMIN) {
      throw new SecurityException("Только администратор может добавлять валюты.");
    }
    if (currencyRepository.findByCode(code).isPresent()) {
      throw new IllegalArgumentException("Валюта с кодом " + code + " уже существует.");
    }
    currencyRepository.save(new Currency(code, exchangeRate));
  }

  @Override
  public void updateCurrency(String code, double exchangeRate) {
    if (currentUser.getRole() != Role.ADMIN) {
      throw new SecurityException("Только администратор может обновлять валюты.");
    }
    Optional<Currency> currencyOpt = currencyRepository.findByCode(code);
    if (currencyOpt.isEmpty()) {
      throw new IllegalArgumentException("Валюта с кодом " + code + " не найдена.");
    }
    currencyOpt.get().setExchangeRate(exchangeRate);
  }

  @Override
  public void removeCurrency(String code) {
    if (currentUser.getRole() != Role.ADMIN) {
      throw new SecurityException("Только администратор может удалять валюты.");
    }
    boolean hasAccounts = accountRepository.findAll().stream(getAllCurrencies())
        .anyMatch(account -> account.getCurrency().getCode().equals(code));    if (hasAccounts) {
      System.out.println("Невозможно удалить валюту, так как существуют аккаунты с этой валютой.");
      return;
    }
    currencyRepository.delete(code);
  }



  @Override
  public int[] getAllCurrencies() {
    if (currentUser.getRole() == Role.BLOCKED) {
      throw new SecurityException("У вас нет доступа к просмотру валют.");
    }
    return Collections.unmodifiableMap(currencyRepository.findAll());
  }
}
//    @Override
//    public void addCurrency(String code, double exchangeRate) {
//
//    }
//
//    @Override
//    public void updateCurrency(String code, double exchangeRate) {
//
//    }
//
//    // При удалении должна быть проверка, есть ли открытые счета
//    // у пользователей в этой валюте? Если есть - что делать?
//    @Override
//    public void removeCurrency(String code) {
//
//    }
//
//    @Override
//    public Map<String, Currency> getAllCurrencies() {
//        return Map.of();
//    }

//    @Override
//    public void addCurrency(String code, double exchangeRate) {
//
//    }
//
//    @Override
//    public void updateCurrency(String code, double exchangeRate) {
//
//    }
//
//    // При удалении должна быть проверка, есть ли открытые счета
//    // у пользователей в этой валюте? Если есть - что делать?
//    @Override
//    public void removeCurrency(String code) {
//
//    }
//
//    @Override
//    public Map<String, Currency> getAllCurrencies() {
//        return new HashMap<>(currencies);
//    }
//
//
//}
