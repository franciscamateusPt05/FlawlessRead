package com.example.FlawlessRead.model;

import com.fasterxml.jackson.databind.JsonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class RangeDeserializer extends JsonDeserializer<Range> {
    @Override
    public Range deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        if (node.isArray() && node.size() == 2) {
            double min = node.get(0).asDouble();
            double max = node.get(1).asDouble();
            return new Range(min, max);
        }
        return null; // ou lançar exceção
    }
}

