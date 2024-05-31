package com.moonerhigh.firstai.service;

import com.moonerhigh.firstai.EventEnum.EventType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ChatServiceImpl implements ChatService {


    /**
     * 聊天流
     *
     * @description: 聊天流 使用SSE 每次返回完整的聊天内容 直到结束
     * @date: 2024/5/30 23:14
     * @param: * @param response 机器人回复
     * @return: {@link Flux< ServerSentEvent< String>>} 聊天内容
     **/
    @Override
    public Flux<ServerSentEvent<String>> chatStreamContinuous(String response) {
        // Create a list of partial messages
        List<String> partialMessages = IntStream.rangeClosed(1, response.length())
                .mapToObj(i -> response.substring(0, i))
                .collect(Collectors.toList());

        // Create SSE stream
        return getServerSentEventFlux(partialMessages);
    }

    /**
     * 按字符流返回
     *
     * @description: 按字符流返回 使用SSE 每次返回一个字符 直到结束
     * @date: 2024/5/30 23:14
     * @param: * @param response 机器人回复
     * @return: {@link Flux< ServerSentEvent< String>>} 聊天内容
     **/
    @Override
    public Flux<ServerSentEvent<String>> streamResponseByCharacter(String response) {
        List<String> characters = IntStream.range(0, response.length())
                .mapToObj(i -> String.valueOf(response.charAt(i)))
                .toList();

        // Create SSE stream
        return getServerSentEventFlux(characters);
    }

    /**
     * 创建SSE流
     *
     * @param characters 字符列表
     * @return SSE流
     */
    private Flux<ServerSentEvent<String>> getServerSentEventFlux(List<String> characters) {
        return Flux.concat(
                        Mono.just(ServerSentEvent.<String>builder()
                                .event(EventType.START.getEventName())
                                .data("Starting process...")
                                .build()),
                        Flux.fromIterable(characters)
                                .delayElements(Duration.ofMillis(160))
                                .map(character -> ServerSentEvent.<String>builder()
                                        .event(EventType.MESSAGE.getEventName())
                                        .data(character)
                                        .build()),
                        Mono.just(ServerSentEvent.<String>builder()
                                .event(EventType.DONE.getEventName())
                                .data("Process complete")
                                .build())
                )
                .onErrorResume(e -> Mono.just(ServerSentEvent.<String>builder()
                        .event(EventType.ERROR.getEventName())
                        .data("Server error occurred")
                        .build()));
    }
}
