package com.curisprofound.tddwebstack.db;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Customer {
    @Id
    private long Id;
}
