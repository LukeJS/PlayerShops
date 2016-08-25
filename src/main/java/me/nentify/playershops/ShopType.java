package me.nentify.playershops;

public enum ShopType {
    BUY("Buy"), SELL("Sell");

    private String name;

    ShopType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
