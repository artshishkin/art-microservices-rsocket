package net.shyshkin.study.rsocket.springrsocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComputationResponseDto {

    private int input;
    private int output;

}
