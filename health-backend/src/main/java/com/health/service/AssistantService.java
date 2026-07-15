package com.health.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AssistantService {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${zhipu.api-key:}")
    private String apiKey;

    @Value("${zhipu.base-url:https://open.bigmodel.cn/api/paas/v4/chat/completions}")
    private String baseUrl;

    @Value("${zhipu.model:glm-4-flash}")
    private String model;

    public AssistantService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String chat(Map<String, Object> request) throws Exception {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("AI服务尚未配置，当前使用本地数据分析");
        }
        Object questionValue = request.get("question");
        String question = questionValue == null ? "" : questionValue.toString().trim();
        if (question.isEmpty() || question.length() > 500) {
            throw new IllegalArgumentException("问题不能为空且不能超过500个字符");
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", model);
        payload.put("temperature", 0.2);
        payload.put("messages", Arrays.asList(
            message("system", "你是知衡健康助手。回答优先覆盖睡眠、运动、饮食和饮水等日常健康问题，并在相关时结合用户提供的本人和已授权家人健康数据。胸痛、呼吸困难、昏厥、意识异常、突发单侧无力等症状只能做安全分流：建议及时联系急救服务或就医，不做诊断、不提供处方、不建议自行用药。不要编造数据；没有数据就明确说没有数据。回答简洁、温和，始终提醒健康数据仅供参考，不替代医生。"),
            message("system", "当前授权健康上下文：" + objectMapper.writeValueAsString(context(request))),
            message("user", question)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey.trim());
        JsonNode response = restTemplate.postForObject(baseUrl, new HttpEntity<>(payload, headers), JsonNode.class);
        JsonNode content = response == null ? null : response.path("choices").path(0).path("message").path("content");
        if (content == null || content.isMissingNode() || content.asText().trim().isEmpty()) {
            throw new IllegalStateException("AI返回内容为空");
        }
        return content.asText().trim();
    }

    private Map<String, Object> context(Map<String, Object> request) {
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("userVitals", request.get("userVitals"));
        context.put("authorizedFamily", request.get("family"));
        return context;
    }

    private Map<String, String> message(String role, String content) {
        Map<String, String> message = new LinkedHashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }
}
