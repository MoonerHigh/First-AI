package com.moonerhigh.firstai.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface ChatService{

    Flux<ServerSentEvent<String>> chatStreamContinuous(String message);

    Flux<ServerSentEvent<String>> streamResponseByCharacter(String response);
}
