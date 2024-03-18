package org.example.service;

import org.example.entity.BettingPlayer;
import org.example.repository.BettingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

@Service
public class BettingService {

    private final BettingRepository bettingRepository;

    private BettingService (BettingRepository bettingRepository) {
        this.bettingRepository = bettingRepository;
    }

    public Mono<String> start(BettingPlayer bettingPlayer) {
        return bettingRepository.findByNumber(bettingPlayer.getNumber())
                .flatMap(existingPlayer -> Mono.just("Error: Player with number " + bettingPlayer.getNumber() + " already exists."))
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

                    bettingRepository.deleteAll();

                    return Mono.just("The number sorted is: " + randomNumber + ". The value was: " + String.valueOf(valueFinal));
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
}
