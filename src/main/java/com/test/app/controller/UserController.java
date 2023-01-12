package com.test.app.controller;

import com.test.app.dto.User;
import com.test.app.repository.UserRepository;
import com.test.app.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final Logger log = LoggerFactory.getLogger(UserController.class);


    public UserController(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }


    @GetMapping("/token")
    public ResponseEntity<String> login() {
        String token = tokenService.generateToken("UserName");
        return ResponseEntity.ok(token);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getUser(@PathVariable("id") Integer id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound()
                        .build());
    }

    @GetMapping
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public Mono<User> createUser(@RequestBody User user) {
        log.info(user.toString());

        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> updateUser(@PathVariable("id") Integer id, @RequestBody User user) {
        return userRepository.findById(id)
                .flatMap(existingUser -> {
                    existingUser.setName(user.getName());
                    existingUser.setAge(user.getAge());
                    return userRepository.save(existingUser);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound()
                        .build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable("id") Integer id) {
        return userRepository.findById(id)
                .flatMap(existingUser ->
                        userRepository.delete(id)
                                .then(Mono.just(ResponseEntity.ok()
                                        .<Void>build()))
                )
                .defaultIfEmpty(ResponseEntity.notFound()
                        .build());
    }
}
