package net.shyshkin.study.rsocket.tradingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.tradingservice.client.UserClient;
import net.shyshkin.study.rsocket.tradingservice.dto.UserDto;
import net.shyshkin.study.rsocket.tradingservice.dto.UserStockDto;
import net.shyshkin.study.rsocket.tradingservice.mapper.DtoMapper;
import net.shyshkin.study.rsocket.tradingservice.repository.UserStockRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStockRepository stockRepository;
    private final UserClient userClient;
    private final DtoMapper dtoMapper;

    public Flux<UserStockDto> getAllUserStocks(String userId) {
        return stockRepository.findByUserId(userId)
                .map(dtoMapper::toUserStockDto);
    }

    public Flux<UserDto> getAllUsers() {
        return userClient.getAllUsers();
    }
}
