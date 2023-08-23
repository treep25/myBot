package com.telegram.myBot;

import com.telegram.myBot.brain.CurrencyServiceBinance;
import com.telegram.myBot.brain.MyTelegramBot;
import com.telegram.myBot.brain.fein.client.CurrencyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EnableFeignClients
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@EnableScheduling
public class MyBotApplication {

	public static void main(String[] args) throws TelegramApiException {

		ConfigurableApplicationContext context = SpringApplication.run(MyBotApplication.class, args);
		CurrencyServiceBinance currencyServiceBinance = context.getBean(CurrencyServiceBinance.class);

		new TelegramBotsApi(DefaultBotSession.class)
				.registerBot(new MyTelegramBot(currencyServiceBinance));
	}

}
