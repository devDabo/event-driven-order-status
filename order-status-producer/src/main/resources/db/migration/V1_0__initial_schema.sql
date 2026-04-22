-- V1__create_orders_table.sql

CREATE TABLE orders (
                        id UUID PRIMARY KEY,
                        status VARCHAR(50) NOT NULL,
                        expedited BOOLEAN NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL
);