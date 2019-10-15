package com.example.dugarolo;

public class Farm {
    private String name;

    public static final Farm[] farms = {
            new Farm("Bertacchini\'s farm"),
            new Farm("Ferrari\'s farm")
    };

    private Farm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString(){
        return this.name;
    }
}
