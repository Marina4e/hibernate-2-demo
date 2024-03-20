package com.javarush.operation;

import com.javarush.dao.ActorDAO;
import com.javarush.dao.CategoryDAO;
import com.javarush.dao.FilmDAO;
import com.javarush.dao.FilmTextDAO;
import com.javarush.dao.LanguageDAO;
import com.javarush.domain.Actor;
import com.javarush.domain.Category;
import com.javarush.domain.Feature;
import com.javarush.domain.Film;
import com.javarush.domain.FilmText;
import com.javarush.domain.Language;
import com.javarush.domain.Rating;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilmOperations {
    private final SessionFactory sessionFactory;
    private final LanguageDAO languageDAO;
    private final CategoryDAO categoryDAO;
    private final ActorDAO actorDAO;
    private final FilmDAO filmDAO;
    private final FilmTextDAO filmTextDAO;

    public FilmOperations(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.languageDAO = new LanguageDAO(sessionFactory);
        this.categoryDAO = new CategoryDAO(sessionFactory);
        this.actorDAO = new ActorDAO(sessionFactory);
        this.filmDAO = new FilmDAO(sessionFactory);
        this.filmTextDAO = new FilmTextDAO(sessionFactory);
    }

    public void newFilmWasMade() {
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
}
