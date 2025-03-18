package repository;

import model.Transaction;

import java.util.List;

public interface TransactionRepository {

    // Создание операции
    void createTransaction(Transaction transaction);
    // Получение всех операций
    // для Admin
    List<Transaction> findAll();

    // for user global report
    List<Transaction> findByUserId(int userId);

    // Получение операций по счету
    // for user 1 acc report
    List<Transaction> findByAccountId(int accountId);
}
