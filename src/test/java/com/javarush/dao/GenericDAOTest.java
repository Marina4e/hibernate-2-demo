package com.javarush.dao;

import com.javarush.domain.City;
import com.javarush.domain.Film;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GenericDAOTest {

    private SessionFactory mockSessionFactory;
    private Session mockSession;
    private Query mockQuery;

    @Before
    public void setUp() {
        mockSessionFactory = mock(SessionFactory.class);
        mockSession = mock(Session.class);
        mockQuery = mock(Query.class);
        when(mockSessionFactory.getCurrentSession()).thenReturn(mockSession);
    }

    @Test
    public void testCityDAOSave() {
        String cityName = "New City";
        City cityToSave = new City(cityName);

        when(mockSession.createQuery("SELECT c FROM City c WHERE c.city = :NAME", City.class)).thenReturn(mockQuery);
        when(mockQuery.setParameter("NAME", cityName)).thenReturn(mockQuery);
        when(mockQuery.setMaxResults(1)).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(null);

        CityDAO cityDAO = new CityDAO(mockSessionFactory);
        City savedCity = cityDAO.save(cityToSave);

        assertNotNull(savedCity);
        assertEquals(cityToSave, savedCity);
        verify(mockSession).saveOrUpdate(cityToSave);
    }

    @Test
    public void testFilmDAOGetFirstAvailableFilmForRent() {
        Film expectedFilm = new Film();

        when(mockSession.createQuery("select f from Film f where f.id not in (select distinct film.id from Inventory)", Film.class)).thenReturn(mockQuery);
        when(mockQuery.setMaxResults(1)).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(expectedFilm);

        FilmDAO filmDAO = new FilmDAO(mockSessionFactory);
        Film retrievedFilm = filmDAO.getFirstAvailableFilmForRent();

        assertNotNull(retrievedFilm);
        assertEquals(expectedFilm, retrievedFilm);
    }
}
