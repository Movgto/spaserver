DELETE FROM users;
DELETE FROM product;
DELETE FROM user_product;

INSERT INTO product (name, price, created_at) VALUES
    ('Masaje Reductor', 200, CURRENT_TIMESTAMP),
    ('Masaje Relajante', 300, CURRENT_TIMESTAMP),
    ('Masaje Facial', 200, CURRENT_TIMESTAMP);