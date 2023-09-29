package com.casino.CasinoProject.service;

import com.casino.CasinoProject.dto.PlayerBet;
import com.casino.CasinoProject.entity.Player;
import com.casino.CasinoProject.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    public String saveNewUser(Player playerNew) {
        if (playerNew == null) {

            throw new IllegalArgumentException("The user does not meet the requirements");

        }

        var user = playerRepository.save(playerNew);

        return "Player creaetd Sucess!";
    }

    public String playLogic(PlayerBet userplay) {
        var existPlayer = playerRepository.findByCpf(userplay.getCpf());
        if (existPlayer.isPresent()) {
            return "Your proposal is received!";
        }
        return "Your proposal is NOT received!";
    }

    public List<Player> findAllPlayers() {
        return playerRepository.findAll();
    }
}
