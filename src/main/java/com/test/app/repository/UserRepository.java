package com.test.app.repository;

import com.test.app.dto.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserRepository {
    private final Map<Integer, User> userMap = new ConcurrentHashMap<>();


    @PostConstruct
    public void init() {
        userMap.put(1, new User(1, "He-Man", 25, "1234567890123456", "RogersPass1", "01/01/1995"));
        userMap.put(2, new User(2, "Ant-Man", 30, "2345678901234567", "RogersPass123", "02/01/1990"));
        userMap.put(3, new User(3, "Super-Man", 35, "3456789012345678", "RogersPass125", "03/01/1985"));
        userMap.put(4, new User(4, "Happy-Man", 40, "4567890123456789", "RogersPass154", "04/01/1980"));
        userMap.put(5, new User(5, "Common-Man", 45, "5678901234567890", "RogersPass1864", "05/01/1975"));

    }

    public Mono<User> save(User user) {
        userMap.put(user.getId(), user);
        return Mono.just(user);
    }

    public Mono<User> findById(Integer id) {
        return Mono.justOrEmpty(userMap.get(id));
    }

    public Mono<Void> delete(Integer id) {
        return Mono.fromRunnable(() -> userMap.remove(id));
    }

    public Mono<User> update(User user) {
        return Mono.just(Objects.requireNonNull(userMap.put(user.getId(), user)))
                .map(val -> user);
    }

    public Flux<User> findAll() {
        return Flux.fromIterable(userMap.values());
    }
}
