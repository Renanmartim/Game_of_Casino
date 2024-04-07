package org.example.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("profit_casino")
public class Profit {

    @Id
    private String id;

    private Long profit_casino;

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

    public Profit(Long profit_casino, LocalDateTime time) {
        this.profit_casino = profit_casino;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Profit{" +
                "id='" + id + '\'' +
                ", profit_casino=" + profit_casino +
                ", time=" + time +
                '}';
    }
}
