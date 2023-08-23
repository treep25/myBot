package com.telegram.myBot.brain.fein.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "currencyClient", url = "https://api.binance.com")
public interface CurrencyClient {
    @RequestMapping(method = RequestMethod.GET, value = "/api/v3/ticker/price")
    Map<String, String> getCurrencyDivUSDT(@RequestParam("symbol") String symbol);

    @RequestMapping(method = RequestMethod.GET, value = "/api/v3/ticker/24hr")
    Map<String, String> getCurrencyChanges24hr(@RequestParam("symbol") String symbol);

    @RequestMapping(method = RequestMethod.GET, value = "/api/v3/avgPrice")
    Map<String, String> getCurrencyAvg5Mins(@RequestParam("symbol") String symbol);

}
