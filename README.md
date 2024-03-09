There are bugs in database tables:
1. Extra column "original_language_id" - it might be removed;
2. In "film.special_features" - information is stored in two records in one column. Database design principle violated;
3. In "film.rating" - "NC-17", "PG-13" values are better written as: NC17 and PG13 (then will not need to convert in code);
4. After each lease event "film.rental_rate" need to add and calculate.
