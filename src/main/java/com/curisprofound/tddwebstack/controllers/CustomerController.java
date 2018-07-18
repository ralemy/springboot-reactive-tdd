package com.curisprofound.tddwebstack.controllers;

import com.curisprofound.tddwebstack.db.Customer;
import com.curisprofound.tddwebstack.db.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.function.server.support.RouterFunctionMapping;

import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@RestController
public class CustomerController {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/customers")
    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

    @PutMapping("/customer")
    public Customer saveCustomer(@RequestBody Customer customer){
        return customerRepository.save(customer);
    }
}
