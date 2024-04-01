package org.example.dto;

import java.math.BigDecimal;

public class VerifyCpf {

    private BigDecimal cpf;

    public BigDecimal getCpf() {
        return cpf;
    }

    public void setCpf(BigDecimal cpf) {
        this.cpf = cpf;
    }


    public VerifyCpf(BigDecimal cpf) {
        this.cpf = cpf;
    }



}
