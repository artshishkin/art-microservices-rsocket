package net.shyshkin.study.rsocket.springrsocket.dto.error;

public enum StatusCode {

    EC001 ("given number is not within the range"),
    EC002 ("your usage limit exceeded");

    private final String description;

    StatusCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
