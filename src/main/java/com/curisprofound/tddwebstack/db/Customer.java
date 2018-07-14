package com.curisprofound.tddwebstack.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @ElementCollection
    private List<Address> addresses;

    @ElementCollection
    private Map<String, String> mealPreferences;

    @OneToOne
    private ShippingContact shippingContact;

    @OneToMany
    private  List<Invoice> invoices;

    public List<String> getPhoneNumbers(){
        if(phoneNumbers == null)
            phoneNumbers = new ArrayList<>();
        return phoneNumbers;
    }

    public List<Address> getAddresses(){
        if(addresses == null)
            addresses = new ArrayList<>();
        return addresses;
    }

    public Map<String,String> getMealPreferences(){
        if(mealPreferences == null)
            mealPreferences = new HashMap<>();
        return mealPreferences;
    }

    public List<Invoice> getInvoices(){
        if(invoices == null)
            invoices = new ArrayList<>();
        return invoices;
    }
}
