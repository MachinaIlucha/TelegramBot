package com.bot.game;

import com.bot.models.Gamer;
import com.bot.repository.GamerRepository;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class Game {
    private final GamerRepository gamerRepository;

    @Autowired
    public Game(GamerRepository gamerRepository) {
        this.gamerRepository = gamerRepository;
    }

    public SendMessage startGame(User user, Chat chat) {
        if (gamerRepository.getGamer(user.id()) == null){
            Gamer gamer = new Gamer();
            gamer.setScore(0L);
            if (user.username() != null)
                gamer.setGamer_username(user.username());
            gamer.setGamer_chatId(chat.id());
            gamer.setGamer_id(user.id());
            gamer.setGamer_isArrested(false);
            gamer.setGamer_lastTimeSmoked(null);
            gamer.setGamer_arrestedTime(null);

            gamerRepository.save(gamer);
        }

        String text = "Добрий день тобi пане, " + user.firstName() + "\n"
                + "Напиши /play@Test_OfOPer22bot щоб почати гру.";

        return new SendMessage(chat.id(), text);
    }

    public SendMessage giveBribe(User user, long chatID){
        Gamer gamer = gamerRepository.getGamer(user.id());

        if (gamer == null)
            return new SendMessage(chatID, "Пане " + user.firstName() + ", щоб почати грати, напиши /start_playing@Test_OfOPer22bot\n");

        String text;
        if (gamer.getGamer_isArrested()) {
            if (gamer.getScore() >= 10) {
                gamer.setScore(gamer.getScore() - 10);
                gamer.setGamer_isArrested(false);

                gamerRepository.save(gamer);

                text = "Пане " + user.firstName() + ", ти вiдкупився, можешь йти грати.\n"+
                        "Залишок грошей - " + gamer.getScore() + " грам.";;
            } else
                text = "Пане " + user.firstName() + ", ти не маешь стiльки грошей щоб вiдкупитись.\n";
        }else {
            if (gamer.getScore() >= 5) {
                gamer.setScore(gamer.getScore() - 5);
                gamer.setGamer_lastTimeSmoked(null);

                gamerRepository.save(gamer);

                text = "Пане " + user.firstName() + ", ти можешь йти грати.\n" +
                        "Залишок грошей - " + gamer.getScore() + " грам.";
            } else
                text = "Пане " + user.firstName() + ", ти не маешь стiльки грошей щоб грати ще раз.\n";
        }

        return new SendMessage(chatID, text);
    }

    public SendMessage playGame(User user, Chat chat){
        Gamer gamer = gamerRepository.getGamer(user.id());
        // если юзер начал играть без /start
        if (gamer == null)
            return new SendMessage(chat.id(), "Пане " + user.firstName() + ", щоб почати грати, напиши /start_playing@Test_OfOPer22bot\n");

        if (gamer.getGamer_username() == null || !gamer.getGamer_username().equals(user.firstName())){
            gamer.setGamer_username(user.firstName());
            gamerRepository.save(gamer);
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

        if(percentage <= 3)
            return arrestGamer(gamer, user.firstName(), chat.id());
        else if (percentage <= 8)
            return gamerLostAllPoints(gamer, user.firstName(), chat.id());
        else if (percentage <= 30)
            randomWeight = -randNumber + 2 > 0 ? -randNumber + 2 : -randNumber;
        else
            randomWeight = randNumber;


        long newWeight = gamer.getScore() + randomWeight >= 0 ? gamer.getScore() + randomWeight : 0;

        gamer.setScore(newWeight);
        gamer.setGamer_lastTimeSmoked(new Date().getTime());

        gamerRepository.save(gamer);

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

        gamerRepository.save(gamer);

        String text = "Пане " + userFirstName + ", сьогоднi тобi не подфартило, ти роздав все нажите добро,"
                + " кожному гравцю переходить - " + weightToEveryOne + " грн.\n";

        return new SendMessage(chatID, text);
    }

    private SendMessage arrestGamer(Gamer gamer, String userFirstName, long chatID){
        gamer.setGamer_isArrested(true);
        gamer.setGamer_arrestedTime(new Date().getTime());

        gamerRepository.save(gamer);

        String text = "Пане " + userFirstName + ", сьогоднi тебе спiмало ТРО, ти сидишь на блокпосту"
                + " i не зможешь грати 2 доби.\n";

        return new SendMessage(chatID, text);

    }
}
