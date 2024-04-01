package org.example.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
public class BettingPlayer {

    @Id
    private String id;

    private BigDecimal cpf;
    private Integer bet_value;
    private Long number;

    public String getId() {
        return id;
    }

    public BigDecimal getCpf() {
        return cpf;
    }

    public void setCpf(BigDecimal cpf) {
        this.cpf = cpf;
    }

    public Integer getBet_value() {
        return bet_value;
    }

    public void setBet_value(Integer bet_value) {
        this.bet_value = bet_value;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }
}
