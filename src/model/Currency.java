package model;

import java.util.HashMap;
import java.util.Objects;

public class Currency {
    public static Currency currency;
    private String code;  // Код валюты (USD, EUR, PLN)
    private double exchangeRate;  // Курс относительно базовой валюты (например, EUR).Можно использовать для двухэтапной конвертации
// например, для USD -> PLN это будет
// USD -> EUR -> PLN


    public Currency(String code, double exchangeRate) {
        this.code = code.toUpperCase(); //  хранится в верхнем регистре
        this.exchangeRate = exchangeRate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency currency)) return false;
        return Double.compare(exchangeRate, currency.exchangeRate) == 0 && Objects.equals(code, currency.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, exchangeRate);
    }

    @Override
    public String toString() {
        return "Currency{" +
                "code='" + code + '\'' +
                ", exchangeRate=" + exchangeRate +
                '}';
    }
}
