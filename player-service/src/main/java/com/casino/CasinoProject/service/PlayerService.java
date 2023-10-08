package com.casino.CasinoProject.service;

import com.casino.CasinoProject.dto.PlayerBet;
import com.casino.CasinoProject.entity.Player;
import com.casino.CasinoProject.repository.PlayerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    private final RestTemplate restTemplate;

    public String saveNewUser(Player playerNew) {
        if (playerNew == null) {

            throw new IllegalArgumentException("The user does not meet the requirements");

        }

        var user = playerRepository.save(playerNew);

        return "Player creaetd Sucess!";
    }

    public String playLogic(PlayerBet userplay) throws JsonProcessingException {
        var existPlayer = playerRepository.findByCpf(userplay.getCpf());
        if (existPlayer.isPresent()) {

            ObjectMapper mapper = new ObjectMapper();

            String json = mapper.writeValueAsString(userplay);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

            try {
                ResponseEntity<String> respostaServer = restTemplate.exchange(
                        "http://localhost:8987/api/betting/start",
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );
                System.out.println("Response: " + respostaServer.getBody());
            } catch (HttpClientErrorException e) {
                HttpStatus statusCode = (HttpStatus) e.getStatusCode();
                String responseBody = e.getResponseBodyAsString();
                System.err.println("HTTP Error: " + statusCode);
                System.err.println("Error Response: " + responseBody);
            }

            return "Your proposal is received!";
        }
        return "Your proposal is NOT received!";
    }

    public List<Player> findAllPlayers() {
        return playerRepository.findAll();
    }
}
