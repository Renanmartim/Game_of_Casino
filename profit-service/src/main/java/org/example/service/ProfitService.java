package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.controller.ProfitController;
import org.example.entity.Profit;
import org.example.repository.ProfitRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfitService {

    private final ProfitRepository profitRepository;
    public String saveProfit(Profit profit) {
        var saveProfit = profitRepository.save(profit);
        return "Profit was saved!";
    }

    public Long requireProfit(Long profitRequired) {
        Long value = profitRepository.getProfit();

        if (profitRequired > value){
            throw new IllegalArgumentException("This value is incorrect!");
        }
        Long valueActual = profitRequired - value;

        var profit = profitRepository.getObject();

        profit.setProfit_casino(valueActual);

        profitRepository.save(profit);

        return profitRequired;

    }
}
