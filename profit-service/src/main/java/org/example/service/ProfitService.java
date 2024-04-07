package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Profit;
import org.example.repository.ProfitRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProfitService {

    private final ProfitRepository profitRepository;

    private ProfitService (ProfitRepository profitRepository) {
        this.profitRepository = profitRepository;
    }

    public Void saveProfit(Long profit) {
        Profit profitBd = profitRepository.getObject();

        Long currentProfit = profitBd.getProfit_casino();
        Long newProfit = currentProfit + profit;

        profitBd.setProfit_casino(newProfit);
        profitBd.setTime(LocalDateTime.now());

        var save = profitRepository.save(profitBd);

        System.out.println(save);
        return null;
    }

    public String requireProfit(Long profitRequired) {
        Profit profit = profitRepository.getObject();

        Long currentProfit = profit.getProfit_casino();

        if (profitRequired > currentProfit) {
            return ("Required profit is incorrect!");
        }
        else {

            Long profitDifference = currentProfit - profitRequired;

            profit.setProfit_casino(profitDifference);

            profitRepository.save(profit);

            return "Required was ok!";
        }
    }

    public Long profitValue() {
        return profitRepository.getObject().getProfit_casino();
    }

    public ResponseEntity<String> create(Profit profit) {
        var profitNew = new Profit(100L, LocalDateTime.now());
        var save = profitRepository.save(profitNew);
        System.out.println(save);
        return ResponseEntity.ok().body("Ok");
    }
}
