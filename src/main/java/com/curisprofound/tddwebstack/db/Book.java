package com.curisprofound.tddwebstack.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.Map;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Book {
    @Id
    private String id;
    private String title;
    private Author author;

    @DBRef
    private Publisher publisher;


}
