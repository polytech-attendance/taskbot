package ru.spbstu.ai.service;

import reactor.core.publisher.Mono;
import ru.spbstu.ai.entity.User;

public interface UserService {
    Mono<User> create(long telegramId);
    Mono<User> getUser(long telegramId);
}
