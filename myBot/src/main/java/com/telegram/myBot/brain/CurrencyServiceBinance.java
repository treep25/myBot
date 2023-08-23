package com.telegram.myBot.brain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CurrencyServiceBinance {

    private final CurrencyRepositoryBinance currencyRepositoryBinance;

    public BotAnswer getCurrencyBinanceValue(Update update) {
        return currencyRepositoryBinance
                .getCurrencyBinanceValue(update);
    }
}
