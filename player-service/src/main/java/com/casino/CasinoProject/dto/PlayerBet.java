package com.casino.CasinoProject.dto;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlayerBet {

    private BigDecimal cpf;
    private BigDecimal bet_value;
    private Long number;

}
