package repository;

import model.Transaction;
import model.TypeTransaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {

    // Создание операции
    Transaction createTransaction(TypeTransaction type, int accountId, String currency, double amount, LocalDateTime date);
    // Получение всех операций для Admin
    List<Transaction> findAll();

    // Для глобального отчета пользователя
    List<Transaction> findByUserId(int userId);

    // Получение операций по счету
    // for user 1 acc report
    List<Transaction> findByAccountId(int accountId);
}
