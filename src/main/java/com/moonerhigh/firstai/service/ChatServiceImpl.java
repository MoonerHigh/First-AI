package com.moonerhigh.firstai.service;

import com.moonerhigh.firstai.EventEnum.EventType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {


    /**
     * 创建SSE流
     *
     * @param characters 字符列表
     * @return SSE流
     */
    public Flux<ServerSentEvent<String>> getServerSentEventFlux(Flux<String> characters) {
        log.info("Creating SSE stream for characters");

        return Flux.concat(
                        Mono.just(ServerSentEvent.<String>builder()
                                .event(EventType.START.getEventName())
                                .data("Starting process...")
                                .build()),
                        characters
                                .delayElements(Duration.ofMillis(50))
                                .map(character -> ServerSentEvent.<String>builder()
                                        .event(EventType.MESSAGE.getEventName())
                                        .data(character)
                                        .build()),
                        Mono.just(ServerSentEvent.<String>builder()
                                .event(EventType.DONE.getEventName())
                                .data("Process complete")
                                .build())
                )
                .onErrorResume(e -> {
                    log.error("Error occurred while processing SSE stream", e);
                    return Mono.just(ServerSentEvent.<String>builder()
                            .event(EventType.ERROR.getEventName())
                            .data("Server error occurred")
                            .build());
                });
    }
}
