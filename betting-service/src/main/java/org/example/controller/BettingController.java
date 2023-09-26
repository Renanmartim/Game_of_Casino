package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.entity.BettingPlayer;
import org.example.service.BettingService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/betting")
@RequiredArgsConstructor
public class BettingController {

    private final BettingService bettingService;

    @PostMapping("/start")
    @Async
    public String startGame(@RequestBody BettingPlayer bettingPlayer) {
        return bettingService.start(bettingPlayer);
    }

    @PostMapping("/sorted")
    public String sorted(){
        return bettingService.sorted();
    }
}
