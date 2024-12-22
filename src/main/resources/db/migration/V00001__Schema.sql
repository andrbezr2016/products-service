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
    deleted boolean,
    PRIMARY KEY (id, version)
);

CREATE TABLE IF NOT EXISTS notifications (
    id bigserial PRIMARY KEY,
    tariff uuid NOT NULL,
    tariff_version bigint NOT NULL,
    processed_date timestamptz
);