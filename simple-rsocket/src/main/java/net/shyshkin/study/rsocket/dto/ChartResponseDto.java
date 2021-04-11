package net.shyshkin.study.rsocket.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChartResponseDto {

    private int input;
    private int output;

    private String getFormat(int value) {
        return "%3d|%" + value + "s";
    }

    @Override
    public String toString() {
        String chartFormat = getFormat(output);
        return String.format(chartFormat, input, "*");
    }
}
