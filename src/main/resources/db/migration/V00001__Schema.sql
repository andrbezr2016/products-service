CREATE TABLE IF NOT EXISTS products (
    id uuid NOT NULL,
    name varchar NOT NULL,
    type varchar NOT NULL,
    start_date timestamp NOT NULL,
    end_date timestamp,
    description text,
    tariff uuid,
    tariff_version bigint,
    author uuid NOT NULL,
    version bigint NOT NULL,
    state varchar NOT NULL,
    PRIMARY KEY (id, version)
);