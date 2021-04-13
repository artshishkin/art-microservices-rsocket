package net.shyshkin.study.rsocket.springrsocket.client.serviceregistry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInstanceAddress {

    private String host;
    private int port;

}
