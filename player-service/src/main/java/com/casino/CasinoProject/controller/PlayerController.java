package com.casino.CasinoProject.controller;

import com.casino.CasinoProject.dto.PlayerBet;
import com.casino.CasinoProject.entity.Player;
import com.casino.CasinoProject.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;


@RestController
@RequestMapping("/api/casino")
public class PlayerController {

    private final PlayerService playerService;

    private PlayerController (PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping()
    public ResponseEntity<String> saveNewUser(@RequestBody @Valid Player playerNew) {
        var UserNew = playerService.saveNewUser(playerNew);
        return ResponseEntity.ok().body(UserNew);
    }

    @PostMapping("/play")
    public ResponseEntity<Mono<Boolean>> playGame(@RequestBody PlayerBet Userplay) throws JsonProcessingException {
        var PlayUser = playerService.verifyCpf(Userplay);
        return ResponseEntity.ok().body(PlayUser);
    }



}
