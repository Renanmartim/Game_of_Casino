package com.casino.CasinoProject.controller;

import com.casino.CasinoProject.dto.PlayerBet;
import com.casino.CasinoProject.entity.Player;
import com.casino.CasinoProject.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/casino")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping()
    public ResponseEntity<String> saveNewUser(@RequestBody Player playerNew) {
        var UserNew = playerService.saveNewUser(playerNew);
        return ResponseEntity.ok().body(UserNew);
    }

    @PostMapping("/play")
    public ResponseEntity<String> playGame(@RequestBody PlayerBet Userplay) throws JsonProcessingException {
        var PlayUser = playerService.playLogic(Userplay);
        return ResponseEntity.ok().body(PlayUser);
    }

    @GetMapping
    public ResponseEntity<List<Player>> allPlayers(){
        var player = playerService.findAllPlayers();
        return ResponseEntity.ok().body(player);
    }


}
