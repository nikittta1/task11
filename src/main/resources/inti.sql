CREATE TABLE persons (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    price INT
);

CREATE TABLE purchase (
    id SERIAL PRIMARY KEY,
    person_id INT,
    product_id INT,
    purchase_price INT,
    FOREIGN KEY (person_id) REFERENCES persons(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
);

INSERT INTO persons (name) VALUES
    ('Denis'),
    ('Zhenya'),
    ('Vitya'),
    ('Vova'),
    ('Sasha');

INSERT INTO product (title, price) VALUES
    ('Cola', 150),
    ('Fanta', 130),
    ('Sprite', 100);

INSERT INTO product (title, price) VALUES
    ('Fanta', 130);