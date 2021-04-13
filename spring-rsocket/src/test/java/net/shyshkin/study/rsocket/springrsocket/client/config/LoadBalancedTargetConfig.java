package net.shyshkin.study.rsocket.springrsocket.client.config;

import io.rsocket.loadbalance.LoadbalanceTarget;
import io.rsocket.transport.ClientTransport;
import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.RequiredArgsConstructor;
import net.shyshkin.study.rsocket.springrsocket.client.serviceregistry.ServiceInstanceAddress;
import net.shyshkin.study.rsocket.springrsocket.client.serviceregistry.ServiceRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Configuration
@RequiredArgsConstructor
public class LoadBalancedTargetConfig {

    private final ServiceRegistryClient serviceRegistryClient;

    @Bean
    public Flux<List<LoadbalanceTarget>> targetsFlux() {
        return Flux.from(targets());
    }

    private Mono<List<LoadbalanceTarget>> targets() {
        return Mono.fromSupplier(
                () -> serviceRegistryClient
                        .getInstances("server")
                        .stream()
                        .map(server -> LoadbalanceTarget.from(key(server), transport(server)))
                        .collect(toList()));
    }

    private ClientTransport transport(ServiceInstanceAddress address) {
        return TcpClientTransport.create(address.getHost(), address.getPort());
    }

    //can implement any algorithm but must generate unique and stable key
    private String key(ServiceInstanceAddress instanceCoordinate) {
        return instanceCoordinate.getHost() + ":" + instanceCoordinate.getPort();
    }

}
