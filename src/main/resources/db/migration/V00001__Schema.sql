CREATE TABLE IF NOT EXISTS products (
    id uuid PRIMARY KEY,
    name varchar,
    type varchar,
    start_date timestamptz,
    end_date timestamptz,
    description text,
    tariff uuid,
    tariff_version bigint,
    author uuid,
    version bigint NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS products_AUD (
    id uuid,
    name varchar,
    type varchar,
    start_date timestamptz,
    end_date timestamptz,
    description text,
    tariff uuid,
    tariff_version bigint,
    author uuid,
    version bigint NOT NULL DEFAULT 0,
    rev bigint NOT NULL,
    revtype smallint,
    PRIMARY KEY (id, rev)
);

CREATE TABLE IF NOT EXISTS revinfo (
    revtstmp bigint PRIMARY KEY,
    rev bigserial NOT NULL
);

ALTER SEQUENCE IF EXISTS revinfo_rev_seq RENAME TO revinfo_seq;
ALTER SEQUENCE IF EXISTS revinfo_seq INCREMENT 50;