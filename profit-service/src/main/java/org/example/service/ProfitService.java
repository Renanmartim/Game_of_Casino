package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.Profit;
import org.example.repository.ProfitRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfitService {

    private final ProfitRepository profitRepository;

    public String saveProfit(Profit profit) {
        Profit profitBd = profitRepository.getObject();

        Long currentProfit = profitBd.getProfit_casino();
        Long newProfit = currentProfit + profit.getProfit_casino();

        profitBd.setProfit_casino(newProfit);
        profitBd.setTime(LocalDateTime.now());

        profitRepository.save(profitBd);

        return "Profit was saved!";
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
}
