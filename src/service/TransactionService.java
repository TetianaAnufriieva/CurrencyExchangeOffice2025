package service;

import model.Transaction;
import model.TypeTransaction;
import model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    // Создание новой операции
    Transaction createTransaction(TypeTransaction type, int accountId, String currency, double amount, LocalDateTime date);
    // Возвращает все транзакции для администратора
    List<Transaction> getAllTransactions();
    // Возвращает все операции конкретного пользователя
    List<Transaction> getUserTransactions(int userId);
    // Получить операции по конкретной валюте для администратора
    List<Transaction> getAllTransactionsByCurrency(String currency);
    // Получить операции по конкретной валюте для пользователя
    List<Transaction> getUserTransactionsByCurrency(int userId, String currency);
    // Пополнение счёта в указанной валюте
    void deposit(int userId, String currency, double amount);
    // Снятие средств со счета
    boolean withdraw(int userId, String currency, double amount);
}
