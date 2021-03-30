package com.harshvladha.coindcx.httpclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Response handler for handling all the responses.
 */
public class CoinDCXResponseHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    public <D> D handle(String body, Class<D> responseType) throws JsonProcessingException {
        return objectMapper.readValue(body, responseType);
    }

}
