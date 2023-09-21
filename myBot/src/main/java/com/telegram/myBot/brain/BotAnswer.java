package com.telegram.myBot.brain;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class BotAnswer {
    private long chatId;
    private String answer;
    private String cryptoCurrency;
    private Map<String,String> backResult;
}
