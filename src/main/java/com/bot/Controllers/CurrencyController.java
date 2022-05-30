package com.bot.Controllers;

import com.bot.Repositories.CurrencyRepository;
import com.github.kshashov.telegram.api.TelegramMvcController;
import com.github.kshashov.telegram.api.bind.annotation.BotController;
import com.github.kshashov.telegram.api.bind.annotation.BotPathVariable;
import com.github.kshashov.telegram.api.bind.annotation.request.MessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.DecimalFormat;

@BotController
public class CurrencyController implements TelegramMvcController {

    @Autowired
    private CurrencyRepository currencyRepository;

    private String[] currencies = new String[2];

    @MessageRequest("/currency {currency:[\\S]{3}}")
    public String getCurrency(@BotPathVariable("currency") String currency){
        if (currency.equals("USD") || currency.equals("BTC") || currency.equals("EUR") ){
            currencyRepository.updateAllCurrencies();
            return currencyRepository.getCurrencyRate(currency);
        }
        return "Доступнi курси валют: USD, BTC, EUR";
    }

    @MessageRequest("/ {currencyFrom:[\\S]{3}} to {currencyTo:[\\S]{3}}")
    public String getCurrencies(@BotPathVariable("currencyFrom") String currencyFrom, @BotPathVariable("currencyTo") String currencyTo ) {
        currencies[0] = currencyFrom;
        currencies[1] = currencyTo;

        return "Введи суму для переводу";
    }

    @MessageRequest("/ {number:[\\d]+}")
    public String currencyConverter(@BotPathVariable("number") Double number) {
        if (currencies[0] == null && currencies[1] == null)
            return "Invalid input";
        if ((currencies[0].equals("UAH") && currencies[1].equals("USD") || currencies[1].equals("EUR"))
                || (currencies[0].equals("USD") || currencies[0].equals("EUR") && currencies[1].equals("UAH"))){
            currencyRepository.updateAllCurrencies();
            return "По cьогоднiшньому курсу: " + number + " " + currencies[0] + " це " + new DecimalFormat("#.####").format(currencyRepository.getRate(number, currencies[0], currencies[1])) + " " + currencies[1];
        }

        currencies[0] = null;
        currencies[1] = null;

        return "Доступнi курси валют для перевода: USD, EUR\n" +
                "Переводити можна з USD, EUR - UAH\n" +
                "UAH - USD, EUR.";
    }
    @Value("${telegram.bot.token}")
    private String token;

    @Override
    public String getToken() {
        return token;
    }

}
