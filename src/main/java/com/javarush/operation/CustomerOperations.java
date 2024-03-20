package com.javarush.operation;

import com.javarush.dao.AddressDAO;
import com.javarush.dao.CityDAO;
import com.javarush.dao.CustomerDAO;
import com.javarush.dao.StoreDAO;
import com.javarush.domain.Address;
import com.javarush.domain.City;
import com.javarush.domain.Customer;
import com.javarush.domain.Store;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class CustomerOperations {
    private final SessionFactory sessionFactory;
    private final StoreDAO storeDAO;
    private final CityDAO cityDAO;
    private final AddressDAO addressDAO;
    private final CustomerDAO customerDAO;

    public CustomerOperations(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.storeDAO = new StoreDAO(sessionFactory);
        this.cityDAO = new CityDAO(sessionFactory);
        this.addressDAO = new AddressDAO(sessionFactory);
        this.customerDAO = new CustomerDAO(sessionFactory);
    }

    public Customer createCustomer() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Store store = storeDAO.getItems(0, 1).get(0);
            City city = cityDAO.getByName("Kragujevac");
            Address address = new Address();
            address.setAddress("Independence str, 1");
            address.setPhone("999-222-333");
            address.setCity(city);
            address.setDistrict("Central park");
            addressDAO.save(address);

            Customer customer = new Customer();
            customer.setActive(true);
            customer.setAddress(address);
            customer.setStore(store);
            customer.setFirstName("Petro");
            customer.setLastName("Petrov");
            customer.setEmail("email@mail.ua");
            customerDAO.save(customer);

            session.getTransaction().commit();

            return customer;
        }
    }
}
