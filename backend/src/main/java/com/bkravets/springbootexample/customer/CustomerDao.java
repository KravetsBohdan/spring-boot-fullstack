package com.bkravets.springbootexample.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Long id);
    void insertCustomer(Customer customer);
    boolean existsCustomerWithId(Long id);
    boolean existsCustomerWithEmail(String email);
    void deleteCustomer(Long id);
    void updateCustomer(Customer customer);

}
