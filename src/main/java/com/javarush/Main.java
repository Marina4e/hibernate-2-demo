package com.javarush;

import com.javarush.dao.ActorDAO;
import com.javarush.dao.AddressDAO;
import com.javarush.dao.CategoryDAO;
import com.javarush.dao.CityDAO;
import com.javarush.dao.CountryDAO;
import com.javarush.dao.CustomerDAO;
import com.javarush.dao.FilmDAO;
import com.javarush.dao.FilmTextDAO;
import com.javarush.dao.InventoryDAO;
import com.javarush.dao.LanguageDAO;
import com.javarush.dao.PaymentDAO;
import com.javarush.dao.RentalDAO;
import com.javarush.dao.StaffDAO;
import com.javarush.dao.StoreDAO;
import com.javarush.domain.Actor;
import com.javarush.domain.Address;
import com.javarush.domain.Category;
import com.javarush.domain.City;
import com.javarush.domain.Country;
import com.javarush.domain.Customer;
import com.javarush.domain.Feature;
import com.javarush.domain.Film;
import com.javarush.domain.FilmText;
import com.javarush.domain.Inventory;
import com.javarush.domain.Language;
import com.javarush.domain.Payment;
import com.javarush.domain.Rating;
import com.javarush.domain.Rental;
import com.javarush.domain.Staff;
import com.javarush.domain.Store;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class Main {
    private final SessionFactory sessionFactory;
    private final ActorDAO actorDAO;
    private final AddressDAO addressDAO;
    private final CategoryDAO categoryDAO;
    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;
    private final CustomerDAO customerDAO;
    private final FilmDAO filmDAO;
    private final FilmTextDAO filmTextDAO;
    private final InventoryDAO inventoryDAO;
    private final LanguageDAO languageDAO;
    private final PaymentDAO paymentDAO;
    private final RentalDAO rentalDAO;
    private final StaffDAO staffDAO;
    private final StoreDAO storeDAO;

    public Main() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/movie");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "ukraine2023");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        properties.put(Environment.HBM2DDL_AUTO, "validate");

        sessionFactory = new Configuration()
                .addAnnotatedClass(Actor.class)
                .addAnnotatedClass(Address.class)
                .addAnnotatedClass(Category.class)
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Film.class)
                .addAnnotatedClass(FilmText.class)
                .addAnnotatedClass(Inventory.class)
                .addAnnotatedClass(Language.class)
                .addAnnotatedClass(Payment.class)
                .addAnnotatedClass(Rental.class)
                .addAnnotatedClass(Staff.class)
                .addAnnotatedClass(Store.class)
                .addProperties(properties)
                .buildSessionFactory();

        actorDAO = new ActorDAO(sessionFactory);
        addressDAO = new AddressDAO(sessionFactory);
        categoryDAO = new CategoryDAO(sessionFactory);
        cityDAO = new CityDAO(sessionFactory);
        countryDAO = new CountryDAO(sessionFactory);
        customerDAO = new CustomerDAO(sessionFactory);
        filmDAO = new FilmDAO(sessionFactory);
        filmTextDAO = new FilmTextDAO(sessionFactory);
        inventoryDAO = new InventoryDAO(sessionFactory);
        languageDAO = new LanguageDAO(sessionFactory);
        paymentDAO = new PaymentDAO(sessionFactory);
        rentalDAO = new RentalDAO(sessionFactory);
        staffDAO = new StaffDAO(sessionFactory);
        storeDAO = new StoreDAO(sessionFactory);
    }

    public static void main(String[] args) {
        Main main = new Main();
        Customer customer = main.createCustomer();
        main.customerReturnInventoryToStore();
        main.customerRentInventory(customer);
        main.newFilmWasMade();
    }

    private void newFilmWasMade() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();

            Language language = languageDAO.getItems(0, 20)
                    .stream().unordered().findAny().get();
            List<Category> categories = categoryDAO.getItems(0, 5);
            List<Actor> actors = actorDAO.getItems(0, 20);

            Film film = new Film();
            film.setActors(new HashSet<>(actors));
            film.setRating(Rating.NC17);
            film.setSpecialFeatures(Set.of(Feature.TRAILERS, Feature.COMMENTARIES));
            film.setLength((short) 123);
            film.setReplacementCost(BigDecimal.TEN);
            film.setRentalRate(BigDecimal.ZERO);
            film.setLanguage(language);
            film.setDescription("scary film");
            film.setTitle("scary-2");
            film.setRentalDuration((byte) 44);
            film.setOriginalLanguage(language);
            film.setCategories(new HashSet<>(categories));
            film.setYear(Year.now());
            filmDAO.save(film);

            FilmText filmText = new FilmText();
            filmText.setFilm(film);
            filmText.setId(film.getId());
            filmText.setDescription("scary film");
            filmText.setTitle("scary-2");
            filmTextDAO.save(filmText);


            session.getTransaction().commit();
        }
    }

    private void customerRentInventory(Customer customer) {
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

    private void customerReturnInventoryToStore() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Rental rental = rentalDAO.getAnyUnreturnedRental();
            rental.setReturnDate(LocalDateTime.now());

            rentalDAO.save(rental);
            session.getTransaction().commit();
        }
    }

    Customer createCustomer() {
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

    public void setSessionFactory(SessionFactory sessionFactory) {
    }
}
