package com.example.dugarolo;

public class Weir {
    private Integer number;
    private String farm;
    private String waterLevel;

    public Weir(Integer number, String farm, String waterLevel) {
        this.farm = farm;
        this.number = number;
        this.waterLevel = waterLevel;
    }

    public String getFarm() {
        return farm;
    }

    public Integer getNumber() {
        return number;
    }

    public String getWaterLevel() {
        return waterLevel;
    }
}
