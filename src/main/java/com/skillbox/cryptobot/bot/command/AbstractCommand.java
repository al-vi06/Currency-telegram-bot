package com.skillbox.cryptobot.bot.command;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public abstract class AbstractCommand {

    protected void sendMessage(AbsSender absSender, Long chatId, String text) {
        //!!!??? зачем тут protected
        try {
            absSender.execute(new SendMessage(chatId.toString(), text));
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

}
