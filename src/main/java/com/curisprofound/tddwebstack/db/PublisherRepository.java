package com.curisprofound.tddwebstack.db;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PublisherRepository extends ReactiveMongoRepository<Publisher, String> {
}
