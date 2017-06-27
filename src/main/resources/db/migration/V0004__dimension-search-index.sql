CREATE INDEX ON dimension USING GIN (id gin_trgm_ops);
CREATE INDEX ON dimension USING GIN (description gin_trgm_ops);