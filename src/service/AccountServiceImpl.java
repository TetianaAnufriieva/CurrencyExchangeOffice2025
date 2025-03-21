package service;

import model.Account;
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

    @Override
    public void createAccount(int userId, String currency, double balance) {
        if (accountRepository.exists(userId)) {
            throw new IllegalArgumentException("User  уже существует");
        }
        else
         accountRepository.createAccount(userId, currency, balance);




    }

    // Закрытие счета (с проверками: если на счету есть средства? что делать?)
    @Override
    public boolean close(int userId, String currency) {


        Account account = accountRepository.findById(userId);
        if ( account.getBalance() > 0) {
            throw new IllegalArgumentException("На счете есть средства");
        }
        return true;
    }
}
