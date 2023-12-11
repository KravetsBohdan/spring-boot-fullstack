package com.bkravets.springbootexample.customer;

import com.bkravets.springbootexample.exception.DuplicateResourceException;
import com.bkravets.springbootexample.exception.RequestValidationException;
import com.bkravets.springbootexample.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Long id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "customer with id [%s] not found".formatted(id)
                ));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        if (customerDao.existsCustomerWithEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException("Email is already taken");
        }

        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );
        customerDao.insertCustomer(customer);
    }

    public void deleteCustomerById(Long id) {
        if (!customerDao.existsCustomerWithId(id)) {
            throw new ResourceNotFoundException(
                    "customer with id [%s] not found".formatted(id)
            );
        }
        customerDao.deleteCustomer(id);

    }

    public void updateCustomer(Long id, CustomerUpdateRequest updateRequest) {
        Customer customer = getCustomer(id);

        boolean changes = false;
        if(updateRequest.name() != null && !updateRequest.name().equals(customer.getName())) {
            customer.setName(updateRequest.name());
            changes = true;
        }

        if(updateRequest.age() != null && !updateRequest.age().equals(customer.getAge())) {
            customer.setAge(updateRequest.age());
            changes = true;
        }

        if(updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())) {
            if(customerDao.existsCustomerWithEmail(updateRequest.email())) {
                throw new DuplicateResourceException("Email is already taken");
            }
            customer.setEmail(updateRequest.email());
            changes = true;
        }


        if (!changes) {
            throw new RequestValidationException("No data changed");
        }
            customerDao.updateCustomer(customer);
    }
}
