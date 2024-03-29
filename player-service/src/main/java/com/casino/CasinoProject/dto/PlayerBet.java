package com.casino.CasinoProject.dto;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

public class PlayerBet {

    private BigDecimal cpf;

    public BigDecimal getCpf() {
        return cpf;
    }

    public void setCpf(BigDecimal cpf) {
        this.cpf = cpf;
    }


    public PlayerBet(BigDecimal cpf) {
        this.cpf = cpf;

    }

    public PlayerBet() {
    }
}
