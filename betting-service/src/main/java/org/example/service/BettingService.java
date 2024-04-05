package org.example.service;

import org.example.dto.Result;
import org.example.dto.VerifyCpf;
import org.example.entity.BettingPlayer;
import org.example.exception.CpfNotRegistredException;
import org.example.repository.BettingRepository;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class BettingService {

    private final BettingRepository bettingRepository;

    private final RestTemplate restTemplate;

    private volatile Mono<String> sortedNumber;

    private final AtomicBoolean drawLock = new AtomicBoolean(false);
    private final MonoProcessor<Instant> firstRequestTimeProcessor = MonoProcessor.create();
    private final MonoProcessor<String> resultProcessor = MonoProcessor.create();

    private final ConcurrentHashMap<String, String> resultStore = new ConcurrentHashMap<>();


    private BettingService (BettingRepository bettingRepository, RestTemplate restTemplate) {
        this.bettingRepository = bettingRepository;
        this.restTemplate = restTemplate;
    }

    public Mono<String> start(BettingPlayer bettingPlayer) {
        return bettingRepository.findByNumber(bettingPlayer.getNumber())
                .flatMap(existingPlayer -> Mono.just("Error: Player with number " + bettingPlayer.getNumber() + " already exists."))
                .switchIfEmpty(checkCpfExistence(bettingPlayer));
    }

    private Mono<String> checkCpfExistence(BettingPlayer bettingPlayer) {
        return bettingRepository.findByCpf(bettingPlayer.getCpf())
                .flatMap(existingPlayer -> Mono.just("Error: Player with CPF " + bettingPlayer.getCpf() + " already exists."))
                .switchIfEmpty(saveNewPlayer(bettingPlayer));
    }

    private Mono<String> saveNewPlayer(BettingPlayer bettingPlayer) {
        return bettingRepository.save(bettingPlayer)
                .thenReturn("Player with number " + bettingPlayer.getNumber() + " saved!");
    }

    public Mono<String> sorted() {
        return bettingRepository.findAll()
                .collectList()
                .flatMap(playerAll -> {
                    if (playerAll == null || playerAll.isEmpty()) {
                        return Mono.just("No players found.");
                    }

                    Random random = new Random();
                    int randomIndex = random.nextInt(playerAll.size());
                    BettingPlayer randomPlayer = playerAll.get(randomIndex);
                    String randomNumber = randomPlayer.getNumber().toString();

                    int valueFinal = valueReturnBetting(randomPlayer.getBet_value(), playerAll);

                    System.out.println("The number sorted is: " + randomNumber + ". The value was: " + String.valueOf(valueFinal));

                    return bettingRepository.deleteAll()
                            .then(Mono.just("The number sorted is: " + randomNumber +". The value was: " + String.valueOf(valueFinal)));
                });
    }



    private Integer valueReturnBetting(Integer value, List<BettingPlayer> playerAll) {
        int totalBetAmount = playerAll.stream()
                .mapToInt(BettingPlayer::getBet_value)
                .sum();

        Integer valuePlayers = (int) (totalBetAmount - (totalBetAmount * 0.3));

        //Integer valueOfProfit = (int) (totalBetAmount * 0.3);

        //int multipli = valuePlayers / value;

        //restTemplate = new RestTemplate();
        //ResponseEntity<Long> response = restTemplate.postForEntity("http://localhost:8987/api/profit/save", valueOfProfit, Long.class);

        return valuePlayers;
    }

    public synchronized Mono<String> drawNumber() {
        if (sortedNumber == null) {
            System.out.println(sortedNumber);
            sortedNumber = sorted();
        }
        return sortedNumber;
    }

    public Flux<ServerSentEvent<String>> startGameLogic(BettingPlayer bettingPlayer) {
        System.out.println("Bet player received: " + bettingPlayer);

        String url = "http://localhost:8987/api/casino/play";

        var cpf = bettingPlayer.getCpf();

        final Map<Long, String> playerStatusCache = new ConcurrentHashMap<>();

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
                                    return start(bettingPlayer)
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

    private void initializeFirstRequestTime() {
        System.out.println("Initializing first request time...");
        Instant now = Instant.now();
        firstRequestTimeProcessor.onNext(now);
    }
}
