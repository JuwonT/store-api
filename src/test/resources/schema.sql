create table products (
    id BIGINT PRIMARY KEY,
    title VARCHAR(100) UNIQUE,
    distribution VARCHAR(10),
    format VARCHAR(20),
    currency VARCHAR(3),
    price DECIMAL(10,2),
    release_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    store_name VARCHAR(100),
    product_group_title VARCHAR(100),
    product_group_release_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



CREATE TABLE products_seq (
    next_val BIGINT NOT NULL
);

INSERT INTO products_seq VALUES (1);

create table tags (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE tags_seq (
    next_val BIGINT NOT NULL
);

INSERT INTO tags_seq VALUES (1);

create table product_tags (
    product_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, tag_id),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);