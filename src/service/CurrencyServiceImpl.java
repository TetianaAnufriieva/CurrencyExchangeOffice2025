package service;

import model.Account;
import model.Currency;
import model.Role;
import model.User;
import repository.AccountRepository;
import repository.CurrencyRepository;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class CurrencyServiceImpl implements CurrencyService {
  private final CurrencyRepository currencyRepository;
  private final AccountRepository accountRepository;

  public CurrencyServiceImpl(CurrencyRepository currencyRepository, AccountRepository accountRepository) {
    this.currencyRepository = currencyRepository;
    this.accountRepository = accountRepository;
  }

  @Override
  public void addCurrency(User currentUser, String code, double exchangeRate) {
    if (currentUser.getRole() != Role.ADMIN) {
      throw new SecurityException("Только администратор может добавлять валюты.");
    }
    if (currencyRepository.findByCode(code).isPresent()) {
      throw new IllegalArgumentException("Валюта с кодом " + code + " уже существует.");
    }
    currencyRepository.create(code, exchangeRate);
  }

  @Override
  public void updateCurrency(User currentUser, String code, double exchangeRate) {
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
  public void removeCurrency(User currentUser, String code) {
    if (currentUser.getRole() != Role.ADMIN) {
      throw new SecurityException("Только администратор может удалять валюты.");
    }
    boolean hasAccounts = accountRepository.findAll().stream()
            .anyMatch((Account account) -> account.getCurrency()
                    .getCode().equals(code));
    if (hasAccounts) {
      System.out.println("Невозможно удалить валюту, так как существуют аккаунты с этой валютой.");
      return;
    }
    currencyRepository.delete(code);
  }

  @Override
  public Map<String, Currency> getAllCurrencies(User currentUser) {
    if (currentUser.getRole() == Role.BLOCKED) {
      throw new SecurityException("У вас нет доступа к просмотру валют.");
    }
    return Collections.unmodifiableMap(currencyRepository.findAll());
  }
}