package com.bkravets.springbootexample.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class CustomerJPADataAccessServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    private AutoCloseable autoCloseable;

    private CustomerJPADataAccessService underTest;

    @BeforeEach
    void setUp() {
       autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        underTest.selectAllCustomers();

        verify(customerRepository).findAll();

    }

    @Test
    void selectCustomerById() {
        long id = 1L;

        underTest.selectCustomerById(id);

        verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        Customer customer = new Customer("Name", "email@mail.com", 35);

        underTest.insertCustomer(customer);

        verify(customerRepository).save(customer);
    }

    @Test
    void existsCustomerWithId() {
        long id = 1L;

        underTest.existsCustomerWithId(id);

        verify(customerRepository).existsById(id);
    }

    @Test
    void existsCustomerWithEmail() {
        String email = "email@mail.com";

        underTest.existsCustomerWithEmail(email);

        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomer() {
        long id = 1L;

        underTest.deleteCustomer(id);

        verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer(1L,"Name", "email@mail.com", 35);

        underTest.updateCustomer(customer);

        verify(customerRepository).save(customer);
    }
}