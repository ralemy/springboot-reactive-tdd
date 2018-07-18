package com.curisprofound.tddwebstack.controllers;

import com.curisprofound.tddwebstack.db.Book;
import com.curisprofound.tddwebstack.db.BookRepository;
import org.omg.CORBA.ServerRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class BookHandler {

    private final BookRepository bookRepository;


    public BookHandler(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Mono<ServerResponse> getAll(ServerRequest request){
        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookRepository.findAll(), Book.class);
    }



}
