package com.telegram.myBot.brain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.LoginUrl;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MyTelegramBot extends TelegramLongPollingBot {
    private final static String FIRST_COMMAND = "/start";
    private final CurrencyServiceBinance currencyServiceBinance;
    private String lastCallbackData = "";

    private void sendQuestionPanelIfMessageStart(String initialMessage, long chatId) {
        if (FIRST_COMMAND.equals(initialMessage)) {
            sendQuestionPanel(chatId, "Start page, Choose one of the 3 options", createQuestionPanelMarkupMenu());
        } else {
            sendAnswer(BotAnswer
                    .builder()
                    .chatId(chatId)
                    .answer("Hi there, I am cryptocurrency bot." +
                            "\nI am integrated with binance platform and my answers " +
                            "will be based only on this platform" +
                            "\nHow can I help you?" +
                            "\nIt`s easy. " +
                            "There are some actions I could do for you.\n" +
                            "I can provide you with a pairs summary on the market in seconds.\n" +
                            "Also, the average price of a particular pair.\n" +
                            "Summaries of how the price has changed over the last day.")
                    .build());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            //переносить в класс связан просто с ответами
            sendQuestionPanelIfMessageStart(messageText, chatId);
        } else if (update.hasCallbackQuery()) {

            CallbackQuery callbackQuery = update.getCallbackQuery();
            long chatId = callbackQuery.getMessage().getChatId();
            String callbackData = callbackQuery.getData();

            if ("Pair".equals(callbackData)) {
                lastCallbackData = "Pair";
                sendQuestionPanel(chatId, "get pair", createQuestionPanelMarkupPair());
                return;
            } else if ("AVG".equals(callbackData)) {
                lastCallbackData = "AVG";
                sendQuestionPanel(chatId, "get AVG value", createQuestionPanelMarkupPair());
                return;
            } else if ("Stat24hr".equals(callbackData)) {
                lastCallbackData = "Stat24hr";
                sendQuestionPanel(chatId, "Get 24hr stat", createQuestionPanelMarkupPair());
                return;
            }

            if (lastCallbackData.equals("Pair")) {
                sendAnswer(currencyServiceBinance.getCurrencyPairBinanceValue(update));

            } else if (lastCallbackData.equals("AVG")) {
                sendAnswer(currencyServiceBinance.getCurrencyAVGBinanceValue(update));

            } else if (lastCallbackData.equals("Stat24hr")) {
                sendAnswer(currencyServiceBinance.getCurrencyStat24hrBinanceValue(update));

            }

        }
    }

    private void sendAnswer(BotAnswer botAnswer) {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(botAnswer.getChatId()))
                .text(botAnswer.getAnswer())
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendQuestionPanel(long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .replyMarkup(markup)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createQuestionPanelMarkupPair() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().callbackData("USDT/UAH").text("USDT/UAH").build());
        row.add(InlineKeyboardButton.builder().callbackData("BTC/USDT").text("BTC/USDT").build());
        row.add(InlineKeyboardButton.builder().callbackData("DOGE/USDT").text("DOGE/USDT").build());
        row.add(InlineKeyboardButton.builder().callbackData("ETH/USDT").text("ETH/USDT").build());

        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup createQuestionPanelMarkupMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().callbackData("Pair").text("get a price on a pair").build());
        row.add(InlineKeyboardButton.builder().callbackData("AVG").text("get average price for the last 5 min").loginUrl(LoginUrl.builder()
                .url("https://api.binance.com/api/v3/ticker/24hr?symbol=USDTUAH").build()).build());
        row.add(InlineKeyboardButton.builder().callbackData("Stat24hr").text("get 24 hr statistic of current pair").build());

        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }

    @Override
    public String getBotUsername() {
        return "testLeeloobotbot";
    }

    @Override
    public String getBotToken() {
        return "6338712824:AAGf3bBHtvGK3opscXYaQ1Q_NyqTND8JIRM";
    }
}
