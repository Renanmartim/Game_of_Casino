package org.example.dto;

public class Result {

    private Long id;
    private Long number;
    private Long value;

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Long getValue() {
        return value;
    }

    public Long getId() {
        return id;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Result(Long number, Long value) {
        this.number = number;
        this.value = value;
    }

    public Result() {
    }

    @Override
    public String toString() {
        return "Result{" +
                "id='" + id + '\'' +
                ", number=" + number +
                ", value=" + value +
                '}';
    }
}
