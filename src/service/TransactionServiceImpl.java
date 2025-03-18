package service;

import model.Transaction;
import model.TypeTransaction;
import model.User;

import java.util.List;

public class TransactionServiceImpl implements TransactionService {
    @Override
    public void createTransaction(TypeTransaction type, double amount, String currency) {

    }

    @Override
    public List<Transaction> getAllTransactions() {
        return List.of();
    }

// Просмотр истории операций по всем счетам
    @Override
    public List<Transaction> getUserTransactions(int userId) {
        return List.of();
    }

// Просмотр истории операций по конкретной валюте
    @Override
    public List<Transaction> getTransactionsByCurrency(String currencyCode) {
        // TODO: пользователь получает только информацию о своем аккаунте, но администратор получает информацию обо всех аккаунтах в этой валюте
        return List.of();
    }

// Пополнение счета в выбранной валюте (проверка существования такого счета
// у пользователя. Если отсутствует - открыть соответствующий счет)
    @Override
    public void deposit(User user, String currency, double amount) {

    }

// Снятие средств со счета (с соответствующими проверками возможности операции)
    @Override
    public boolean withdraw(User user, String currency, double amount) {
        return false;
    }

}
