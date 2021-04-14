package net.shyshkin.study.rsocket.userservice.mapper;

import net.shyshkin.study.rsocket.userservice.dto.TransactionRequest;
import net.shyshkin.study.rsocket.userservice.dto.TransactionResponse;
import net.shyshkin.study.rsocket.userservice.dto.TransactionStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(TransactionRequest request, TransactionStatus status) {
        TransactionResponse response = new TransactionResponse();
        BeanUtils.copyProperties(request, response);
        response.setStatus(status);
        return response;
    }
}
