package com.bot.Entities;

import com.bot.Models.Gamer;
import com.bot.Repositories.GamerRepository;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class Game {

    @Autowired
    private GamerRepository gamerRepository;

    public SendMessage startGame(User user, Chat chat) {
        if (!gamerRepository.checkIfGamerAlreadyExist(user.id()))
            gamerRepository.save(user, chat.id());

        String text = "Добрий день тобi пане, " + user.firstName() + "\n"
                + "Напиши /play@Test_OfOPer22bot щоб почати гру.";

        return new SendMessage(chat.id(), text);
    }

    public SendMessage giveBribe(User user, long chatID){
        Gamer gamer;
        // если юзер начал играть без /start
        if (!gamerRepository.checkIfGamerAlreadyExist(user.id()))
            return new SendMessage(chatID, "Пане " + user.firstName() + ", щоб почати грати, напиши /start_playing@Test_OfOPer22bot\n");
        else
            gamer = gamerRepository.getGamerById(user.id());

        String text;

        if (gamer.getGamer_isArrested()) {
            if (gamer.getScore() >= 10) {
                gamer.setScore(gamer.getScore() - 10);
                gamer.setGamer_isArrested(false);

                gamerRepository.update(gamer);

                text = "Пане " + user.firstName() + ", ти вiдкупився, можешь йти грати.\n"+
                        "Залишок грошей - " + gamer.getScore() + " грн.";;
            } else
                text = "Пане " + user.firstName() + ", ти не маешь стiльки грошей щоб вiдкупитись.\n";
        }else {
            if (gamer.getScore() >= 5) {
                gamer.setScore(gamer.getScore() - 5);
                gamer.setGamer_lastTimeSmoked(null);

                gamerRepository.update(gamer);

                text = "Пане " + user.firstName() + ", ти можешь йти грати.\n" +
                        "Залишок грошей - " + gamer.getScore() + " грн.";
            } else
                text = "Пане " + user.firstName() + ", ти не маешь стiльки грошей щоб грати ще раз.\n";
        }

        return new SendMessage(chatID, text);
    }

    public SendMessage playGame(User user, Chat chat){
        Gamer gamer;
        // если юзер начал играть без /start
        if (!gamerRepository.checkIfGamerAlreadyExist(user.id()))
            return new SendMessage(chat.id(), "Пане " + user.firstName() + ", щоб почати грати, напиши /start_playing@Test_OfOPer22bot\n");
        else{
            gamer = gamerRepository.getGamerById(user.id());
            if (gamer.getGamer_username() == null || !gamer.getGamer_username().equals(user.firstName())){
                gamer.setGamer_username(user.firstName());
                gamerRepository.update(gamer);
            }
            gamer = gamerRepository.getGamerById(user.id());
        }


        if (gamer.getGamer_isArrested()){
            long diff = new Date().getTime() - gamer.getGamer_arrestedTime();
            int minutes = (int) diff / (60 * 1000);

            if(minutes < 2880)
                return new SendMessage(chat.id(), "Сорі, пане " + user.firstName() + ", ти ще в ТРО"
                        + ", чекай поки вiдпустять.\n"
                        + "Вiдпустять через " + (2880 - minutes) + " хвилин.");
            else
                gamer.setGamer_isArrested(false);
        }

        // если юзер еще не играл, добавляем его и ставим текущую дату
        if (gamer.getGamer_lastTimeSmoked() != null) {
            long diff = new Date().getTime() - gamer.getGamer_lastTimeSmoked();
            int minutes = (int) diff / (60 * 1000);

            if (minutes < 1440)
                return new SendMessage(chat.id(), "Сорі, пане " + user.firstName() + ", ти награвся.\n"
                        + "Можна буде пограти через " + (1440 - minutes) + " хвилин.");
        }

        // считаем добавиться число или отнимется
        int randNumber = ThreadLocalRandom.current().nextInt(1, 10);
        int percentage = ThreadLocalRandom.current().nextInt(1, 100);
        int randomWeight;

        if(percentage <= 8)
            return arrestGamer(gamer, user.firstName(), chat.id());
        else if (percentage >= 60 && percentage <= 65)
            return gamerLostAllPoints(gamer, user.firstName(), chat.id());
        else if (percentage <= 30)
            randomWeight = randNumber * -1 + 2 > 0 ? randNumber * -1 + 2 : randNumber * -1;
        else
            randomWeight = randNumber;


        long newWeight = gamer.getScore() + randomWeight >= 0 ? gamer.getScore() + randomWeight : 0;

        gamer.setScore(newWeight);
        gamer.setGamer_lastTimeSmoked(new Date().getTime());

        gamerRepository.update(gamer);

        String text;
        if (randomWeight > 0)
            text = "Пане " + user.firstName() + "\nти знайшов: " + randomWeight + " грн за сьогоднi.\n"
                    + "Всього ти знайшов: " + newWeight + " грн.\n";
        else text = "Пане " + user.firstName() + "\nты знайшов: " + randomWeight * -1 + " грн сьогоднi, коли йшов на роботу.\n"
                + "Але оболонськi гопники все вiджали.\n"
                + "Всього ти знайшов: " + newWeight + " грн.\n";

        return new SendMessage(chat.id(), text);
    }

    public SendMessage topGamers(Chat chat){
        List<Gamer> topGamers = gamerRepository.getTopGamers(chat.id());

        int i = 1;
        StringBuilder text = new StringBuilder();
        for(Gamer gamer : topGamers)
            text.append(i++).append(": ").append(gamer.getGamer_username() == null ? gamer.getGamer_id() : gamer.getGamer_username()).append(": ").append(gamer.getScore()).append(" грам.\n");

        return new SendMessage(chat.id(), text.toString());
    }

    private SendMessage gamerLostAllPoints(Gamer gamer, String userFirstName, long chatID){
        long gamerScore = gamer.getScore();

        long weightToEveryOne = gamerScore != 0 ? (gamerScore / (gamerRepository.getCountOfGamers(chatID) - 1)) : 0;

        gamerRepository.addNumberToEveryOne(weightToEveryOne, gamer.getGamer_id(), chatID);

        gamer.setScore(0L);
        gamer.setGamer_lastTimeSmoked(new Date().getTime());

        gamerRepository.update(gamer);

        String text = "Пане " + userFirstName + ", сьогоднi тобi не подфартило, ти роздав все нажите добро,"
                + " кожному гравцю переходить - " + weightToEveryOne + " грн.\n";

        return new SendMessage(chatID, text);
    }

    private SendMessage arrestGamer(Gamer gamer, String userFirstName, long chatID){
        gamer.setGamer_isArrested(true);
        gamer.setGamer_arrestedTime(new Date().getTime());

        gamerRepository.update(gamer);

        String text = "Пане " + userFirstName + ", сьогоднi тебе спiмало ТРО, ти сидишь на блокпосту"
                + " i не зможешь грати 2 доби.\n";

        return new SendMessage(chatID, text);

    }
}
