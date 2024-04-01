package com.casino.CasinoProject.service;

import com.casino.CasinoProject.dto.PlayerBet;
import com.casino.CasinoProject.entity.Player;
import com.casino.CasinoProject.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    private final WebClient webClient;


    private PlayerService (PlayerRepository playerRepository, WebClient.Builder webClientBuilder) {
        this.playerRepository = playerRepository;
        this.webClient = webClientBuilder.baseUrl("http://localhost:8987").build();
    }

    public String saveNewUser(Player playerNew) {
        if (playerNew == null) {

            throw new IllegalArgumentException("The user does not meet the requirements");

        }

        var user = playerRepository.save(playerNew);

        return "Player creaetd Sucess!";
    }

    public Mono<Boolean> verifyCpf(PlayerBet userplay) {
        return playerRepository.findByCpf(userplay.getCpf())
                .map(player -> {
                    return true;
                })
                .defaultIfEmpty(false);
    }

}
