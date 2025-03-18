package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {
    private int transactionId;
    private TypeTransaction type; // Тип операции (пополнение, снятие, обмен)
    private int accountId; // ID счета, к которому относится операция
    private String currency;
    private double amount;
    private LocalDateTime date;

    public Transaction(int transactionId, TypeTransaction type, int accountId, String currency, double amount, LocalDateTime date) {
        this.transactionId = transactionId;
        this.type = TypeTransaction.DEPOSIT;
        this.accountId = accountId;
        this.currency = currency;
        this.amount = amount;
        this.date = date;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public TypeTransaction getType() {
        return type;
    }

    public void setType(TypeTransaction type) {
        this.type = type;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction that)) return false;
        return transactionId == that.transactionId && accountId == that.accountId && Double.compare(amount, that.amount) == 0 && type == that.type && Objects.equals(currency, that.currency) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, type, accountId, currency, amount, date);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", type=" + type +
                ", accountId=" + accountId +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}
