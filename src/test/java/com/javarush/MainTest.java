package com.javarush;


import com.javarush.domain.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MainTest {

    @Mock
    private SessionFactory sessionFactory;

    private Main main;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        main = new Main();
        main.setSessionFactory(sessionFactory);
    }

    @Test
    void testCreateCustomer() {
        when(sessionFactory.getCurrentSession()).thenReturn(mock(Session.class));
        Customer actualCustomer = main.createCustomer();
        assertNotNull(actualCustomer);
    }
}
