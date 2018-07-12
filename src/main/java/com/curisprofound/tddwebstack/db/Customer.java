package com.curisprofound.tddwebstack.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Customer {
    @Id
    private long id;
    private String name;

    @ElementCollection
    private List<String> phoneNumbers;

    public List<String> getPhoneNumbers(){
        if(phoneNumbers == null)
            phoneNumbers = new ArrayList<>();
        return phoneNumbers;
    }
}
