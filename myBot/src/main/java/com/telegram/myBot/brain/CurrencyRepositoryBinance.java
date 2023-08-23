package com.telegram.myBot.brain;

import com.telegram.myBot.brain.fein.client.CurrencyClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.telegram.myBot.CurrencyPool.*;

@Repository
public class CurrencyRepositoryBinance {
    private final Map<Predicate<String>, Supplier<BotAnswer>> botCurrencyCommands;

    private final Map<String, BotAnswer> repo;

    @Autowired
    public CurrencyRepositoryBinance(CurrencyClient currencyClient) {
        this.repo = new HashMap<>();
        this.botCurrencyCommands = Map.of(
                callbackData -> USDT_UAH.getCurrency().equals(callbackData), () ->
                        BotAnswer
                                .builder()
                                .answer(USDT_UAH.getCurrency() + " = " + currencyClient.getCurrencyDivUSDT("USDTUAH").get("price"))
                                .build(),

                callbackData -> BTC_USDT.getCurrency().equals(callbackData), () ->
                        BotAnswer
                                .builder()
                                .answer(BTC_USDT.getCurrency() + " = " + currencyClient.getCurrencyDivUSDT("BTCUSDT").get("price"))
                                .build(),

                callbackData -> DOGE_USDT.getCurrency().equals(callbackData), () ->
                        BotAnswer
                                .builder()
                                .answer(DOGE_USDT.getCurrency() + " = " + currencyClient.getCurrencyDivUSDT("DOGEUSDT").get("price"))
                                .build(),

                callbackData -> ETH_USDT.getCurrency().equals(callbackData), () ->
                        BotAnswer
                                .builder()
                                .answer(ETH_USDT.getCurrency() + " = " + currencyClient.getCurrencyDivUSDT("ETHUSDT").get("price"))
                                .build()
        );
    }

    @PostConstruct
    public void onStart() {
        updateCurrentCurrenciesSchedule();
    }

    @Scheduled(fixedRate = 2000)
    public void onSchedule() {
        updateCurrentCurrenciesSchedule();
    }
    private void updateCurrentCurrenciesSchedule() {
        for (Map.Entry<Predicate<String>, Supplier<BotAnswer>> entry : botCurrencyCommands.entrySet()) {
            Predicate<String> currency = entry.getKey();
            Supplier<BotAnswer> getCurrency = entry.getValue();

            for (String currencyPattern : getAllCurrenciesInBrackets()) {

                if (currency.test(currencyPattern)) {
                    repo.put(currencyPattern, getCurrency.get());
                }
            }
        }
    }
    public BotAnswer getCurrencyBinanceValue(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        long chatId = callbackQuery.getMessage().getChatId();
        String callbackData = callbackQuery.getData();

        BotAnswer answer = repo.get(callbackData);
        answer.setChatId(chatId);

        return answer;
    }
}
