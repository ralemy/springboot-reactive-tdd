package com.curisprofound.tddwebstack.db;

import com.sun.javafx.beans.IDProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Invoice {
    @Id
    @GeneratedValue
    private long id;

    private Date date;
    private String place;

    @ManyToOne
    private Customer customer;

    @ManyToMany
    private List<Product> products;
}
