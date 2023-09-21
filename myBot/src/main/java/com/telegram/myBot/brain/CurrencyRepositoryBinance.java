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
    private final Map<Predicate<String>, Supplier<BotAnswer>> botCurrencyPairCommand;
    private final Map<Predicate<String>, Supplier<BotAnswer>> botCurrencyAVGCommand;
    private final Map<Predicate<String>, Supplier<BotAnswer>> botCurrencyStat24HrCommand;

    private final Map<String, BotAnswer> repo;

    @Autowired
    public CurrencyRepositoryBinance(CurrencyClient currencyClient) {
        this.repo = new HashMap<>();
        this.botCurrencyPairCommand = Map.of(
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

        this.botCurrencyAVGCommand = Map.of(
                callbackData -> USDT_UAH.getCurrency().equals(callbackData), () ->
                        BotAnswer
                                .builder()
                                .answer("AVG price for 5 minutes\n" + USDT_UAH.getCurrency() + " = " + currencyClient.getCurrencyAvg5Mins("USDTUAH").get("price"))
                                .build(),

                callbackData -> BTC_USDT.getCurrency().equals(callbackData), () ->
                        BotAnswer
                                .builder()
                                .answer("AVG price for 5 minutes\n" + BTC_USDT.getCurrency() + " = " + currencyClient.getCurrencyAvg5Mins("BTCUSDT").get("price"))
                                .build(),

                callbackData -> DOGE_USDT.getCurrency().equals(callbackData), () ->
                        BotAnswer
                                .builder()
                                .answer("AVG price for 5 minutes\n" + DOGE_USDT.getCurrency() + " = " + currencyClient.getCurrencyAvg5Mins("DOGEUSDT").get("price"))
                                .build(),

                callbackData -> ETH_USDT.getCurrency().equals(callbackData), () ->
                        BotAnswer
                                .builder()
                                .answer("AVG price for 5 minutes\n" + ETH_USDT.getCurrency() + " = " + currencyClient.getCurrencyAvg5Mins("ETHUSDT").get("price"))
                                .build()
        );
        this.botCurrencyStat24HrCommand = Map.of(
                callbackData -> USDT_UAH.getCurrency().equals(callbackData), () ->
                        BotAnswer
                                .builder()
                                .cryptoCurrency(USDT_UAH.getCurrency())
                                .backResult(currencyClient.getCurrencyChanges24hr("USDTUAH"))
                                .build(),

                callbackData -> BTC_USDT.getCurrency().equals(callbackData), () ->
                        BotAnswer
                                .builder()
                                .cryptoCurrency(BTC_USDT.getCurrency())
                                .backResult(currencyClient.getCurrencyChanges24hr("BTCUSDT"))
                                .build(),

                callbackData -> DOGE_USDT.getCurrency().equals(callbackData), () ->
                        BotAnswer
                                .builder()
                                .cryptoCurrency(DOGE_USDT.getCurrency())
                                .backResult(currencyClient.getCurrencyChanges24hr("DOGEUSDT"))
                                .build(),

                callbackData -> ETH_USDT.getCurrency().equals(callbackData), () ->
                        BotAnswer
                                .builder()
                                .cryptoCurrency(ETH_USDT.getCurrency())
                                .backResult(currencyClient.getCurrencyChanges24hr("ETHUSDT"))
                                .build()
        );
    }

    @PostConstruct
    public void onStart() {
        updateCurrentCurrenciesSchedule();
    }

    @Scheduled(fixedRate = 360000)
    public void onSchedule() {
        updateCurrentCurrenciesSchedule();
    }

    private void updateCurrentCurrenciesSchedule() {
        for (Map.Entry<Predicate<String>, Supplier<BotAnswer>> entry : botCurrencyPairCommand.entrySet()) {
            Predicate<String> currency = entry.getKey();
            Supplier<BotAnswer> getCurrency = entry.getValue();

            for (String currencyPattern : getAllCurrenciesInBrackets()) {

                if (currency.test(currencyPattern)) {
                    repo.put(currencyPattern, getCurrency.get());
                }
            }
        }
    }

    public BotAnswer getCurrencyPairBinanceValue(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        long chatId = callbackQuery.getMessage().getChatId();
        String callbackData = callbackQuery.getData();

        BotAnswer answer = repo.get(callbackData);
        answer.setChatId(chatId);

        return answer;
    }

    public BotAnswer getCurrencyAVGBinanceValue(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        long chatId = callbackQuery.getMessage().getChatId();
        String callbackData = callbackQuery.getData();
        for (Map.Entry<Predicate<String>, Supplier<BotAnswer>> entry : botCurrencyAVGCommand.entrySet()) {
            Predicate<String> currency = entry.getKey();
            Supplier<BotAnswer> getCurrency = entry.getValue();

            if (currency.test(callbackData)) {
                BotAnswer answer = getCurrency.get();
                answer.setChatId(chatId);

                return answer;
            }

        }
        return null;
    }

    public BotAnswer getCurrencyStat24hrBinanceValue(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        long chatId = callbackQuery.getMessage().getChatId();
        String callbackData = callbackQuery.getData();
        for (Map.Entry<Predicate<String>, Supplier<BotAnswer>> entry : botCurrencyStat24HrCommand.entrySet()) {
            Predicate<String> currency = entry.getKey();
            Supplier<BotAnswer> getCurrency = entry.getValue();

            if (currency.test(callbackData)) {
                BotAnswer answer = getCurrency.get();
                answer.setChatId(chatId);

                answer.setAnswer("24hr stat "+ answer.getCryptoCurrency()+"\n" +
                        "price change: "+answer.getBackResult().get("priceChange")+"\n"+
                        "price change in %: "+answer.getBackResult().get("priceChangePercent")+"\n"+
                        "bid price: "+answer.getBackResult().get("bidPrice")+"\n"+
                        "ask price: "+answer.getBackResult().get("askPrice")+"\n"+
                        "last price: "+answer.getBackResult().get("lastPrice")+"\n"+
                        "high price: "+answer.getBackResult().get("highPrice")+"\n"+
                        "low price: "+answer.getBackResult().get("lowPrice")+"\n"+
                        "open price: "+answer.getBackResult().get("openPrice"));

                return answer;
            }

        }
        return null;
    }


}
