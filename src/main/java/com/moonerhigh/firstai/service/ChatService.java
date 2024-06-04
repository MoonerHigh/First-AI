package com.moonerhigh.firstai.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface ChatService{

    Flux<ServerSentEvent<String>> getServerSentEventFlux(Flux<String> characters);
}
