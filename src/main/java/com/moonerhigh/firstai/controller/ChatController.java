package com.moonerhigh.firstai.controller;

import com.moonerhigh.firstai.dto.ChatRequestDto;
import com.moonerhigh.firstai.service.ChatService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@CrossOrigin
@RestController("/chat")
public class ChatController {

    @Resource
    private ChatService chatService;

    private final OpenAiChatClient chatClient;

    @Autowired
    public ChatController(OpenAiChatClient chatClient) {
        this.chatClient = chatClient;
    }


    /**
     * 按字符流返回
     *
     * @description: 按字符流返回 使用SSE 每次返回一个字符 直到结束
     * @date: 2024/5/30 23:14
     * @param: * @param message 用户输入消息
     * @return: {@link Flux< ServerSentEvent< String>>} 聊天内容
     **/
    @PostMapping(value = "/ai/generateContinuous", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> generateContinuous(@Valid @RequestBody ChatRequestDto requestDto) {
        Flux<String> responseStream = chatClient.stream(requestDto.getMessage());
        return chatService.getServerSentEventFlux(responseStream);
    }

    @PostMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@Valid @RequestBody ChatRequestDto requestDto) {
        Prompt prompt = new Prompt(new UserMessage(requestDto.getMessage()));
        return chatClient.stream(prompt);
    }
}