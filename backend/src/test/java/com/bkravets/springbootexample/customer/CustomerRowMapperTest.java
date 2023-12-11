package com.bkravets.springbootexample.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerRowMapperTest {


    @Test
    void mapRow() throws SQLException {

        ResultSet rs = Mockito.mock(ResultSet.class);

        Mockito.when(rs.getLong("id")).thenReturn(1L);
        Mockito.when(rs.getString("name")).thenReturn("John");
        Mockito.when(rs.getString("email")).thenReturn("XXXXXXXXXXXXXX");
        Mockito.when(rs.getInt("age")).thenReturn(25);

        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        Customer customer = customerRowMapper.mapRow(rs, 1);


        assertNotNull(customer);
        assertEquals(1L, customer.getId());
        assertEquals("John", customer.getName());
        assertEquals("XXXXXXXXXXXXXX", customer.getEmail());
        assertEquals(25, customer.getAge());
    }
}