package com.telegram.myBot;

import java.util.ArrayList;
import java.util.List;

public enum CurrencyPool {
    USDT_UAH ("USDT/UAH"),
    BTC_USDT ("BTC/USDT"),
    DOGE_USDT ("DOGE/USDT"),
    ETH_USDT ("ETH/USDT");
    private final String currency;
    CurrencyPool(String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return currency;
    }
    public static List<String> getAllCurrenciesInBrackets() {
        List<String> currenciesInBrackets = new ArrayList<>();
        for (CurrencyPool currencyPool : CurrencyPool.values()) {
            currenciesInBrackets.add(currencyPool.getCurrency());
        }
        return currenciesInBrackets;
    }
}
