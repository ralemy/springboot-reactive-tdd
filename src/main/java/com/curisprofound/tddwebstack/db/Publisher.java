package com.curisprofound.tddwebstack.db;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Publisher {
    @Id
    private String id;

    private String name;
    private String postalCode;

    private List<String> books;

    public List<String> getBooks(){
        if(books == null)
            books = new ArrayList<>();
        return books;
    }
}
