package com.moonerhigh.firstai.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService{

    Flux<ServerSentEvent<String>> getServerSentEventFlux(Flux<String> characters);
}
