package com.mr.mymr.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    public static <T> T convertJsonStringToObject(String json, Class<T> valueType ) {
        final ObjectMapper mapper = new ObjectMapper();
        T t = null;
        try {
            t = (T) mapper.readValue(json, valueType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
}
