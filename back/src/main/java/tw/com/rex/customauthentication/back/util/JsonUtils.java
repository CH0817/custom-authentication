package tw.com.rex.customauthentication.back.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@AllArgsConstructor
@Component
public class JsonUtils {

    private final ObjectMapper objectMapper;

    public JsonNode readTree(String content) {
        try {
            return objectMapper.readTree(content);
        } catch (JsonProcessingException e) {
            log.error("read JSON content: {}", content);
            throw new RuntimeException("read JSON to JsonNode error!", e);
        }
    }

    public String getStringValue(JsonNode jsonNode, String fieldName) {
        if (Objects.isNull(jsonNode) || !jsonNode.has(fieldName) || StringUtils.isEmpty(fieldName)) {
            return StringUtils.EMPTY;
        }
        JsonNode node = jsonNode.get(fieldName);
        return node.getClass().isAssignableFrom(ArrayNode.class) ? getStringValue((ArrayNode) node) : node.asText();
    }

    private String getStringValue(ArrayNode arrayNode) {
        return StreamSupport.stream(arrayNode.spliterator(), false)
                .filter(Objects::nonNull)
                .map(JsonNode::asText)
                .collect(Collectors.joining(","));
    }

}
