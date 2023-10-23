package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.entity.BettingPlayer;
import org.example.repository.BettingRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BettingService {

    private final BettingRepository bettingRepository;
    private final LocalTime lc;

    public String start(BettingPlayer bettingPlayer) {
        var player = bettingRepository.save(bettingPlayer);

        return String.valueOf(lc);
    }

    public String sorted() {
        List<BettingPlayer> playerAll = bettingRepository.findAll();

        Random random = new Random();

        int randomIndex = random.nextInt(playerAll.size());

        BettingPlayer randomPlayer = playerAll.get(randomIndex);

        String randomNumber = randomPlayer.getNumber().toString();

        int valueFinal = valueReturnBetting(randomPlayer.getBet_value(), playerAll);

        bettingRepository.deleteAll();

        return "The number sorted is: " + randomNumber +
                " " +
                "The value was: " + String.valueOf(valueFinal);
    }

    private Integer valueReturnBetting(Integer value, List<BettingPlayer> playerAll) {
        Integer totalBetAmount = playerAll.stream()
                .mapToInt(player -> player.getBet_value())
                .sum();

        Integer valuePlayers = (int) (totalBetAmount - (totalBetAmount * 0.3));

        Integer valueOfProfit = (int) (totalBetAmount * 0.3);

        int multipli = valuePlayers / value;

        int valueFinalPlayer = value * multipli;

        // -----------------------

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Long> response = restTemplate.postForEntity("http://localhost:8987/api/profit/save", valueOfProfit, Long.class);

        return valueFinalPlayer;
    }
}
