package service;

import model.Transaction;
import model.TypeTransaction;
import model.User;

import java.util.List;

public interface TransactionService {
    // Создание новой операции
    void createTransaction(TypeTransaction type, double amount, String currency);
    // для Admin
    List<Transaction> getAllTransactions();
    // Получить операции конкретного пользователя по всем счетам
    List<Transaction> getUserTransactions(int userId);
    // Получить операции по конкретной валюте
    List<Transaction> getTransactionsByCurrency(String currencyCode);
    void deposit(User user, String currency, double amount);
    boolean withdraw(User user, String currency, double amount);
}
