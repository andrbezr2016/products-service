CREATE TABLE IF NOT EXISTS products (
    id uuid,
    name varchar,
    type varchar,
    start_date timestamp,
    end_date timestamp,
    description text,
    tariff uuid,
    tariff_version bigint,
    author uuid,
    version bigint,
    deleted boolean,
    PRIMARY KEY (id, version)
);