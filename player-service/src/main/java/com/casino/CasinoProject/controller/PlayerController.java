package com.casino.CasinoProject.controller;

import com.casino.CasinoProject.dto.PlayerBet;
import com.casino.CasinoProject.entity.Player;
import com.casino.CasinoProject.service.PlayerService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
    public ResponseEntity<String> playGame(@RequestBody PlayerBet Userplay) {
        var PlayUser = playerService.playLogic(Userplay);
        return ResponseEntity.ok().body(PlayUser);
    }


}
