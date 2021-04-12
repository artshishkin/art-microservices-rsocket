package net.shyshkin.study.rsocket.springrsocket.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorEvent {

    private StatusCode statusCode;
    private final LocalDateTime date = LocalDateTime.now();

}
