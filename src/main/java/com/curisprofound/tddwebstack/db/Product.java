package com.curisprofound.tddwebstack.db;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private long number;

    @ManyToMany(mappedBy = "products")
    private List<Invoice> invoices;

    public List<Invoice> getInvoices(){
        if(invoices == null)
            invoices = new ArrayList<>();
        return invoices;
    }
}
