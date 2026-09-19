package org.example;

public enum Rockets {
    FALCON_ONE("Falcon 1", 3000),
    FALCON_NINE("Falcon 9", 5000),
    FALCON_HEAVY("Falcon Heavy", 6000),
    STARSHIP("Starship", 9000);

    private final String rocketName;
    private final int rocketPrice;

    Rockets(String rocketName, int rocketPrice){
        this.rocketName = rocketName;
        this.rocketPrice = rocketPrice;
    }

    public String getRocketName(){
        return rocketName;
    }

    public int getRocketPrice(){
        return rocketPrice;
    }
}
