package com.telegram.myBot.brain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CurrencyServiceBinance {

    private final CurrencyRepositoryBinance currencyRepositoryBinance;

    public BotAnswer getCurrencyPairBinanceValue(Update update) {
        return currencyRepositoryBinance
                .getCurrencyPairBinanceValue(update);
    }

    public BotAnswer getCurrencyAVGBinanceValue(Update update) {
        return currencyRepositoryBinance
                .getCurrencyAVGBinanceValue(update);
    }
    public BotAnswer getCurrencyStat24hrBinanceValue(Update update) {
        return currencyRepositoryBinance
                .getCurrencyStat24hrBinanceValue(update);
    }
}
