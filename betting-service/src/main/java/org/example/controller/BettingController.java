package org.example.controller;

import org.example.dto.VerifyCpf;
import org.example.entity.BettingPlayer;
import org.example.exception.CpfNotRegistredException;
import org.example.service.BettingService;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static reactor.core.publisher.Signal.subscribe;

@RestController
@RequestMapping("/api/betting")
public class BettingController {

    private final RestTemplate restTemplate;

    private final BettingService bettingService;
    private volatile Mono<String> sortedNumber;
    private final Map<Long, String> playerStatusCache = new ConcurrentHashMap<>();
    private final MonoProcessor<Instant> firstRequestTimeProcessor = MonoProcessor.create();
    private final MonoProcessor<String> resultProcessor = MonoProcessor.create();
    private final AtomicBoolean drawLock = new AtomicBoolean(false);

    private final RedisOperations<Object, Object> redisOperations;


    public BettingController(BettingService bettingService, RedisOperations<Object, Object> redisOperations,RestTemplate restTemplate) {
        this.bettingService = bettingService;
        this.redisOperations = redisOperations;
        this.restTemplate = restTemplate;
    }

    private final Object drawLockObject = new Object();

    public synchronized Mono<String> drawNumber() {
        if (sortedNumber == null) {
            System.out.println(sortedNumber);
            sortedNumber = bettingService.sorted();
        }
        return sortedNumber;
    }

    private final ConcurrentHashMap<String, String> resultStore = new ConcurrentHashMap<>();


    @PostMapping("/start")
    public Flux<ServerSentEvent<String>> startGame(@RequestBody BettingPlayer bettingPlayer) {
        System.out.println("Bet player received: " + bettingPlayer);

        String url = "http://localhost:8987/api/casino/play";

        var cpf = bettingPlayer.getCpf();

        var verify = new VerifyCpf(cpf);

        Boolean search = restTemplate.postForObject(url, verify, Boolean.class);

        if (search == Boolean.FALSE){
            throw new CpfNotRegistredException("CPF not registered!");
        }

        System.out.println("Result: " + search);

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


            MonoProcessor<Void> gameEndedProcessor = MonoProcessor.create(); // Signal game end

            Flux.interval(Duration.ofSeconds(10))
                    .takeUntilOther(gameEndedProcessor) // Terminate when gameEndedProcessor signals
                    .flatMap(tick -> {
                        Instant currentTime = Instant.now();
                        Duration elapsedTime = Duration.between(startTime, currentTime);
                        Duration remainingTime = Duration.ofMinutes(1).minus(elapsedTime);
                        System.out.println("Elapsed time: " + elapsedTime);
                        System.out.println("Remaining time: " + remainingTime);

                        synchronized (drawLock) {
                            if (gameEndedProcessor.isTerminated()) {
                                System.out.println("AQUI 2 _>>>");
                                sink.complete(); // Stop sending events to the client
                                return Mono.empty();
                            }
                            if (remainingTime.isNegative() || remainingTime.isZero()) {
                                if (resultStore.containsKey("sorted-number")) {
                                    // A stored result was found
                                    sink.next(ServerSentEvent.<String>builder()
                                            .event("result")
                                            .data("Final result: " + resultStore.get("sorted-number"))
                                            .build());
                                    sink.complete();

                                    gameEndedProcessor.onComplete();
                                    return Mono.empty();
                                } else {
                                    // No stored result was found
                                    return drawNumber()
                                            .flatMap(result -> {
                                                resultStore.put("sorted-number", result);
                                                sink.next(ServerSentEvent.<String>builder()
                                                        .event("result")
                                                        .data("Final result: " + result)
                                                        .build());
                                                sink.complete();

                                                gameEndedProcessor.onComplete();

                                                return Mono.empty();
                                            });
                                }
                            }else {
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

    private AtomicInteger restarts = new AtomicInteger(0);

    private void restartServer() {
        System.out.println("Server restarted. Restarts count: " + restarts.get());
    }
}
