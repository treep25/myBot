package com.telegram.myBot.brain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BotAnswer {
    private long chatId;
    private String answer;
}
