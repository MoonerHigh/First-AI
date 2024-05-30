package com.moonerhigh.firstai.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@CrossOrigin
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
     * @description: 默认聊天接口 使用SSE逐字返回
     * @date: 2024/5/30 23:14
     * @param: * @param message 用户输入消息
     * @return: {@link Flux< ServerSentEvent< String>>} 聊天内容
     **/

    @GetMapping(value = "/ai/generate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        String response = chatClient.call(message);

        // Create a list of partial messages
        List<String> partialMessages = IntStream.rangeClosed(1, response.length())
                .mapToObj(i -> response.substring(0, i))
                .collect(Collectors.toList());

        // Create SSE stream
        return Flux.concat(
                        Mono.just(ServerSentEvent.<String>builder().event("start").data("Starting process...").build()),
                        Flux.fromIterable(partialMessages)
                                .delayElements(Duration.ofMillis(160))
                                .map(partialMessage -> ServerSentEvent.<String>builder().event("message").data(partialMessage).build()),
                        Mono.just(ServerSentEvent.<String>builder().event("done").data("Process complete").build())
                )
                .onErrorResume(e -> Mono.just(ServerSentEvent.<String>builder().event("error").data("Server error occurred").build()));
    }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return chatClient.stream(prompt);
    }
}