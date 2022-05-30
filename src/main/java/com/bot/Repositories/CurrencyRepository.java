package com.bot.Repositories;

import com.bot.Models.Currency;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
@Transactional
public class CurrencyRepository {

    private final String SQL_getCurrencyRate = "FROM Currency WHERE ccy = :ccy";
    private final String SQL_updateCurrencyRate = "UPDATE Currency set buy = :buy, sale = :sale" +
            " where ccy = :ccy";
    @PersistenceContext
    private EntityManager entityManager;

    public void updateAllCurrencies(){
        String url = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
        String jsonString = "";
        try {
            Document doc = Jsoup.connect(url).ignoreContentType(true).get();
            jsonString = doc.body().text();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Currency[] currencyArray = gson.fromJson(jsonString, Currency[].class);
        List<Currency> currencyList = new ArrayList<>(Arrays.asList(currencyArray));

        try {
            for (Currency currency : currencyList){
                Query query = entityManager.createQuery(SQL_updateCurrencyRate);
                query.setParameter("ccy", currency.getCcy());
                query.setParameter("buy", currency.getBuy());
                query.setParameter("sale", currency.getSale());
                query.executeUpdate();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCurrencyRate(String cur){
        Query query = entityManager.createQuery(SQL_getCurrencyRate);
        query.setParameter("ccy", cur);
        Currency currency;
        try {
            currency = (Currency) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return currency.toString();
    }

    public double getRate(Double number, String currencyFrom, String currencyTo){
        Query query = entityManager.createQuery(SQL_getCurrencyRate);
        if (currencyTo.equals("UAH"))
            query.setParameter("ccy", currencyFrom);
        else query.setParameter("ccy", currencyTo);
        Currency currency;
        try {
            currency = (Currency) query.getSingleResult();
        } catch (NoResultException e) {
            return 0.0;
        }
        if (currencyFrom.equals("UAH")){
            return number / currency.getSale();
        } else return number * currency.getSale();
    }
}
