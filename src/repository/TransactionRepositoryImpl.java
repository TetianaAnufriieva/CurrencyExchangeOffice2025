package repository;

import model.Transaction;
import model.TypeTransaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TransactionRepositoryImpl implements TransactionRepository {

//    private static final String FILE_NAME = "transactions.dat";
    private final AtomicInteger currentId = new AtomicInteger(1);
    private List<Transaction> transactions = new ArrayList<>();
    private AccountRepository accountRepository;

    public TransactionRepositoryImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Transaction createTransaction(TypeTransaction type, int accountId, String currency, double amount, LocalDateTime date) {
        Transaction transaction = new Transaction(currentId.getAndIncrement(), type, accountId, currency, amount, date);
        transactions.add(transaction);
        return transaction;
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions);
    }

    @Override
    public List<Transaction> findByAccountId(int accountId) {

        return transactions.stream()
                .filter(transaction -> transaction.getAccountId() == accountId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByUserId(int userId) {
        // Получаем все аккаунты пользователя
        List<Integer> accountIds = accountRepository.findByUser(userId).stream()
                .map(account -> account.getAccountId())
                .collect(Collectors.toList());

        // Фильтруем транзакции по найденным аккаунтам
        return transactions.stream()
                .filter(transaction -> accountIds.contains(transaction.getAccountId()))
                .collect(Collectors.toList());
    }

}
