package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.entity.Profit;
import org.example.service.ProfitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profit")
public class ProfitController {

    private final ProfitService profitService;

    private ProfitController (ProfitService profitService) {
        this.profitService = profitService;
    }

    @GetMapping
    public ResponseEntity<Long> takeValue(){
        var value = profitService.profitValue();
        return ResponseEntity.ok().body(value);

    }

    @PostMapping("/save")
    public Void saveProfit(@RequestBody Long profit){
        var saveNewProfit = profitService.saveProfit(profit);
        return null;
    }

    @GetMapping("/required/{value}")
    public ResponseEntity<String> requiredProfit(@PathVariable Long value){
        var requiredValue = profitService.requireProfit(value);
        return ResponseEntity.ok().body(requiredValue);
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(Profit profit){
        return profitService.create(profit);
    }
}
