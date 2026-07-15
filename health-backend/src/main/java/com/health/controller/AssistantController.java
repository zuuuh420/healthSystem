package com.health.controller;

import com.health.common.Result;
import com.health.service.AssistantService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/assistant")
public class AssistantController {
    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/chat")
    public Result<Map<String, String>> chat(@RequestBody Map<String, Object> request) {
        try {
            String answer = assistantService.chat(request);
            return Result.success("success", java.util.Collections.singletonMap("answer", answer));
        } catch (IllegalStateException e) {
            return Result.error(503, e.getMessage());
        } catch (Exception e) {
            return Result.error(502, "AI服务暂时不可用，请稍后重试");
        }
    }
}
