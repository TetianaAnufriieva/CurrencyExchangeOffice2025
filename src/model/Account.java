package model;

import java.util.List;
import java.util.Objects;

public class Account {
    private int accountId;
    private int userId;
    private Currency currency;
    private double balance;
    private List<Transaction> transactionHistory;

    public Account(int accountId, int userId, Currency currency, double balance, List<Transaction> transactionHistory) {
        this.accountId = accountId;
        this.userId = userId;
        this.currency = currency;
        this.balance = balance;
        this.transactionHistory = transactionHistory;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(List<Transaction> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return accountId == account.accountId && userId == account.userId && Double.compare(balance, account.balance) == 0 && Objects.equals(currency, account.currency) && Objects.equals(transactionHistory, account.transactionHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, userId, currency, balance, transactionHistory);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + accountId +
                ", userId=" + userId +
                ", currency=" + currency +
                ", balance=" + balance +
                ", transactionHistory=" + transactionHistory +
                '}';
    }
}

