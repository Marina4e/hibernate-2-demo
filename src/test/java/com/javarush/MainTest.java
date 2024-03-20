package com.javarush;


import com.javarush.domain.Customer;
import com.javarush.operation.CustomerOperations;
import com.javarush.operation.FilmOperations;
import com.javarush.operation.RentalOperations;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MainTest {
    private static SessionFactory sessionFactory;
    private static CustomerOperations customerOperations;
    private static RentalOperations rentalOperations;
    private static FilmOperations filmOperations;

    @BeforeAll
    public static void setUp() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        customerOperations = new CustomerOperations(sessionFactory);
        rentalOperations = new RentalOperations(sessionFactory);
        filmOperations = new FilmOperations(sessionFactory);
    }

    @AfterAll
    public static void tearDown() {
        sessionFactory.close();
    }

    @Test
    public void testCreateCustomer() {
        Customer customer = customerOperations.createCustomer();
        assertNotNull(customer);
    }

    @Test
    public void testCustomerRentInventory() {
        Customer customer = customerOperations.createCustomer();
        rentalOperations.customerRentInventory(customer);
    }

    @Test
    public void testCustomerReturnInventoryToStore() {
        rentalOperations.customerReturnInventoryToStore();
    }

    @Test
    public void testNewFilmWasMade() {
        filmOperations.newFilmWasMade();
    }
}
