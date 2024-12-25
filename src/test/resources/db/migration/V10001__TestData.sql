INSERT INTO products (id, name, type, start_date, end_date, tariff, tariff_version, author, version, state) VALUES
('548ea2e0-bcef-4e12-b933-803a4de50106', 'Product 1 Create', 'CARD', '2020-01-01T12:00:00.000', '2020-01-01T13:00:00.000', null, null, '53d15658-5493-4828-80d9-f1c1f8eae252', 0, 'INACTIVE'),
('548ea2e0-bcef-4e12-b933-803a4de50106', 'Product 1 Update 1', 'CARD', '2020-01-01T13:00:00.000', '2020-01-01T14:00:00.000', '284add3b-e6f2-45f6-8a5e-1dfbed6a1f40', 2, '53d15658-5493-4828-80d9-f1c1f8eae252', 1, 'INACTIVE'),
('548ea2e0-bcef-4e12-b933-803a4de50106', 'Product 1 Update 2', 'CARD', '2020-01-01T14:00:00.000', null, '5c50cc6c-8600-48a3-acf8-a83298035857', 3, '53d15658-5493-4828-80d9-f1c1f8eae252', 2, 'ACTIVE');

INSERT INTO products (id, name, type, start_date, end_date, tariff, tariff_version, author, version, state) VALUES
('15cfb4c1-7083-475e-838d-4a1e696cf917', 'Product Del Create', 'LOAN', '2020-01-01T12:00:00.000', '2020-01-01T13:00:00.000', null, null, '53d15658-5493-4828-80d9-f1c1f8eae252', 0, 'INACTIVE'),
('15cfb4c1-7083-475e-838d-4a1e696cf917', 'Product Del Update 1', 'LOAN', '2020-01-01T13:00:00.000', null, '38abfc79-bf9d-400a-808e-79256c28c401', 1, '53d15658-5493-4828-80d9-f1c1f8eae252', 1, 'ACTIVE');

INSERT INTO products (id, name, type, start_date, end_date, tariff, tariff_version, author, version, state) VALUES
('272fff11-4790-4488-9564-7370724816c2', 'Product RollBack Create', 'LOAN', '2020-01-01T12:00:00.000', '2020-01-01T13:00:00.000', null, null, '53d15658-5493-4828-80d9-f1c1f8eae252', 0, 'INACTIVE'),
('272fff11-4790-4488-9564-7370724816c2', 'Product RollBack Update 1', 'LOAN', '2020-01-01T13:00:00.000', null, null, null, '53d15658-5493-4828-80d9-f1c1f8eae252', 1, 'DELETED');

INSERT INTO products (id, name, type, start_date, end_date, tariff, tariff_version, author, version, state) VALUES
('a8ddef4d-5942-42b8-9354-41a715e03b56', 'Product Del Create', 'LOAN', '2020-01-01T12:00:00.000', null, '7567c537-feca-4483-848f-cddddc4deb5a', 0, '53d15658-5493-4828-80d9-f1c1f8eae252', 0, 'ACTIVE');

INSERT INTO products (id, name, type, start_date, end_date, tariff, tariff_version, author, version, state) VALUES
('a929d899-1f06-418d-a002-77f4d6584676', 'Product RollBack Create', 'LOAN', '2020-01-01T12:00:00.000', null, null, null, '53d15658-5493-4828-80d9-f1c1f8eae252', 0, 'ACTIVE');