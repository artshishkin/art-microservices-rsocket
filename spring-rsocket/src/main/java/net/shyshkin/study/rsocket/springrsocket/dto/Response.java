package net.shyshkin.study.rsocket.springrsocket.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.shyshkin.study.rsocket.springrsocket.dto.error.ErrorEvent;

import java.util.Objects;

@NoArgsConstructor
@Getter
@ToString
public class Response<T> {

    private ErrorEvent errorResponse;
    private T successResponse;

    public Response(ErrorEvent errorResponse) {
        this.errorResponse = errorResponse;
    }

    public Response(T successResponse) {
        this.successResponse = successResponse;
    }

    public boolean hasError() {
        return Objects.nonNull(errorResponse);
    }

    public static <T> Response<T> with(T t) {
        return new Response<>(t);
    }

    public static <T> Response<T> with(ErrorEvent errorResponse) {
        return new Response<>(errorResponse);
    }
}
