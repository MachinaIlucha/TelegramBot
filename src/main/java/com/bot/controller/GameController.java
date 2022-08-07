package com.bot.controller;

import com.bot.game.Game;
import com.github.kshashov.telegram.api.MessageType;
import com.github.kshashov.telegram.api.TelegramMvcController;
import com.github.kshashov.telegram.api.bind.annotation.BotController;
import com.github.kshashov.telegram.api.bind.annotation.BotRequest;
import com.github.kshashov.telegram.api.bind.annotation.request.MessageRequest;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@BotController
public class GameController implements TelegramMvcController {

    private final Game smokeGame;

    @Autowired
    public GameController(Game smokeGame) {
        this.smokeGame = smokeGame;
    }

    @BotRequest(value = "/help@Test_OfOPer22bot", type = {MessageType.CALLBACK_QUERY, MessageType.MESSAGE})
    public BaseRequest listOfCommands(Chat chat, Update update) {
        String text =   "/start_playing@Test_OfOPer22bot -\n команда для старту гри \n" +
                "/play@Test_OfOPer22bot - ну... це грати \n" +
                "/top_players@Test_OfOPer22bot - вивести топ гравцiв\n" +
                "/help@Test_OfOPer22bot - список команд\n" +
                "/bribe@Test_OfOPer22bot - дати взятку щоб грати далi\n" +
                "(10 вiд ТРО; 5 щоб грати ще раз)";
        return new SendMessage(chat.id(),text).replyToMessageId(update.message().messageId());
    }

    @BotRequest(value = "/start_playing@Test_OfOPer22bot", type = {MessageType.CALLBACK_QUERY, MessageType.MESSAGE})
    public BaseRequest startGame(User user, Chat chat, Update update) {
        return smokeGame.startGame(user,chat).replyToMessageId(update.message().messageId());
    }

    @BotRequest(value = "/play@Test_OfOPer22bot", type = {MessageType.CALLBACK_QUERY, MessageType.MESSAGE})
    public BaseRequest smokeSomeWeed(User user, Chat chat, Update update) {
        return smokeGame.playGame(user, chat).replyToMessageId(update.message().messageId());
    }

    @BotRequest(value = "/bribe@Test_OfOPer22bot", type = {MessageType.CALLBACK_QUERY, MessageType.MESSAGE})
    public BaseRequest giveBribe(User user, Chat chat, Update update) {
        return smokeGame.giveBribe(user, chat.id()).replyToMessageId(update.message().messageId());
    }

    @BotRequest(value = "/top_players@Test_OfOPer22bot", type = {MessageType.CALLBACK_QUERY, MessageType.MESSAGE})
    public BaseRequest top(Chat chat, Update update) {
        return smokeGame.topGamers(chat).replyToMessageId(update.message().messageId());
    }

    @MessageRequest("/memory")
    public String getMemoryUsage() {
        return "Meg used="+(Runtime.getRuntime().totalMemory()-
                Runtime.getRuntime().freeMemory())/(1000*1000)+"M";
    }

    @Value("${telegram.bot.token}")
    private String token;
    @Override
    public String getToken() {
        return token;
    }
}
