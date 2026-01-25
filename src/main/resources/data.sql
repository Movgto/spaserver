DELETE FROM user_product;
DELETE FROM user_role;
DELETE FROM refresh_token;
DELETE FROM users;
DELETE FROM product;
DELETE FROM role;

INSERT INTO product (name, price, created_at) VALUES
    ('Masaje Reductor', 200, CURRENT_TIMESTAMP),
    ('Masaje Relajante', 300, CURRENT_TIMESTAMP),
    ('Masaje Facial', 200, CURRENT_TIMESTAMP);

INSERT INTO role (name) VALUES
    ('ROLE_ADMIN'), ('ROLE_USER');