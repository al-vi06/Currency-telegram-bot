package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.entity.Subscribers;
import com.skillbox.cryptobot.repository.SubsctiberRepository;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Обработка команды отмены подписки на курс валюты
 */
@Service
@Slf4j
@AllArgsConstructor
public class UnsubscribeCommand extends AbstractCommand implements IBotCommand {

    private final SubsctiberRepository subsctiberRepository;

    @Override
    public String getCommandIdentifier() {
        return "unsubscribe";
    }

    @Override
    public String getDescription() {
        return "Отменяет подписку пользователя";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        Long telegramId = message.getFrom().getId();

        Subscribers subscriber = subsctiberRepository
                .findByTelegramId(telegramId)
                .orElseThrow();

        if(subscriber.getTargetPrice()==null) {
            sendMessage(absSender, message.getChatId(),
                    "Активные подписки отсутствуют");
            return;
        }

        subscriber.setTargetPrice(null);
        subsctiberRepository.save(subscriber);

        sendMessage(absSender, message.getChatId(),
                "Подписка отменена");


    }
}