package com.curisprofound.tddwebstack.db;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BookRepository extends ReactiveMongoRepository<Book,String> {
}
