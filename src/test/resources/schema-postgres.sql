CREATE TABLE role (
	id int4 PRIMARY KEY,
	name varchar NULL,
);

CREATE TABLE user_profile (
	id uuid PRIMARY KEY,
	name varchar NULL,
	email varchar NOT NULL,
	password varchar NOT NULL,
	role_id int REFERENCES public.role (id),
);

CREATE TABLE task (
	id int PRIMARY KEY,
	title varchar NOT NULL,
	priority varchar NOT NULL,
	status varchar NULL,
	description varchar NULL,
	assigned_user uuid NULL,
    user_id uuid REFERENCES public.user_profile (id),
);

CREATE TABLE IF NOT EXISTS comment (
	id int PRIMARY KEY,
	commentary text,
	task_id int REFERENCES public.task (id),
);

INSERT INTO role VALUES(1, 'ADMIN')
INSERT INTO role VALUES(2, 'USER')
INSERT INTO role VALUES(3, 'VISITOR')

--password is pass
INSERT INTO user_profile VALUES("c7a673ca-a5f1-4a6b-984a-c5afea40d9b1", 'Peter', "email@mail.ru", "$2a$12$x.r3woysflsJtNSLtR6Z.ez1UEjWZ.KAEBp1lkTDtAyXh/HhAmFu.", 1)
INSERT INTO user_profile VALUES("5fb215cc-3c88-4261-9e39-bebccd498396", 'Kolya', "mail@mail.ru", "$2a$12$6o86zi3E11qdn49sPpSzf.qWvz2/ZvKobh4aPHdYCQcoaMW6/KQAa", 2)
INSERT INTO user_profile VALUES("09a8a6b0-f7a3-4042-b9d3-17cd951aa545", 'Naayil', "rtyemail@mail.ru", "$2a$12$dHhmzdFC.THUAwKVW3sDiuCZwXzRos7DH3JUjdgeBF04DDtCx5QFq", 1)

INSERT INTO task VALUES(1, "qwerty", "Высокий", "В ожидании", "c7a673ca-a5f1-4a6b-984a-c5afea40d9b1", "Сделайте побыстрее", null)
INSERT INTO task VALUES(2, "qwerty", "Высокий", "В ожидании", "c7a673ca-a5f1-4a6b-984a-c5afea40d9b1", "Сделайте побыстрее", null)
INSERT INTO task VALUES(3, "qwerty", "Высокий", "В ожидании", "c7a673ca-a5f1-4a6b-984a-c5afea40d9b1", "Сделайте побыстрее", null)
INSERT INTO task VALUES(4, "qwerty", "Высокий", "В ожидании", "c7a673ca-a5f1-4a6b-984a-c5afea40d9b1", "Сделайте побыстрее", null)

INSERT INTO comment VALUES(1, "comment", 1)
INSERT INTO comment VALUES(2, "comment", 1)
INSERT INTO comment VALUES(3, "comment", 2)
INSERT INTO comment VALUES(4, "comment", 3)
INSERT INTO comment VALUES(5, "comment", 2)
