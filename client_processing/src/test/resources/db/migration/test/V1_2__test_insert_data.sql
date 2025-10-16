INSERT INTO users (username, password, role) VALUES
('testuser', 'password', 'MASTER');

INSERT INTO clients (client_id, user_id, first_name, middle_name, last_name, date_of_birth, document_type, document_id, document_prefix, document_suffix, client_role)
VALUES
('770100000001', 1, 'Иван', 'Иванович', 'Иванов', '1990-01-01', 'PASSPORT', '123456', 'AB', '77', 'CURRENT_CLIENT');