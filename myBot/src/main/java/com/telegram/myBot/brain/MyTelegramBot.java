package com.telegram.myBot.brain;

import com.telegram.myBot.brain.fein.client.CurrencyClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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
    private void sendQuestionPanelIfMessageStart(String initialMessage, long chatId) {
        if (FIRST_COMMAND.equals(initialMessage)) {
            sendQuestionPanel(chatId);
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
            sendAnswer(currencyServiceBinance.getCurrencyBinanceValue(update));
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

    private void sendQuestionPanel(long chatId) {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Choose couple of currency")
                .replyMarkup(createQuestionPanelMarkup())
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createQuestionPanelMarkup() {
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

    @Override
    public String getBotUsername() {
        return "*****";
    }

    @Override
    public String getBotToken() {
        return "******";
    }
}
