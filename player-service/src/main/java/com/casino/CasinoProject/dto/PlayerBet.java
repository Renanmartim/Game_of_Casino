package com.casino.CasinoProject.dto;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

public class PlayerBet {

    private BigDecimal cpf;
    private BigDecimal bet_value;
    private Long number;

    public BigDecimal getCpf() {
        return cpf;
    }

    public void setCpf(BigDecimal cpf) {
        this.cpf = cpf;
    }

    public BigDecimal getBet_value() {
        return bet_value;
    }

    public void setBet_value(BigDecimal bet_value) {
        this.bet_value = bet_value;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public PlayerBet(BigDecimal cpf, BigDecimal bet_value, Long number) {
        this.cpf = cpf;
        this.bet_value = bet_value;
        this.number = number;
    }

    public PlayerBet() {
    }
}
