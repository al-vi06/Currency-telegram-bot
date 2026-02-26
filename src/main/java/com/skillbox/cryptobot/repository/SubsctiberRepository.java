package com.skillbox.cryptobot.repository;

import com.skillbox.cryptobot.entity.Subscribers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubsctiberRepository extends JpaRepository<Subscribers, UUID> {
    Optional<Subscribers> findByTelegramId(Long telegramId);

    List<Subscribers> findAllByTargetPriceIsNotNull();
}
