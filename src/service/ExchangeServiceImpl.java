package service;

import model.Account;
import model.Currency;
import model.User;
import java.util.HashMap;

public class ExchangeServiceImpl implements ExchangeService {

    @Override
    public void exchange(int userId, String fromCurrency, String toCurrency, double amount) {

        if (amount <= 0) {
            System.out.println("Сумма для обмена должна быть больше 0.");
            return ;
        }

        double exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        double userBalance = getUserBalance(userId, fromCurrency);

        if (userBalance >= amount) {
            double exchangedAmount = amount * exchangeRate;
            updateUserBalance(userId, fromCurrency, -amount);
            updateUserBalance(userId, toCurrency, exchangedAmount);

            System.out.println("Обмен успешно завершен! " + amount + " " + fromCurrency + " обмен на " + exchangedAmount + " " + toCurrency);
        } else {
            System.out.println("Недостаточно денег для обмена.");
        }
    }

    private double getExchangeRate(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals("USD") && toCurrency.equals("EUR")) {
            return 42.60;
        } else if (fromCurrency.equals("EUR") && toCurrency.equals("USD")) {
            return 1 / 42.60;
        }
        return 1.0;
    }

    private double getUserBalance(int userId, String currency) {
        return 1000.0;
    }

    // Оновлення балансу
    private void updateUserBalance(int userId, String currency, double amount) {
        System.out.println("Баланс пользователя обновлен: " + currency + " " + amount);
    }




}




