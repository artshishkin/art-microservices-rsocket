package net.shyshkin.study.rsocket.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;

import java.io.IOException;

public class ObjectUtil {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static Payload toPayload(Object o) {

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(o);
            return DefaultPayload.create(bytes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObject(Payload payload, Class<T> clazz) {

        byte[] bytes = payload.data().array();

        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
