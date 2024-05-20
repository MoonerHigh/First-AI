package com.moonerhigh.firstai.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController("/chat")
public class ChatController {

    private final OpenAiChatClient chatClient;

    @Autowired
    public ChatController(OpenAiChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 默认聊天接口
     *
     * @description: 默认生成对话接口
     * @date: 2024/5/21 0:00
     * @param: * @param message 消息
     * @return: {@link SseEmitter} 对话结果 SSE
     **/
    @GetMapping(value = "/ai/generate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        SseEmitter emitter = new SseEmitter();
        log.info("OpenAI建立SSE连接...");
        CompletableFuture.supplyAsync(() -> {
            String result = chatClient.call(message);
            return Map.of("generation", result);
        }).whenComplete((result, throwable) -> {
            try {
                emitter.send(SseEmitter.event().data(result));
                emitter.complete();
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });
        log.info("OpenAI关闭SSE连接...");
        return emitter;
    }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatClient.stream(prompt);
    }
}