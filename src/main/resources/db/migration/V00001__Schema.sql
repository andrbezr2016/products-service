CREATE TABLE IF NOT EXISTS products (
    id uuid,
    name varchar,
    type varchar,
    start_date timestamptz,
    end_date timestamptz,
    description text,
    tariff uuid,
    tariff_version bigint,
    author uuid,
    version bigint,
    PRIMARY KEY (id, version)
);