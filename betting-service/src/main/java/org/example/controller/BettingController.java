package org.example.controller;
import org.example.entity.BettingPlayer;
import org.example.service.BettingService;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/betting")
public class BettingController {

    private final BettingService bettingService;

    public BettingController(BettingService bettingService) {
        this.bettingService = bettingService;
    }

    @PostMapping("/start")
    public Flux<ServerSentEvent<String>> startGame(@RequestBody BettingPlayer bettingPlayer) {
        return bettingService.startGameLogic(bettingPlayer);
    }
}
