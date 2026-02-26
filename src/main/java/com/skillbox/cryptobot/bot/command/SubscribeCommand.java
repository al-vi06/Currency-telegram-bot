package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.entity.Subscribers;
import com.skillbox.cryptobot.repository.SubsctiberRepository;
import com.skillbox.cryptobot.service.CryptoCurrencyService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SubscribeCommand extends AbstractCommand implements IBotCommand {

    private final CryptoCurrencyService service;
    private final SubsctiberRepository subsctiberRepository;
    private static final Pattern PRICE_PATTERN = Pattern.compile("^[0-9]+([.,][0-9]+)?$"); //^\d+(\.\d+)?$

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        Long telegramId = message.getFrom().getId();

        if (arguments.length == 0 || !PRICE_PATTERN.matcher(arguments[0]).matches()) {
            sendMessage(absSender, message.getChatId(),
                    "Введите корректную цену. Пример: /subscribe 35000");
            return;
        }

        String normalized = arguments[0].replace(",", ".");
        double targetPrice = Double.parseDouble(normalized);

        Subscribers subscriber = subsctiberRepository
                .findByTelegramId(telegramId)
                .orElseThrow();

        subscriber.setTargetPrice(targetPrice);
        subsctiberRepository.save(subscriber);

        double currentPrice = 0;
        try {
            currentPrice = service.getBitcoinPrice();
        } catch (IOException e) {
            log.error("Ошибка получения цены", e);
            sendMessage(absSender, message.getChatId(),
                    "Ошибка получения текущей цены");
        }

        sendMessage(absSender, message.getChatId(),
                "Текущая цена биткоина " + TextUtil.toString(currentPrice) + " USD");

        sendMessage(absSender, message.getChatId(),
                "Новая подписка создана на стоимость "
                        + TextUtil.toString(targetPrice));


    }
}