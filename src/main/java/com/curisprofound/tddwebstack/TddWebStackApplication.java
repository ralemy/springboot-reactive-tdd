package com.curisprofound.tddwebstack;

import com.curisprofound.tddwebstack.db.Book;
import com.curisprofound.tddwebstack.db.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TddWebStackApplication {

    private final BookRepository bookRepository;

    @Autowired
    public TddWebStackApplication(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(TddWebStackApplication.class, args);
    }

}
