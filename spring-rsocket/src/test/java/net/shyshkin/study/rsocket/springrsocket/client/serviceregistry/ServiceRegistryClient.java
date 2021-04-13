package net.shyshkin.study.rsocket.springrsocket.client.serviceregistry;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ServiceRegistryClient {

    private final Map<String, List<ServiceInstanceAddress>> serviceRegistryMap;

    public ServiceRegistryClient() {
        serviceRegistryMap = new HashMap<>();

        List<ServiceInstanceAddress> serverInstances = IntStream.rangeClosed(6563, 6565)
                .mapToObj(port -> new ServiceInstanceAddress("localhost", port))
                .collect(Collectors.toList());

        serviceRegistryMap.put("server", serverInstances);
    }

    public List<ServiceInstanceAddress> getInstances(String serviceName) {
        return serviceRegistryMap.get(serviceName);
    }
}
