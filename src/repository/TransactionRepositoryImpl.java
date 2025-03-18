package repository;

import model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionRepositoryImpl implements TransactionRepository {

    private static final String FILE_NAME = "transactions.dat";
    private List<Transaction> transactions = new ArrayList<>();

    public TransactionRepositoryImpl(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public void createTransaction(Transaction transaction) {

    }

    @Override
    public List<Transaction> findAll() {
        return List.of();
    }

    @Override
    public List<Transaction> findByAccountId(int accountId) {
        return List.of();
    }

    @Override
    public List<Transaction> findByUserId(int userId) {
        return List.of();
    }

}
