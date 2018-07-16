package com.curisprofound.tddwebstack.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    private String customerRepository;

    @GetMapping("/customers")
    public void getAllCustomers(){

    }
}
