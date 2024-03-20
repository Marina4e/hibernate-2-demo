package com.javarush;

import com.javarush.domain.Customer;
import com.javarush.operation.CustomerOperations;
import com.javarush.operation.FilmOperations;
import com.javarush.operation.RentalOperations;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class Main {
    private final SessionFactory sessionFactory;

    public Main() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    public static void main(String[] args) {
        Main main = new Main();

        CustomerOperations customerOperations = new CustomerOperations(main.sessionFactory);
        RentalOperations rentalOperations = new RentalOperations(main.sessionFactory);
        FilmOperations filmOperations = new FilmOperations(main.sessionFactory);

        Customer customer = customerOperations.createCustomer();
        rentalOperations.customerReturnInventoryToStore();
        rentalOperations.customerRentInventory(customer);
        filmOperations.newFilmWasMade();
    }
}
