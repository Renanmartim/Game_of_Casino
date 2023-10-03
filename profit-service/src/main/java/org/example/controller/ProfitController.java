package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.entity.Profit;
import org.example.service.ProfitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profit")
@RequiredArgsConstructor
public class ProfitController {

    private final ProfitService profitService;

    @PostMapping
    public ResponseEntity<String> saveProfit(@RequestBody Profit profit){
        var saveNewProfit = profitService.saveProfit(profit);
        return ResponseEntity.ok().body(saveNewProfit);
    }

    @PostMapping
    public ResponseEntity<Long> requiredProfit(@RequestBody Long profitRequired){
        var requiredValue = profitService.requireProfit(profitRequired);
        return ResponseEntity.ok().body(requiredValue);
    }


}
