package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.entity.BettingPlayer;
import org.example.service.BettingService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/betting")
@RequiredArgsConstructor
public class BettingController {

    private final BettingService bettingService;
    private boolean isDrawing = false;

    @PostMapping("/start")
    @Async
    public Mono<String> startGame(@RequestBody BettingPlayer bettingPlayer) {
        if (!isDrawing) {

            bettingService.start(bettingPlayer);

            var startTime = bettingService.getDate();
            isDrawing = true;

            Duration delayDuration = Duration.between(startTime, startTime.plusMinutes(5));

            return Mono.delay(delayDuration)
                    .flatMap(i -> sorted())
                    .doOnTerminate(() -> isDrawing = false)
                    .map(result -> "Drawing completed. Result: " + result);
        } else {
            return Mono.just("Drawing already in progress.");
        }
    }

    @Async
    public Mono<String> sorted(){
        return Mono.just(bettingService.sorted());
    }
}
