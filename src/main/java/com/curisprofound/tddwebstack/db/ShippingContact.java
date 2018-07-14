package com.curisprofound.tddwebstack.db;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShippingContact {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    private String phoneNumber;

    @OneToOne
    private Customer customer;
}
