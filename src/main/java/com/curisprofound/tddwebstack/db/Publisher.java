package com.curisprofound.tddwebstack.db;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Publisher {
    @Id
    private String id;

    private String name;
    private String postalCode;
}
