package com.bkravets.springbootexample.journey;


import com.bkravets.springbootexample.customer.Customer;
import com.bkravets.springbootexample.customer.CustomerRegistrationRequest;
import com.bkravets.springbootexample.customer.CustomerUpdateRequest;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient testClient;
    private static String PATH = "/api/v1/customers";

    @Test
    public void canRegisterCustomer() {
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress();
        int age = faker.number().numberBetween(18, 65);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age);

        testClient.post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk();

        List<Customer> customers = testClient.get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        Customer expectedCustomer = new Customer(name, email, age);
        assertThat(customers).
                usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        long id = customers.stream()
                        .filter(c -> c.getEmail().equals(email))
                        .findFirst()
                        .orElseThrow()
                        .getId();

        expectedCustomer.setId(id);


        testClient.get()
                .uri(PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);

    }

    @Test
    public void canDeleteCustomer() {
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress();
        int age = faker.number().numberBetween(18, 65);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age);

        testClient.post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk();

        List<Customer> customers = testClient.get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        Customer expectedCustomer = new Customer(name, email, age);
        assertThat(customers).
                usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        long id = customers.stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElseThrow()
                .getId();

        testClient.delete()
                .uri(PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();


        testClient.get()
                .uri(PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void canUpdateCustomer() {
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress();
        int age = faker.number().numberBetween(18, 65);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(name, email, age);

        testClient.post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk();

        List<Customer> customers = testClient.get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();


        long id = customers.stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElseThrow()
                .getId();


        String newName = faker.name().fullName();
        String newEmail = faker.internet().emailAddress();
        int newAge = faker.number().numberBetween(18, 65);

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(newName, newEmail, newAge);

        testClient.put()
                .uri(PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus().isOk();




        testClient.get()
                .uri(PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .isEqualTo(new Customer(id, newName, newEmail, newAge));

    }




}
