MERGE
INTO
	GENRE t
		USING (
VALUES 
		('Комедия'),
		('Драма'),
		('Мультфильм'),
		('Триллер'),
		('Документальный'),
		('Боевик')
	) S(NAME)
 ON
	t.NAME = s.NAME
	WHEN NOT MATCHED THEN
INSERT (NAME)
VALUES (S.NAME);

MERGE
INTO
	MPA t
		USING (
VALUES 
	    ('G'),
		('PG'),
		('PG-13'),
		('R'),
		('NC-17')
	) S(NAME)
 ON
	t.NAME = s.NAME
	WHEN NOT MATCHED THEN
INSERT (NAME)
VALUES (S.NAME);
