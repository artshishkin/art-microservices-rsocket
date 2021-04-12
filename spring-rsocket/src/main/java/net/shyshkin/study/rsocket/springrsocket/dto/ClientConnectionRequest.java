package net.shyshkin.study.rsocket.springrsocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientConnectionRequest {

    private String clientId;
    private String secretKey;

}
