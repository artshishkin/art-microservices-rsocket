package net.shyshkin.study.rsocket.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;
import lombok.SneakyThrows;

public class ObjectUtil {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static Payload toPayload(Object o) {
        byte[] bytes = objectMapper.writeValueAsBytes(o);
        return DefaultPayload.create(bytes);
    }

    @SneakyThrows
    public static <T> T toObject(Payload payload, Class<T> clazz) {
        byte[] bytes = payload.data().array();
        return objectMapper.readValue(bytes, clazz);
    }

}
