package com.curisprofound.tddwebstack.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class HighRiseAddressExtension {
    @Id
    @GeneratedValue
    private long id;
    private String suite;
    private String floor;
    private String buzzerCode;
}
