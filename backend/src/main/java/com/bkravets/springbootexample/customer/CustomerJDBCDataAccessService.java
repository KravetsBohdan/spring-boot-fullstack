package com.bkravets.springbootexample.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {
    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper rowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return jdbcTemplate.query("SELECT * FROM customer", rowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        String sql = """
                SELECT * FROM customer
                WHERE id = ?
                """;
        return jdbcTemplate.queryForStream(sql, rowMapper, id)
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        String sql = """
                INSERT INTO customer (name, email, age)
                VALUES (?, ?, ?)
                """;
        jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge());
    }

    @Override
    public boolean existsCustomerWithId(Long id) {
        String sql = """
                SELECT COUNT(id) FROM customer
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        String sql = """
                SELECT COUNT(id) FROM customer
                WHERE email = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);

        return count != null && count > 0;
    }

    @Override
    public void deleteCustomer(Long id) {
        String sql = """
                DELETE FROM customer WHERE id = ?
                """;

        jdbcTemplate.update(sql, id);
    }

    @Override
    public void updateCustomer(Customer customer) {
        if (customer.getName() != null) {
            String sql = """
                        UPDATE customer
                        SET name = ?
                        WHERE id = ?
                    """;

            jdbcTemplate.update(sql,
                    customer.getName(),
                    customer.getId());
        }

        if (customer.getEmail() != null) {
            String sql = """
                        UPDATE customer
                        SET email = ?
                        WHERE id = ?
                    """;

            jdbcTemplate.update(sql,
                    customer.getEmail(),
                    customer.getId());
        }


        if (customer.getAge() != null) {
            String sql = """
                        UPDATE customer
                        SET age = ?
                        WHERE id = ?
                    """;

            jdbcTemplate.update(sql,
                    customer.getAge(),
                    customer.getId());
        }
    }

}
