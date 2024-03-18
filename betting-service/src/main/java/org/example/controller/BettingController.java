package org.example.controller;

import org.example.entity.BettingPlayer;
import org.example.service.BettingService;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static reactor.core.publisher.Signal.subscribe;

@RestController
@RequestMapping("/api/betting")
public class BettingController {

    private final BettingService bettingService;
    private Mono<String> sortedNumber;
    private final Map<Long, String> playerStatusCache = new ConcurrentHashMap<>();
    private final MonoProcessor<Instant> firstRequestTimeProcessor = MonoProcessor.create();
    private final MonoProcessor<String> resultProcessor = MonoProcessor.create();

    public BettingController(BettingService bettingService) {
        this.bettingService = bettingService;
    }

    @PostMapping("/start")
    public Flux<ServerSentEvent<String>> startGame(@RequestBody BettingPlayer bettingPlayer) {
        System.out.println("Bet player received: " + bettingPlayer);

        return Flux.create(sink -> {
            initializeFirstRequestTime();
            Instant startTime = firstRequestTimeProcessor.peek();
            if (startTime == null) {
                sink.next(ServerSentEvent.<String>builder()
                        .event("error")
                        .data("The game has not started yet")
                        .build());
                sink.complete();
                return;
            }

            AtomicBoolean gameEnded = new AtomicBoolean(false); // Flag to indicate game end

            Flux.interval(Duration.ofSeconds(10))
                    .takeUntil(tick -> gameEnded.get()) // Terminate when gameEnded is true
                    .flatMap(tick -> {
                        Instant currentTime = Instant.now();
                        Duration elapsedTime = Duration.between(startTime, currentTime);
                        Duration remainingTime = Duration.ofMinutes(1).minus(elapsedTime);
                        System.out.println("Elapsed time: " + elapsedTime);
                        System.out.println("Remaining time: " + remainingTime);

                        if (remainingTime.isNegative() || remainingTime.isZero()) {
                            gameEnded.set(true); // Set flag to true to end the game
                            return bettingService.sorted()
                                    .map(result -> ServerSentEvent.<String>builder()
                                            .event("result")
                                            .data("Final result: " + result)
                                            .build())
                                    .flatMap(event -> {
                                        sink.next(event);
                                        sink.complete();
                                        return Mono.empty();
                                    });
                        } else {
                            if (playerStatusCache.containsKey(bettingPlayer.getNumber())) {
                                return Mono.just(ServerSentEvent.<String>builder()
                                        .event("update")
                                        .data(playerStatusCache.get(bettingPlayer.getNumber()) + " Wait " + remainingTime.toMinutes() + " minutes.")
                                        .build());
                            } else {
                                return bettingService.start(bettingPlayer)
                                        .flatMap(playerStatus -> {
                                            if (playerStatus.startsWith("Error: ")) {
                                                return Mono.just(ServerSentEvent.<String>builder()
                                                        .event("error")
                                                        .data(playerStatus)
                                                        .build());
                                            } else {
                                                playerStatusCache.put(bettingPlayer.getNumber(), playerStatus);
                                                return Mono.just(ServerSentEvent.<String>builder()
                                                        .event("update")
                                                        .data(playerStatus + " Please wait " + remainingTime.toMinutes() + " minutes.")
                                                        .build());
                                            }
                                        });
                            }
                        }
                    })
                    .subscribe(sink::next, sink::error, sink::complete);
        });
    }



    @GetMapping("/result")
    public String getResult() {
        return resultProcessor.name();
    }

    private void initializeFirstRequestTime() {
        System.out.println("Initializing first request time...");
        Instant now = Instant.now();
        firstRequestTimeProcessor.onNext(now);
    }
}
