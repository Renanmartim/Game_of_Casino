package com.casino.CasinoProject.entity;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Player {

    @Id
    private String id;

    @NotNull
    @UniqueElements
    private BigDecimal cpf;

    @NotNull
    @Size(min = 10)
    private String name_full;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 6)
    private String senha;

}
