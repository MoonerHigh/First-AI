package com.moonerhigh.firstai.controller;

import com.moonerhigh.firstai.dto.ChatRequestDto;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.ai.image.ImageClient;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController("/chat/image")
public class ChatImageController {

    @Resource
    private ImageClient imageClient;

    /**
     * 生成图片
     *
     * @description: 生成图片
     * @date: 2024/5/30 23:14
     * @param: * @param message 用户指令
     * @return: {@link Flux< ServerSentEvent< String>>} 图片URL
     **/
    @PostMapping(value = "/ai/generateImage", produces = MediaType.IMAGE_JPEG_VALUE)
    public Flux<ServerSentEvent<String>> generateImage(@Valid @RequestBody ChatRequestDto requestDto) {
        String url = imageClient.call(new ImagePrompt(requestDto.getMessage())).getResult().getOutput().getUrl();
        return Flux.just(ServerSentEvent.builder(url).build());
    }
}
