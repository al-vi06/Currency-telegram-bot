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

@Service
@Slf4j
@AllArgsConstructor
public class GetSubscriptionCommand extends AbstractCommand implements IBotCommand {
    private final SubsctiberRepository subsctiberRepository;

    @Override
    public String getCommandIdentifier() {
        return "get_subscription";
    }

    @Override
    public String getDescription() {
        return "Возвращает текущую подписку";
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
        } else {
            sendMessage(absSender, message.getChatId(),
                    "Вы подписаны на стоимость биткоина "
                            + TextUtil.toString(subscriber.getTargetPrice()) + " USD");
        }

    }
}