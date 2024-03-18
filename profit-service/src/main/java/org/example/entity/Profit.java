package org.example.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profit {

    @Id
    private String id;

    private Long profit_casino;

    @JsonIgnore
    private LocalDateTime time;

    public String getId() {
        return id;
    }


    public Long getProfit_casino() {
        return profit_casino;
    }

    public void setProfit_casino(Long profit_casino) {
        this.profit_casino = profit_casino;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
