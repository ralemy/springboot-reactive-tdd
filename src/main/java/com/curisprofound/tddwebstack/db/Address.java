package com.curisprofound.tddwebstack.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Address {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postalCode;

    @OneToOne
    private HighRiseAddressExtension highRiseExtension;
}
