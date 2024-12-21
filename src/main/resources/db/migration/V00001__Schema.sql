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
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS products_aud (
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
    rev bigint NOT NULL,
    revtype smallint NOT NULL,
    PRIMARY KEY (id, rev)
);

CREATE TABLE IF NOT EXISTS revinfo (
    rev bigserial NOT NULL,
    revtstmp bigint NOT NULL,
    PRIMARY KEY(rev)
);

ALTER SEQUENCE IF EXISTS revinfo_rev_seq RENAME TO revinfo_seq;
ALTER SEQUENCE IF EXISTS revinfo_seq INCREMENT 50;
