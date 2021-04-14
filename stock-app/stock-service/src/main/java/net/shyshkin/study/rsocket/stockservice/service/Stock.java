package net.shyshkin.study.rsocket.stockservice.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class Stock {

    private int price;
    @Getter
    private final String code;
    private final int volatility;

    public int getPrice() {
        updatePrice();
        return price;
    }

    private void updatePrice() {
        int delta = ThreadLocalRandom.current().nextInt(-volatility, volatility + 1);
        price += delta;
        price = Math.max(price, 0);
    }


}
