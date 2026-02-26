package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.bot.CryptoBot;
import com.skillbox.cryptobot.entity.Subscribers;
import com.skillbox.cryptobot.repository.SubsctiberRepository;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final SubsctiberRepository subsctiberRepository;
    private final CryptoCurrencyService currencyService;
    private final CryptoBot bot;

    @Value("${telegram.bot.notify.delay.value}")
    private long notifyDelay;

    @Scheduled(fixedDelayString = "${price.update.interval:120000}")
    public void checkPrice() throws IOException {

        double currentPrice = currencyService.getBitcoinPrice();

        subsctiberRepository.findAllByTargetPriceIsNotNull()
                .stream()
                .filter(sub -> sub.getTargetPrice() > currentPrice)
                .filter(this::canNotify)
                .forEach(sub -> notify(sub, currentPrice));
    }

    private boolean canNotify(Subscribers sub) {
        if (sub.getLastNotificationTime() == null) {
            return true;
        }
        return sub.getLastNotificationTime()
                .plusMinutes(notifyDelay)
                .isBefore(LocalDateTime.now());
    }

    private void notify(Subscribers sub, double price) {
        sendMessage(sub.getTelegramId(),
                "Пора покупать, стоимость биткоина "
                        + TextUtil.toString(price));

        sub.setLastNotificationTime(LocalDateTime.now());
        subsctiberRepository.save(sub);
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки уведомления", e);
        }
    }


}
