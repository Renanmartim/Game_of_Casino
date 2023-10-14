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

    @GetMapping
    public ResponseEntity<Long> takeValue(){
        var value = profitService.profitValue();
        return ResponseEntity.ok().body(value);

    }

    @PostMapping("/save")
    public ResponseEntity<String> saveProfit(@RequestBody Profit profit){
        var saveNewProfit = profitService.saveProfit(profit);
        return ResponseEntity.ok().body(saveNewProfit);
    }

    @GetMapping("/required/{value}")
    public ResponseEntity<String> requiredProfit(@PathVariable Long value){
        var requiredValue = profitService.requireProfit(value);
        return ResponseEntity.ok().body(requiredValue);
    }
}
