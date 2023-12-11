package com.bkravets.springbootexample.customer;

import com.bkravets.springbootexample.exception.DuplicateResourceException;
import com.bkravets.springbootexample.exception.RequestValidationException;
import com.bkravets.springbootexample.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerDao customerDao;

    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        underTest.getAllCustomers();

        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        Long id = 1L;
        Customer customer = new Customer(id, "Name", "email@mail.com", 30);
        Mockito.when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        Customer actual = underTest.getCustomer(id);

        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowGetCustomerReturnsEmptyOptional() {
        Long id = 1L;
        Mockito.when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.empty());


        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

    }

    @Test
    void addCustomerWhenEmailFree() {
        String email = "email@mail.com";
        when(customerDao.existsCustomerWithEmail(anyString()))
                .thenReturn(false);

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                "Name",
                email,
                30
        );
        underTest.addCustomer(customerRegistrationRequest);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getEmail()).isEqualTo(email);
        assertThat(capturedCustomer.getName()).isEqualTo(customerRegistrationRequest.name());
        assertThat(capturedCustomer.getAge()).isEqualTo(customerRegistrationRequest.age());
        assertThat(capturedCustomer.getId()).isNull();

    }


    @Test
    void addCustomerWhenEmailDuplicatedExceptionThrown() {
        when(customerDao.existsCustomerWithEmail(anyString()))
                .thenReturn(true);

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                "Name",
                "email@mail.com",

                30
        );
        assertThatThrownBy(() -> underTest.addCustomer(customerRegistrationRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email is already taken");

        verify(customerDao, never()).insertCustomer(any(Customer.class));
    }

    @Test
    void deleteCustomerByIdWhenCustomerExists() {
        Long id = 1L;
        when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        underTest.deleteCustomerById(id);

        verify(customerDao).deleteCustomer(id);
    }

    @Test
    void deleteCustomerByIdWhenCustomerNotExists() {
        Long id = 1L;
        when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void canUpdateAllCustomerProperties() {
        Long id = 1L;
        Customer customer = new Customer(id, "Name", "email@mail.com", 30);
        Mockito.when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        String newEmail = "newEmail@mail.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "newName",
                newEmail,
                40);
        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        underTest.updateCustomer(id, request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getId()).isEqualTo(id);
    }


    @Test
    public void canUpdateOnlyCustomerName() {
        Long id = 1L;
        Customer customer = new Customer(id, "Name", "email@mail.com", 30);
        Mockito.when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "newName",
                null,
                null);

        underTest.updateCustomer(id, request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getId()).isEqualTo(customer.getId());
    }

    @Test
    public void canUpdateOnlyCustomerEmail() {
        Long id = 1L;
        Customer customer = new Customer(id, "Name", "email@mail.com", 30);
        Mockito.when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        String newEmail = "newName@mail.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                newEmail,
                null);
        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        underTest.updateCustomer(id, request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getId()).isEqualTo(customer.getId());
    }

    @Test
    public void canUpdateOnlyCustomerAge() {
        Long id = 1L;
        Customer customer = new Customer(id, "Name", "email@mail.com", 30);
        Mockito.when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                null,
                40);

        underTest.updateCustomer(id, request);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getId()).isEqualTo(customer.getId());
    }


    @Test
    public void updateCustomerThrowsExceptionWhenEmailIsDuplicated() {
        Long id = 1L;
        Customer customer = new Customer(id, "Name", "email@mail.com", 30);
        Mockito.when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        String newEmail = "newName@mail.com";
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null,
                newEmail,
                null);
        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(true);

        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email is already taken");

        verify(customerDao, never()).updateCustomer(customer);
    }

    @Test
    void updateCustomerThrowsExceptionWhenNoChanges() {
        Long id = 1L;
        String name = "Name";
        String email = "email@mail.com";
        int age = 30;
        Customer customer = new Customer(id, name, email, age);
        Mockito.when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                name,
                email,
                age);

        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changed");

        verify(customerDao, never()).updateCustomer(customer);
    }

}