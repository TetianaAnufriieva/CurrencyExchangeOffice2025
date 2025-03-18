package service;

import repository.AccountRepository;
import repository.TransactionRepository;

public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    public AccountServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // Просмотр баланса (остатка на всех счетах или каком-то конкретном счете)
    @Override
    public double checkBalance(int userId, String currency) {
        return 0;
    }

    // Открытие нового счета
    @Override
    public void createAccount(int userId, String currency) {


    }

    // Закрытие счета (с проверками: если на счету есть средства? что делать?)
    @Override
    public boolean close(int userId, String currency) {
        return false;
    }
}
