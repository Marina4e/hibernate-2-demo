package com.javarush.operation;

import com.javarush.dao.FilmDAO;
import com.javarush.dao.InventoryDAO;
import com.javarush.dao.PaymentDAO;
import com.javarush.dao.RentalDAO;
import com.javarush.dao.StaffDAO;
import com.javarush.dao.StoreDAO;
import com.javarush.domain.Customer;
import com.javarush.domain.Film;
import com.javarush.domain.Inventory;
import com.javarush.domain.Payment;
import com.javarush.domain.Rental;
import com.javarush.domain.Staff;
import com.javarush.domain.Store;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RentalOperations {
    private final SessionFactory sessionFactory;
    private final FilmDAO filmDAO;
    private final StoreDAO storeDAO;
    private final InventoryDAO inventoryDAO;
    private final StaffDAO staffDAO;
    private final RentalDAO rentalDAO;
    private final PaymentDAO paymentDAO;

    public RentalOperations(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.filmDAO = new FilmDAO(sessionFactory);
        this.storeDAO = new StoreDAO(sessionFactory);
        this.inventoryDAO = new InventoryDAO(sessionFactory);
        this.staffDAO = new StaffDAO(sessionFactory);
        this.rentalDAO = new RentalDAO(sessionFactory);
        this.paymentDAO = new PaymentDAO(sessionFactory);
    }

    public void customerRentInventory(Customer customer) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Film film = filmDAO.getFirstAvailableFilmForRent();
            Store store = storeDAO.getItems(0, 1).get(0);

            Inventory inventory = new Inventory();
            inventory.setFilm(film);
            inventory.setStore(store);
            inventoryDAO.save(inventory);

            Staff staff = store.getStaff();

            Rental rental = new Rental();
            rental.setRentalDate(LocalDateTime.now());
            rental.setCustomer(customer);
            rental.setInventory(inventory);
            rental.setStaff(staff);
            rentalDAO.save(rental);

            Payment payment = new Payment();
            payment.setRental(rental);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setCustomer(customer);
            payment.setAmount(BigDecimal.valueOf(55.77));
            payment.setStaff(staff);
            paymentDAO.save(payment);


            session.getTransaction().commit();
        }
    }

    public void customerReturnInventoryToStore() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Rental rental = rentalDAO.getAnyUnreturnedRental();
            rental.setReturnDate(LocalDateTime.now());

            rentalDAO.save(rental);
            session.getTransaction().commit();
        }
    }
}
